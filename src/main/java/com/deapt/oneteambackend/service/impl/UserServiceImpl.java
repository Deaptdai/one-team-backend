package com.deapt.oneteambackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deapt.oneteambackend.common.result.Result;
import com.deapt.oneteambackend.common.result.StatusCode;
import com.deapt.oneteambackend.constant.UserConstant;
import com.deapt.oneteambackend.exception.BaseException;
import com.deapt.oneteambackend.model.domin.User;
import com.deapt.oneteambackend.model.vo.UserVO;
import com.deapt.oneteambackend.service.UserService;
import com.deapt.oneteambackend.mapper.UserMapper;
import com.deapt.oneteambackend.utils.AlgorithmUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import io.swagger.models.auth.In;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

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

        //3.先判断是否为管理员，提升管理员登录速度
        User admin = userMapper.selectById(1);
        if (admin.getUserAccount().equals(userAccount) && admin.getUserPassword().equals(encryptPassword)) {
            //如果是管理员，直接返回管理员信息
            //用户数据脱敏（隐藏敏感信息）,使用链式构造器
            User safetyUser = getSafetyUser(admin);

            //记录用户的登录状态
            request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);
            return getSafetyUser(safetyUser);
        }

        //4.查询普通用户
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
    @Override
    public boolean notAdmin(HttpServletRequest request){
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
     * 检验权限
     * @param loginUser 登录用户
     * @return 是否为管理员
     */
    @Override
    public boolean notAdmin(User loginUser){
        //鉴权，仅管理员可查询
        if (loginUser == null){
            log.info("用户未登录");
            throw new BaseException(StatusCode.USER_NOT_LOGIN,"用户未登录");
        }
        return !Objects.equals(loginUser.getUserRole(), UserConstant.ADMIN_ROLE);
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
                .userProfile(originUser.getUserProfile())
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
     * 更新用户信息
     * @param user 当前用户信息
     * @return 更新结果
     */
    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BaseException(StatusCode.PARAMETER_ERROR, "用户id不合法");
        }
        // 如果传入的用户只有id，则不更新，直接报错
        if (user.getUserAccount() == null && user.getUsername() == null && user.getAvatarUrl() == null &&
                user.getGender() == null && user.getPhone() == null && user.getEmail() == null &&
                user.getUserRole() == null && user.getUserCode() == null && user.getUserStatus() == null &&
                user.getTags() == null && user.getUserProfile() == null) {
            throw new BaseException(StatusCode.PARAMETER_ERROR, "没有需要更新的用户信息");
        }
        // 如果是管理员，则可以更新任意用户信息,如果是普通用户，则只能更新自己的信息
        if (notAdmin(loginUser) && !Objects.equals(user.getId(), loginUser.getId())) {
            throw new BaseException(StatusCode.USER_NO_AUTH, "无权限更新用户信息");
        }

        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BaseException(StatusCode.PARAMETER_ERROR, "用户不存在");
        }
        // 更新用户信息
        return userMapper.updateById(user);
    }

    /**
     * 获取当前登录用户
     * @param request 客户端请求
     * @return 当前登录用户
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BaseException(StatusCode.REQUEST_IS_NULL, "请求不能为空");
        }
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BaseException(StatusCode.USER_NOT_LOGIN, "用户未登录");
        }
        return (User) userObj;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        //用户列表=>相似度
        List<Pair<User, Long>> userDistanceList = new ArrayList<>();
        for (User user : userList) {
            String userTags = user.getTags();
            // 无标签或者当前用户
            if (StringUtils.isBlank(userTags) || Objects.equals(user.getId(), loginUser.getId())) {
                continue; // 如果用户没有标签，则跳过
            }
            List<String> userTageList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            long distance = AlgorithmUtils.minDistance(tagList, userTageList);
            userDistanceList.add(new Pair<>(user, distance));
        }
        List<Pair<User, Long>> topUserList = userDistanceList.stream().sorted((a, b) -> (int) (a.getValue() - b.getValue())).limit(num).collect(Collectors.toList());
        //原本顺序userId列表
        List<Long> idList = topUserList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList(); // 如果没有匹配的用户，则返回空列表
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", idList);
        Map<Long,List<User>> userIdUserListMap = this.list(queryWrapper).stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : idList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
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




