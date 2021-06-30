package com.chinasoft.gangjiantou.timer;

import com.chinasoft.gangjiantou.service.SyncService;
import com.github.wangran99.welink.api.client.openapi.OpenAPI;
import com.github.wangran99.welink.api.client.openapi.model.PreUploadRes;
import com.github.wangran99.welink.api.client.openapi.model.PredownloadReq;
import com.github.wangran99.welink.api.client.openapi.model.PreuploadReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Slf4j
@Component
public class SyncTimer {
    @Autowired
    SyncService syncService;
    @Autowired
    OpenAPI openAPI;


    //每隔1小时
//    @Scheduled(fixedRate = 24 * 60 * 60 * 1000, initialDelay = 2000)
    @Scheduled(cron = "0 0 0 * * ?")//每天晚上0点执行
    @Transactional
    public void scheduled() {
        syncService.delDepts();
        syncService.syncDepts();
        syncService.syncUsers();
//        uploadFile();
    }

    void uploadFile(){
//        File file = new File("D:/welink-sdk-java-2.0.0.jar");
//        PreuploadReq preuploadReq=new PreuploadReq();
//        preuploadReq.setName(file.getName());
//        preuploadReq.setSize(file.length());
//        PreUploadRes preUploadRes= openAPI.getPreUpload(preuploadReq);
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        //设置请求头
//        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType("multipart/form-data");
//        headers.setContentType(type);
//
//        //设置请求体，注意是LinkedMultiValueMap
//        FileSystemResource fileSystemResource = new FileSystemResource(file);
//        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
//        form.add("file", fileSystemResource);
//        form.add("filename",file.getName());
//
//        //用HttpEntity封装整个请求报文
//        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
//
//        String s = restTemplate.postForObject(preUploadRes.getUpload_url()+"?objectLength="+file.length(),
//                files, String.class);
//        System.out.println(s);
        PredownloadReq predownloadReq=new PredownloadReq();
        predownloadReq.setFile_sn("9547920-10105");
        openAPI.getPreDownload(predownloadReq);
    }
}
