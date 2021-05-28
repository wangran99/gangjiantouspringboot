package com.chinasoft.gangjiantou.controller;

import com.chinasoft.gangjiantou.dto.Callback;
import com.chinasoft.gangjiantou.dto.CallbackRes;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.chinasoft.gangjiantou.service.IFileService;
import com.github.wangran99.welink.api.client.openapi.model.UserBasicInfoRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 上传下载文件 前端控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-05-13
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    /**
     * 配置的上传路径
     */
    @Value("${file.path}")
    private String filePath;
    @Autowired
    IFileService fileService;
    @Autowired
    RedisService redisService;

    /**
     * 畅写office回调接口
     *
     * @param callback
     * @return
     */
    @PostMapping("/ping")
    public CallbackRes ping(@RequestBody Callback callback) {
        log.error(callback.toString());
        CallbackRes callbackRes = new CallbackRes();
        callbackRes.setError(0);
        return null;
//        return callbackRes;
    }

    /**
     * 多个文件上传
     *
     * @param files
     */
    @PostMapping("/uploadFile")
    public List<com.chinasoft.gangjiantou.entity.File> uploadFile(@RequestHeader("authCode") String authCode, @RequestParam("files") List<MultipartFile> files) throws IOException {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        List<com.chinasoft.gangjiantou.entity.File> list = new LinkedList();
        for (MultipartFile file : files) {
            //获取原文件名称和后缀
            String originalFilename = file.getOriginalFilename();
            // 获取文件后缀名
            String fil_extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String uuid = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();
            String path = String.format("{}-{}-{}", year, month, day);

            File file1 = new File(filePath + File.pathSeparator + path);
            if (!file1.exists())
                file1.mkdirs();
            file.transferTo(file1);
            //file.renameTo(new File(path))
            log.info("{} 上传成功！", originalFilename);
            com.chinasoft.gangjiantou.entity.File tempFile = new com.chinasoft.gangjiantou.entity.File();
            tempFile.setFileName(originalFilename);
            tempFile.setApprovalId(-1L);
            tempFile.setPath(File.pathSeparator + path + File.pathSeparator + uuid + "." + fil_extension);
            tempFile.setUserId(userBasicInfoRes.getUserId());
            tempFile.setUserName(userBasicInfoRes.getUserNameCn());
            list.add(tempFile);
        }
        fileService.saveBatch(list);
        return list;
    }
}
