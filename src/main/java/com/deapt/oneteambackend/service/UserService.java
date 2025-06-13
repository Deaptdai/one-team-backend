package com.deapt.oneteambackend.service;

import com.deapt.oneteambackend.model.domin.User;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletRequest;

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
     * 判断用户是否是管理员
     * @param request 客户端请求
     * @return 如果是管理员则返回 true
     */
    boolean notAdmin(HttpServletRequest request);

    /**
     * 判断用户是否不是管理员
     * @param loginUser 登录用户
     * @return 如果不是管理员则返回 true
     */
    boolean notAdmin(User loginUser);

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

    /**
     * 根据标签搜索用户
     * @param tagNameList 用户拥有标签列表
     * @return 用户列表
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 更新用户信息
     *
     * @param user 当前用户信息
     * @return 大于一的整数表示更新成功
     */
    int updateUser(User user,User loginUser);

    /**
     * 获取当前登录用户
     *
     * @param request 客户端请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);
}
