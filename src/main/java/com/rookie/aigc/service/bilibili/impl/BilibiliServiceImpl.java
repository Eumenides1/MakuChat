package com.rookie.aigc.service.bilibili.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.rookie.aigc.component.bilibili.BilibiliSubtitle;
import com.rookie.aigc.domain.dto.bilibili.BiliBiliUnRead;
import com.rookie.aigc.domain.dto.bilibili.Subtitle;
import com.rookie.aigc.domain.dto.bilibili.Subtitles;
import com.rookie.aigc.domain.dto.bilibili.BilibiliVideoInfo;
import com.rookie.aigc.domain.dto.common.CommonSubtitleItem;
import com.rookie.aigc.domain.vo.req.BiliBiliReplyReq;
import com.rookie.aigc.domain.vo.req.UserConfig;
import com.rookie.aigc.domain.vo.req.VideoConfig;
import com.rookie.aigc.domain.vo.resp.BilibiliAtResp;
import com.rookie.aigc.domain.vo.resp.SubtitleFetchResult;
import com.rookie.aigc.domain.vo.resp.SubtitleResult;
import com.rookie.aigc.service.bilibili.BilibiliService;
import com.rookie.aigc.utils.SubtitleProcessor;
import com.rookie.aigc.utils.SubtitlePromptGenerator;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
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

    @Value("${maku-chat.bilibili.csrf}")
    private  String csrf;

    @Value("${ChatGLM.api-key}")
    private String apiKey; // 移除static修饰符

    public static final int BYTE_LIMIT = 6200;

    private static final String UNREAD_MSG_URL = "https://api.bilibili.com/x/msgfeed/unread";

    public static final String AT_LIST_URL = "https://api.bilibili.com/x/msgfeed/at?build=0&mobi_app=web";

    public static final String REPLY_URL = "https://api.bilibili.com/x/v2/reply/add";

    @Autowired
    private BilibiliSubtitle subtitle;

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

    @Override
    public BilibiliAtResp getAtMsgList() {
        HttpRequest request = HttpUtil.createGet(AT_LIST_URL);
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
        request.header("Host", "api.bilibili.com");
        request.header("Cookie", "SESSDATA=" + bilibiliSessionData);

        HttpResponse response = request.execute();
        JSON json = JSONUtil.parse(response.body());

        BilibiliAtResp bilibiliAtResp = JSONUtil.toBean(json.toString(), BilibiliAtResp.class);

        return bilibiliAtResp;
    }

    @Override
    public void reply(BiliBiliReplyReq req) {

        HttpRequest request = HttpUtil.createPost(REPLY_URL)
                .header("Cookie", "SESSDATA=" + bilibiliSessionData)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .form("type", req.getType())
                .form("oid", req.getOid())
                .form("root", req.getRoot())
                .form("message", req.getMessage())
                .form("csrf", csrf)
                .form("parent", req.getParent());

        HttpResponse response = request.execute();

        // 解析JSON响应
        JSONObject json = new JSONObject(response);

        int status = json.getInt("status");


    }

    @Override
    public String autoSummarize(String bvNum) {
        VideoConfig videoConfig = new VideoConfig();
        videoConfig.setVideoId(bvNum);
        videoConfig.setEnableStream(true);
        videoConfig.setShowTimestamp(true);
        videoConfig.setShowEmoji(true);
        videoConfig.setOutputLanguage("Chinese");
        videoConfig.setDetailLevel(1000);
        videoConfig.setSentenceNumber(7);
        videoConfig.setDetailLevel(2);
        videoConfig.setOutlineLevel(2);

        UserConfig userConfig = new UserConfig();
        userConfig.setUserKey("");
        userConfig.setShouldShowTimestamp(true);

        if (videoConfig.getVideoId() == null || videoConfig.getVideoId().isEmpty()) {
            // TODO 这里返回异常
        }

        // 拿到Bilibili 的字幕列表
        SubtitleResult subtitleResult = fetchBilibiliSubtitle(videoConfig.getVideoId(), null,userConfig.isShouldShowTimestamp());
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

        return invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();

    }
}
