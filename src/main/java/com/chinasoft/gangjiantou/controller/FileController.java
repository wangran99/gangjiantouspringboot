package com.chinasoft.gangjiantou.controller;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.chinasoft.gangjiantou.dto.Callback;
import com.chinasoft.gangjiantou.dto.CallbackRes;
import com.chinasoft.gangjiantou.dto.SaveDocDto;
import com.chinasoft.gangjiantou.entity.Apply;
import com.chinasoft.gangjiantou.entity.ApplyApprover;
import com.chinasoft.gangjiantou.exception.CommonException;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.chinasoft.gangjiantou.service.IApplyApproverService;
import com.chinasoft.gangjiantou.service.IApplyService;
import com.chinasoft.gangjiantou.service.IFileService;
import com.github.wangran99.welink.api.client.openapi.model.UserBasicInfoRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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
    IApplyService applyService;
    @Autowired
    IApplyApproverService applyApproverService;
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
        return null;
    }

    /**
     * 单个文件上传
     *
     * @param file
     */
    @PostMapping("/uploadFile")
    @Transactional
    public com.chinasoft.gangjiantou.entity.File uploadFile(@RequestHeader("authCode") String authCode, @RequestParam("file") MultipartFile file) throws IOException {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        //获取原文件名称和后缀
        String originalFilename = file.getOriginalFilename();
        originalFilename = URLDecoder.decode(originalFilename, "UTF-8");
        // 获取文件后缀名
        String fil_extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        String path = String.format("%d-%d-%d", year, month, day);

        File file1 = new File(filePath + File.separator + path);
        if (!file1.exists())
            file1.mkdirs();
        Path path1 = Paths.get(filePath + File.separator + path + File.separator + uuid + "." + fil_extension);
        byte[] bytes = file.getBytes();
        Files.write(path1, bytes);
        //file.renameTo(new File(path))
        log.info("{} 上传成功！", originalFilename);
        com.chinasoft.gangjiantou.entity.File tempFile = new com.chinasoft.gangjiantou.entity.File();
        tempFile.setFileName(originalFilename);
        tempFile.setApprovalId(-1L);
        tempFile.setType(fil_extension);
        tempFile.setPath(File.separator + path + File.separator + uuid + "." + fil_extension);
        tempFile.setUserId(userBasicInfoRes.getUserId());
        tempFile.setUuid(uuid);
        tempFile.setUserName(userBasicInfoRes.getUserNameCn());

        fileService.save(tempFile);
        return tempFile;
    }

    /**
     * 编辑文档后保存文档
     *
     * @param saveDocDto
     * @return
     */
    @PostMapping("save")
    @Transactional
    public boolean save(@RequestBody SaveDocDto saveDocDto, @RequestHeader("authCode") String authCode) {
        UserBasicInfoRes user = redisService.getUserInfo(authCode);
        log.error("savedto:" + saveDocDto.toString());
        log.error("user:" + user.toString());
        if (saveDocDto.getSourceFileId() < 1)
            throw new CommonException("文件对应的上传初始文件id错误。");
        Apply apply = applyService.getById(saveDocDto.getApplyId());
        ApplyApprover applyApprover =getActualApprove(apply);
        if (!applyApprover.getApproverId().equals(user.getUserId()))
            throw new CommonException("您当前无权编辑保存文档");
        com.chinasoft.gangjiantou.entity.File file = fileService.getById(saveDocDto.getSourceFileId());
        com.chinasoft.gangjiantou.entity.File currentFile = fileService.lambdaQuery().eq(com.chinasoft.gangjiantou.entity.File::getSource, saveDocDto.getSourceFileId())
                .eq(com.chinasoft.gangjiantou.entity.File::getApprovalId, applyApprover.getId()).one();
        if (currentFile == null) {
            LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();
            String path = String.format("%d-%d-%d", year, month, day);

            File file1 = new File(filePath + File.separator + path);
            if (!file1.exists())
                file1.mkdirs();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");

            com.chinasoft.gangjiantou.entity.File tempFile = new com.chinasoft.gangjiantou.entity.File();
            tempFile.setFileName(file.getFileName());
            tempFile.setType(file.getType());
            tempFile.setPath(File.separator + path + File.separator + uuid + "." + file.getType());
            tempFile.setUuid(uuid);
            tempFile.setSource(saveDocDto.getSourceFileId());
            tempFile.setUserId(user.getUserId());
            tempFile.setUserName(user.getUserNameCn());
            tempFile.setApplyId(file.getApplyId());
            tempFile.setApprovalId(applyApprover.getId());
            fileService.save(tempFile);
            HttpUtil.downloadFile(saveDocDto.getUrl(), filePath + File.separator + path + File.separator + uuid + "." + file.getType());
        } else {
            File file2 = new File(filePath + currentFile.getPath());
            if (file2.exists())
                file2.delete();
            LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();
            String path = String.format("%d-%d-%d", year, month, day);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            currentFile.setUuid(uuid);
            currentFile.setPath(File.separator + path + File.separator + uuid + "." + file.getType());
            fileService.updateById(currentFile);
            HttpUtil.downloadFile(saveDocDto.getUrl(), filePath + currentFile.getPath());
        }
        return true;
    }

    /**
     * 下载文件
     *
     * @param uuid     文件的uuid
     * @param response
     */
    @GetMapping("download")
    public void download(String uuid, HttpServletResponse response) throws UnsupportedEncodingException {
        com.chinasoft.gangjiantou.entity.File file = fileService.lambdaQuery().eq(com.chinasoft.gangjiantou.entity.File::getUuid, uuid).one();
        File file1 = new File(filePath + file.getPath());
        if (!file1.exists())
            throw new CommonException("文件不存在");
        // 获得文件的长度
        response.setHeader("Content-Length", String.valueOf(file1.length()));
        response.setContentType("application/octet-stream");
        String fileName = URLEncoder.encode(file.getFileName(), "UTF-8");
        if (file.getSource() == -1)
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        else {
            int index = fileName.lastIndexOf(".");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName.substring(0, index) +
                    "_" + file.getUserName() + "." + file.getType());
        }
        // 实现文件下载
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file1);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            System.out.println("Download the file successfully!");
        } catch (Exception e) {
            log.error("io exception.", e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    private ApplyApprover getActualApprove(Apply apply) {
        if (apply.getEndTime() != null)
            return null;
        List<ApplyApprover> applyApproverList = applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, apply.getId())
                .orderByAsc(ApplyApprover::getId).list();
        for (ApplyApprover applyApprover : applyApproverList)
            if (applyApprover.getStatus() == 0)
                return applyApprover;
        return null;
    }

}
