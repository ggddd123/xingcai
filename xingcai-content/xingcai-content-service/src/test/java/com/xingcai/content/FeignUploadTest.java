package com.xingcai.content;

import com.xingcai.content.config.MultipartSupportConfig;
import com.xingcai.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@SpringBootTest
public class FeignUploadTest {


    @Autowired
    MediaServiceClient mediaServiceClient;

    @Test
    public void test(){

        //将file转为multipartfile
        File file = new File("E:\\120.html");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);


        mediaServiceClient.uploadFile(multipartFile,"course/120.html");
    }
}
