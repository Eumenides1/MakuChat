package com.rookie.aigc.service;

import com.rookie.aigc.service.bilibili.BilibiliService;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */
@SpringBootTest
public class BiliBiliServiceTest {

    @Resource
    private BilibiliService bilibiliService;


    public void testFetchBilibiliVideoInfo(){
        String bv1tC41157Gr = bilibiliService.fetchBilibiliSubtitleUrls("BV1tC41157Gr", null);
        System.out.println(bv1tC41157Gr);

    }

}
