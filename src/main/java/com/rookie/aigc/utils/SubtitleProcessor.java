package com.rookie.aigc.utils;

import com.rookie.aigc.domain.dto.bilibili.SubtitleItem;
import com.rookie.aigc.domain.dto.common.CommonSubtitleItem;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */

public class SubtitleProcessor {
    private static final int LIMIT_COUNT = 6200; // 字节限制

    public static String getSmallSizeTranscripts(List<CommonSubtitleItem> newTextData, int byteLimit) {
        newTextData.sort((a, b) -> Integer.compare(a.getIndex(), b.getIndex()));
        String text = newTextData.stream()
                .map(CommonSubtitleItem::getText)
                .collect(Collectors.joining(" "));

        int byteLength = text.getBytes(StandardCharsets.UTF_8).length;

        if (byteLength > byteLimit) {
            List<CommonSubtitleItem> filteredData = filterHalfRandomly(newTextData);
            return getSmallSizeTranscripts(filteredData, byteLimit);
        } else {
            return text;
        }
    }

    private static List<CommonSubtitleItem> filterHalfRandomly(List<CommonSubtitleItem> arr) {
        List<CommonSubtitleItem> filteredArr = new ArrayList<>();
        int halfLength = arr.size() / 2;
        Random random = new Random();

        while (filteredArr.size() < halfLength) {
            int index = random.nextInt(arr.size());
            if (!filteredArr.contains(arr.get(index))) {
                filteredArr.add(arr.get(index));
            }
        }

        return filteredArr;
    }

    public static List<CommonSubtitleItem> reduceSubtitleTimestamp(List<SubtitleItem> subtitles, boolean shouldShowTimestamp) {
        final int TOTAL_GROUP_COUNT = 30;
        final int MINIMUM_COUNT_ONE_GROUP = 7;

        int eachGroupCount = subtitles.size() > TOTAL_GROUP_COUNT ?
                subtitles.size() / TOTAL_GROUP_COUNT :
                MINIMUM_COUNT_ONE_GROUP;

        List<CommonSubtitleItem> reducedSubtitles = new ArrayList<>();
        CommonSubtitleItem groupItem = null;

        for (int i = 0; i < subtitles.size(); i++) {
            SubtitleItem subtitleItem = subtitles.get(i);
            int groupIndex = i / eachGroupCount;

            if (i % eachGroupCount == 0 || groupItem == null) {
                if (groupItem != null) {
                    reducedSubtitles.add(groupItem);
                }
                groupItem = new CommonSubtitleItem(
                        shouldShowTimestamp ? subtitleItem.getFrom() + " - " : "",
                        groupIndex,
                        subtitleItem.getFrom()
                );
            }

            groupItem.setText(groupItem.getText() + subtitleItem.getContent() + " ");
        }

        if (groupItem != null) {
            reducedSubtitles.add(groupItem);
        }

        return reducedSubtitles;
    }
    public static String limitTranscriptByteLength(String str) {
        if (str == null) {
            return null;
        }


        byte[] utf8Bytes = str.getBytes(StandardCharsets.UTF_8);
        if (utf8Bytes.length <= LIMIT_COUNT) {
            return str;
        }

        int newLength = (int) ((double) LIMIT_COUNT / utf8Bytes.length * str.length());
        return str.substring(0, newLength);
    }
}
