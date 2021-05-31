package com.chinasoft.gangjiantou.service.impl;

import com.chinasoft.gangjiantou.entity.File;
import com.chinasoft.gangjiantou.mapper.FileMapper;
import com.chinasoft.gangjiantou.service.IFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 上传/修订文件信息表 服务实现类
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

}
