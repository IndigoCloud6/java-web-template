package com.indigo.template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.template.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * User Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
