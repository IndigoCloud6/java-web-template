package com.indigo.template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.template.entity.Config;
import org.apache.ibatis.annotations.Mapper;

/**
 * Config Mapper
 * 系统配置表Mapper接口
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {
}
