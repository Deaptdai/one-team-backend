package com.deapt.oneteambackend.service;

import com.deapt.oneteambackend.model.domin.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author Deapt
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-04-22 10:06:04
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request 客户端的请求
     * @return 用户 user
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 查询用户
     * @param username 用户名
     * @param request 客户端的请求
     * @return 用户列表
     */
    List<User> search(String username,HttpServletRequest request);

    /**
     * 删除用户
     * @param id 用户id
     * @param request 客户端的请求
     * @return 成功与否
     */
    boolean delete(long id,HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser 用户
     * @return 脱敏用户
     */
    User getSafetyUser(User originUser);

    /**
     * 用户退出登录
     * @param request 客户端请求
     */
    void userLogout(HttpServletRequest request);
}
