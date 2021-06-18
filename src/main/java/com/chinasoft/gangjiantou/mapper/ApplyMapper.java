package com.chinasoft.gangjiantou.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.ApplyPendingDto;
import com.chinasoft.gangjiantou.dto.CcDto;
import com.chinasoft.gangjiantou.dto.ShiftDto;
import com.chinasoft.gangjiantou.entity.Apply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.wangran99.welink.api.client.openapi.model.UserBasicInfoRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 申请表 Mapper 接口
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
public interface ApplyMapper extends BaseMapper<Apply> {

  Page<Apply>  pendingApply(Page<Apply> page, @Param("userId")String userId,@Param("applyPendingDto") ApplyPendingDto applyPendingDto);

  Page<Apply>  queryCC(Page<Apply> page,String userId, CcDto ccDto);

  Page<Apply>  queryShift(Page<Apply> page, @Param("userId")String userId,@Param("shiftDto") ShiftDto shiftDto);

}
