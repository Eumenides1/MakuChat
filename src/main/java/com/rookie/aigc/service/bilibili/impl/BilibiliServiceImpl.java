package com.rookie.aigc.service.bilibili.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.rookie.aigc.component.bilibili.BilibiliSubtitle;
import com.rookie.aigc.domain.dto.bilibili.BiliBiliUnRead;
import com.rookie.aigc.domain.dto.bilibili.Subtitle;
import com.rookie.aigc.domain.dto.bilibili.Subtitles;
import com.rookie.aigc.domain.dto.bilibili.BilibiliVideoInfo;
import com.rookie.aigc.domain.dto.common.CommonSubtitleItem;
import com.rookie.aigc.domain.vo.resp.SubtitleFetchResult;
import com.rookie.aigc.domain.vo.resp.SubtitleResult;
import com.rookie.aigc.service.bilibili.BilibiliService;
import com.rookie.aigc.utils.SubtitleProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */
@Service
public class BilibiliServiceImpl implements BilibiliService {

    @Value("${bilibili.session-token}")
    private  String bilibiliSessionData;

    private String UNREAD_MSG_URL = "https://api.bilibili.com/x/msgfeed/unread";

    @Autowired
    private BilibiliSubtitle subtitle;


    @Override
    public SubtitleResult fetchBilibiliSubtitle(String videoId, String pageNumber, Boolean shouldShowTimestamp) {

        String jsonStr = subtitle.fetchBilibiliSubtitleUrls(videoId, pageNumber);
        BilibiliVideoInfo res = JSONUtil.toBean(jsonStr, BilibiliVideoInfo.class);

        String title = res.getTitle();
        String desc = res.getDesc();
        String dynamic = res.getDynamic();
        Subtitle subtitle = res.getSubtitle();

        String descriptionText = (desc != null && dynamic != null) ? desc + " " + dynamic : null;

        if (subtitle.getSubtitles() == null || subtitle.getSubtitles().isEmpty()) {
            return new SubtitleResult(title, null, descriptionText);
        }

        Subtitles betterSubtitle = subtitle.getSubtitles().stream()
                .filter(sub -> "zh-CN".equals(sub.getLan()))
                .findFirst()
                .orElse(subtitle.getSubtitles().get(0));

        String subtitleUrl = betterSubtitle.getSubtitleUrl();
        if (subtitleUrl != null && subtitleUrl.startsWith("//")) {
            subtitleUrl = "https:" + subtitleUrl;
        }

        System.out.println("subtitle_url: " + subtitleUrl);
        HttpRequest request = HttpUtil.createGet(subtitleUrl);
        HttpResponse execute = request.execute();

        JSON parse = JSONUtil.parse(execute.body());

        SubtitleFetchResult bean = JSONUtil.toBean(parse.toString(), SubtitleFetchResult.class);
        List<CommonSubtitleItem> commonSubtitleItems = SubtitleProcessor.reduceSubtitleTimestamp(bean.getBody(), shouldShowTimestamp);

        return new SubtitleResult(title, commonSubtitleItems, descriptionText);
    }

    @Override
    public BiliBiliUnRead getRuntimeAtNum() {

        HttpRequest request = HttpUtil.createGet(UNREAD_MSG_URL);
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
        request.header("Host", "api.bilibili.com");
        request.header("Cookie", "SESSDATA=" + bilibiliSessionData);

        HttpResponse response = request.execute();
        JSON json = JSONUtil.parse(response.body());

        BiliBiliUnRead biliBiliUnRead = JSONUtil.toBean(json.toString(), BiliBiliUnRead.class);

        return biliBiliUnRead;
    }
}
