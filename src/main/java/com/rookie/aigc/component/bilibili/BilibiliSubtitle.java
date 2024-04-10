package com.rookie.aigc.component.bilibili;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.rookie.aigc.config.BilibiliProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */
@Component
public class BilibiliSubtitle {

    @Value("${bilibili.session-token}")
    private  String bilibiliSessionData;


    public String fetchBilibiliSubtitleUrls(String videoId, String pageNumber) {


        // 构造请求URL
        String params = videoId.startsWith("av") ? "?aid=" + videoId.substring(2) : "?bvid=" + videoId;
        String requestUrl = "https://api.bilibili.com/x/web-interface/view" + params;

        // 使用Hutool构建请求
        HttpRequest request = HttpUtil.createGet(requestUrl);
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
        request.header("Host", "api.bilibili.com");
        request.header("Cookie", "SESSDATA=" + bilibiliSessionData);

        // 发送请求并获取响应
        HttpResponse response = request.execute();

        // 返回响应体作为字符串
        JSON json = JSONUtil.parse(response.body());

        if (pageNumber != null || hasMultiplePages(json)) {
            Map<String, Object> data = json.getByPath("data", Map.class);
            List<Map<String, Object>> pages = (List<Map<String, Object>>) data.get("pages");
            String aid = data.get("aid").toString();

            Map<String, Object> page = findPage(pages, pageNumber);
            String cid = page.get("cid").toString();

            String pageUrl = String.format("https://api.bilibili.com/x/player/v2?aid=%s&cid=%s", aid, cid);

            HttpResponse pageResponse = HttpRequest.get(pageUrl)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 ...")
                    .header("Cookie", "SESSDATA=" + bilibiliSessionData)
                    .execute();

            JSON pageJson = JSONUtil.parse(pageResponse.body());

            Map<String, Object> subtitleInfo = pageJson.getByPath("data.subtitle", Map.class);
            data.put("subtitle", subtitleInfo);
            json.putByPath("data", data);
        }
        return json.getByPath("data").toString();
    }
    private boolean hasMultiplePages(JSON json) {
        List<Map<String, Object>> pages = json.getByPath("data.pages", List.class);
        return pages != null && !pages.isEmpty();
    }

    private Map<String, Object> findPage(List<Map<String, Object>> pages, String pageNumber) {
        int pageNum = pageNumber != null ? Integer.parseInt(pageNumber) : 1;
        for (Map<String, Object> page : pages) {
            if (pageNum == (int) page.get("page")) {
                return page;
            }
        }
        return null; // 或者处理错误情况
    }

}
