package com.rookie.aigc.utils;

import com.rookie.aigc.domain.vo.req.VideoConfig;

import java.nio.charset.StandardCharsets;

/**
 * @author eumenides
 * @description 生成咒语
 * @date 2024/4/10
 */
public class SubtitlePromptGenerator {

    private static final String DEFAULT_LANGUAGE = "English";

    public static String getUserSubtitlePrompt(String title, String transcript, VideoConfig videoConfig) {
        String videoTitle = title.replaceAll("\n+", " ").trim();
        String videoTranscript = SubtitleProcessor.limitTranscriptByteLength(transcript).replaceAll("\n+", " ").trim();
        String language = videoConfig.getOutputLanguage() != null ? videoConfig.getOutputLanguage() : DEFAULT_LANGUAGE;
        int sentenceCount = videoConfig.getSentenceNumber() != null ? videoConfig.getSentenceNumber() : 7;
        String emojiTemplateText = videoConfig.getShowEmoji() ? "[Emoji] " : "";
        String emojiDescriptionText = videoConfig.getShowEmoji() ? "Choose an appropriate emoji for each bullet point. " : "";
        boolean shouldShowAsOutline = videoConfig.getOutlineLevel() > 1;
        int wordsCount = videoConfig.getDetailLevel() != null ? (int)(videoConfig.getDetailLevel() / 100.0 * 2) : 15;
        String outlineTemplateText = shouldShowAsOutline ? "\n    - Child points" : "";
        String outlineDescriptionText = shouldShowAsOutline
                ? "Use the outline list, which can have a hierarchical structure of up to " + videoConfig.getOutlineLevel() + " levels. "
                : "";

        String prompt = String.format("Your output should use the following template:\n## Summary\n## Highlights\n- %sBulletpoint%s\n\nYour task is to summarise the text I have given you in up to %d concise bullet points, starting with a short highlight, each bullet point is at least %d words. %s%sUse the text above: {{Title}} {{Transcript}}.\n\nReply in %s Language.",
                emojiTemplateText, outlineTemplateText, sentenceCount, wordsCount, outlineDescriptionText, emojiDescriptionText, language);
        prompt = prompt.replace("{{Title}}", videoTitle)
                .replace("{{Transcript}}", videoTranscript);
        return prompt;
    }

}

