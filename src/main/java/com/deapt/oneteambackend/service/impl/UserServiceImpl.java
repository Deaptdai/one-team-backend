package com.deapt.oneteambackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deapt.oneteambackend.model.domain.User;
import com.deapt.oneteambackend.service.UserService;
import com.deapt.oneteambackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Acer
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-04-22 10:06:04
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




