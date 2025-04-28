package com.deapt.oneteambackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deapt.oneteambackend.constant.UserConstant;
import com.deapt.oneteambackend.model.domin.User;
import com.deapt.oneteambackend.service.UserService;
import com.deapt.oneteambackend.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
* @author Deapt
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-04-22 10:06:04
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 用户注册id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1. 校验
        //判断是否有空值
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return -1;
        }

        //校验密码和密码相同
        if (!checkPassword.equals(userPassword)){
            log.info("密码不同");
            return -1;
        }

        //账号符合要求
        Matcher matcher = Pattern.compile(UserConstant.REG_EXP_ACCOUNT).matcher(userAccount);
        if (!matcher.find()){
            log.info("账号不符合要求");
            return -1;
        }

        //密码符合要求
        matcher = Pattern.compile(UserConstant.REG_EXP_PASSWORD).matcher(userPassword);
        if (!matcher.find()){
            log.info("密码不符合要求");
            return -1;
        }

        //账户不能重复
        //使用LambdaQueryWrapper支持Lambda表达式
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //使用::来引用方法（Lambda表达式），User::getUserAccount 指向实体类的 getUserAccount() 方法
        queryWrapper.eq(User::getUserAccount,userAccount);
        //执行查询并统计符合条件的数据条数
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            log.info("账号重复");
            return -1;
        }

        //2. 加密
        //密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());

        //3. 插入数据
        User user = new User();
        //user.setUsername(userAccount); //默认用户名与账户名相同
        //自动生成用户编码(0-8位不重复的数字)
        String userCode = String.valueOf(System.currentTimeMillis());
        user.setUserCode(userCode);
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveUser = this.save(user);
        if (!saveUser){
            log.info("数据插入失败");
            return -1;
        }

        return user.getId();
    }

    /**
     * 用户登录
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request 客户端的请求
     * @return 登录用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1. 校验
        //判断是否有空值
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }

        //2. 加密
        //密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());

        //3.查询
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserAccount,userAccount);
        lambdaQueryWrapper.eq(User::getUserPassword,encryptPassword);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        if (user == null){
            log.info("账号或密码错误，请重新输入");
            return null;
        }

        //3.用户数据脱敏（隐藏敏感信息）,使用链式构造器
        User safetyUser = getSafetyUser(user);

        //4. 记录用户的登录状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    /**
     * 查询用户
     * @param username 用户名
     * @param request 客户端的请求
     * @return 用户列表
     */
    @Override
    public List<User> search(String username,HttpServletRequest request) {
        if (notAdmin(request)){
            return new ArrayList<>();
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        List<User> userList = this.list(queryWrapper);

        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 删除用户
     * @param id 用户id
     * @param request 客户端的请求
     * @return 是否成功
     */
    @Override
    public boolean delete(long id,HttpServletRequest request) {
        if (notAdmin(request) || id <= 0) {
            return false;
        }

        return this.removeById(id);
    }

    /**
     * 检验权限
     * @param request 请求
     * @return 是否为管理员
     */
    private boolean notAdmin(HttpServletRequest request){
        //鉴权，仅管理员可查询
        Object userObject = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObject;
        if (user == null)
            return false;
        return !Objects.equals(user.getUserRole(), UserConstant.ADMIN_ROLE);
    }

    /**
     * 用户脱敏
     * @param originUser 用户
     * @return 脱敏用户
     */
    @Override
    public User getSafetyUser(User originUser){
        return User.builder()
                .id(originUser.getId())
                .username(originUser.getUsername())
                .userAccount(originUser.getUserAccount())
                .avatarUrl(originUser.getAvatarUrl())
                .gender(originUser.getGender())
                .phone(originUser.getPhone())
                .email(originUser.getEmail())
                .userRole(originUser.getUserRole())
                .userCode(originUser.getUserCode())
                .userStatus(originUser.getUserStatus())
                .createTime(originUser.getCreateTime())
                .build();
    }

    /**
     * 用户退出登录
     * @param request 客户端请求
     */
    @Override
    public void userLogout(HttpServletRequest request) {
        //移除登陆态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
    }

}




