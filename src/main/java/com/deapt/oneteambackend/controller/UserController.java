package com.deapt.oneteambackend.controller;

import com.deapt.oneteambackend.model.domin.User;
import com.deapt.oneteambackend.model.domin.request.UserLoginRequest;
import com.deapt.oneteambackend.model.domin.request.UserRegisterRequest;
import com.deapt.oneteambackend.model.domin.result.Result;
import com.deapt.oneteambackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Deapt
 * @description 用户表示层
 * @since 2025/4/26 10:22
 */

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        log.info("用户注册:{}",userRegisterRequest);
        //1. 判断请求体是否为空
        if (userRegisterRequest == null){
            return null;
        }
        //2. 获取所需数据
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isBlank(userAccount)){
            log.info("请输入用户名");
            // TODO: 修改为自定义异常
            return null;
        }else if(StringUtils.isBlank(userPassword)){
            log.info("请输入密码");
            return null;
        }else if (StringUtils.isBlank(checkPassword)){
            log.info("请再次确认密码");
            return null;
        }

        long id = userService.userRegister(userAccount, userPassword, checkPassword);
        return Result.success(id);
    }

    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody UserLoginRequest userRegisterRequest, HttpServletRequest request){
        log.info("用户登录:{}",userRegisterRequest);
        //1. 判断请求体是否为空
        if (userRegisterRequest == null){
            return null;
        }
        //2. 获取所需数据
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();

        if (StringUtils.isBlank(userAccount)){
            log.info("请输入用户名");
            return null;
        }else if(StringUtils.isBlank(userPassword)){
            log.info("请输入密码");
            return null;
        }

        User user = userService.userLogin(userAccount, userPassword, request);
        return Result.success(user);
    }

}
