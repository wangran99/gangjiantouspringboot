package com.chinasoft.gangjiantou.controller;

import com.chinasoft.gangjiantou.dto.Callback;
import com.chinasoft.gangjiantou.dto.CallbackRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 上传文件 前端控制器
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

    @PostMapping("/ping")
    public CallbackRes ping(@RequestBody Callback callback)  {
        log.error(callback.toString());
        CallbackRes callbackRes=new CallbackRes();
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
    public void uploadFile(@RequestParam("files") List<MultipartFile> files) throws IOException {
        StringBuffer buffer = new StringBuffer();
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

            File file1 = new File(filePath + path);
            boolean mkdirs = file1.mkdirs();
            log.info("文件夹{}创建{}", file1.getAbsolutePath(), mkdirs ? "成功" : "失败");
            file.transferTo(file1);
            //file.renameTo(new File(path))
            log.info("{} 上传成功！", originalFilename);

            buffer.append(path);

        }

    }
}
