package com.deapt.oneteambackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deapt.oneteambackend.common.result.StatusCode;
import com.deapt.oneteambackend.constant.UserConstant;
import com.deapt.oneteambackend.exception.BaseException;
import com.deapt.oneteambackend.model.domin.User;
import com.deapt.oneteambackend.service.UserService;
import com.deapt.oneteambackend.mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.*;
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
            log.info("账号或密码不能为空");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"账号或密码不能为空");
        }

        //校验密码和密码相同
        if (!checkPassword.equals(userPassword)){
            log.info("两次密码不一致");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"两次密码不一致");
        }

        //账号符合要求
        Matcher matcher = Pattern.compile(UserConstant.REG_EXP_ACCOUNT).matcher(userAccount);
        if (!matcher.find()){
            log.info("账号格式错误");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"账号格式错误");
        }

        //密码符合要求
        matcher = Pattern.compile(UserConstant.REG_EXP_PASSWORD).matcher(userPassword);
        if (!matcher.find()){
            log.info("密码格式错误");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"密码格式错误");
        }

        //账户不能重复
        //使用LambdaQueryWrapper支持Lambda表达式
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //使用::来引用方法（Lambda表达式），User::getUserAccount 指向实体类的 getUserAccount() 方法
        queryWrapper.eq(User::getUserAccount,userAccount);
        //执行查询并统计符合条件的数据条数
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            log.info("账户已存在");
            throw new BaseException(StatusCode.ACCOUNT_EXIST_ERROR,"用户账号重复");
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
            throw new BaseException(StatusCode.ERROR,"保存用户错误");
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
            log.info("账号或密码不能为空");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"账号或密码不能为空");
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
            log.info("账号或密码错误");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"账号或密码错误");
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
            log.info("用户权限不足");
            throw new BaseException(StatusCode.USER_NO_AUTH,"用户权限不足");
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
        if (notAdmin(request)) {
            log.info("用户权限不足");
            throw new BaseException(StatusCode.USER_NO_AUTH,"用户权限不足");
        }
        if (id <= 0){
            log.info("用户id不合法");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"用户id不合法");
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
        if (user == null){
            log.info("用户未登录");
            throw new BaseException(StatusCode.USER_NOT_LOGIN,"用户未登录");
        }
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
                .tags(originUser.getTags())
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

    /**
     * 根据标签搜索用户（内存过滤版）
     * @param tagNameList 用户拥有标签列表
     * @return 用户列表
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"标签列表不能为空");
        }

        //1. 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();

        //2. 在内存中判断是否包含要求的标签
        return userList.stream().filter((user -> {
            String tagsStr = user.getTags();
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }
            Set<String> tagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {}.getType());
            //判断是否为空， 设置默认值
            tagNameSet = Optional.ofNullable(tagNameSet).orElse(new HashSet<>());

            for (String tagName : tagNameList) {
                if (!tagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        })).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 根据标签搜索用户（sql查询版）
     * @param tagNameList 用户拥有标签列表
     * @return 用户列表
     */
    @Deprecated
    private List<User> searchUsersByTagsBySQL(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"标签列表不能为空");
        }

        //先进行一次空查询，排除掉数据库连接的时间
        userMapper.selectCount(null);

        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"标签列表不能为空");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }

        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }
}




