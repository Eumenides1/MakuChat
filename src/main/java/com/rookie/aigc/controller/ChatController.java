package com.rookie.aigc.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.rookie.aigc.domain.vo.req.SummarizeReq;
import com.rookie.aigc.domain.vo.req.UserConfig;
import com.rookie.aigc.domain.vo.req.VideoConfig;
import com.rookie.aigc.domain.vo.resp.SubtitleResult;
import com.rookie.aigc.service.bilibili.BilibiliService;
import com.rookie.aigc.utils.SubtitleProcessor;
import com.rookie.aigc.utils.SubtitlePromptGenerator;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Collections;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */
@RestController
@RequestMapping("/v1/chat")
public class ChatController {

    public static final int BYTE_LIMIT = 6200;
    @Autowired
    private BilibiliService bilibiliService;

    @Value("${ChatGLM.api-key}")
    private String apiKey; // 移除static修饰符

    private ClientV4 client; // 移除static修饰符

    @PostConstruct
    private void init() {
        client = new ClientV4.Builder(apiKey).build();
    }

    private static final ObjectMapper mapper = defaultObjectMapper();

    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.addMixIn(ChatFunction.class, ChatFunctionMixIn.class);
        mapper.addMixIn(ChatCompletionRequest.class, ChatCompletionRequestMixIn.class);
        mapper.addMixIn(ChatFunctionCall.class, ChatFunctionCallMixIn.class);
        return mapper;
    }


    @PostMapping("")
    public void bilibiliTest(@RequestBody SummarizeReq req){
        VideoConfig videoConfig = req.getVideoConfig();
        UserConfig userConfig = req.getUserConfig();

        if (videoConfig.getVideoId() == null || videoConfig.getVideoId().isEmpty()) {
            // TODO 这里返回异常
        }

        // 拿到Bilibili 的字幕列表
        SubtitleResult subtitleResult = bilibiliService.fetchBilibiliSubtitle(videoConfig.getVideoId(), null,userConfig.isShouldShowTimestamp());
        if (subtitleResult.getSubtitlesArray().isEmpty() && subtitleResult.getDescriptionText().isEmpty()) {
            // TODO 这里返回异常
        }

        String inputText = SubtitleProcessor.getSmallSizeTranscripts(subtitleResult.getSubtitlesArray(), BYTE_LIMIT);

        String prompt = SubtitlePromptGenerator.getUserSubtitlePrompt(subtitleResult.getTitle(), inputText,videoConfig);

        System.out.println(prompt);

        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(Collections.singletonList(chatMessage))
                .build();

        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        try {
            System.out.println("model output:" + mapper.writeValueAsString(invokeModelApiResp));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }




}
