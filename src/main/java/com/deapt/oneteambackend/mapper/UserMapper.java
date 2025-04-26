package com.deapt.oneteambackend.mapper;

import com.deapt.oneteambackend.model.domin.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Deapt
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2025-04-22 10:06:04
* @Entity generator.domain.User
*/

@Mapper
public interface UserMapper extends BaseMapper<User> {

}




