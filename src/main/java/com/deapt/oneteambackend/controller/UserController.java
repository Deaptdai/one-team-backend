package com.deapt.oneteambackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deapt.oneteambackend.common.result.StatusCode;
import com.deapt.oneteambackend.constant.UserConstant;
import com.deapt.oneteambackend.exception.BaseException;
import com.deapt.oneteambackend.model.domin.User;
import com.deapt.oneteambackend.model.domin.request.UserLoginRequest;
import com.deapt.oneteambackend.model.domin.request.UserRegisterRequest;
import com.deapt.oneteambackend.common.result.Result;
import com.deapt.oneteambackend.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Deapt
 * @description 用户表示层
 * @since 2025/4/26 10:22
 */

@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        log.info("用户注册:{}",userRegisterRequest);
        //1. 判断请求体是否为空
        if (userRegisterRequest == null){
            return Result.error(StatusCode.REQUEST_IS_NULL);
        }
        //2. 获取所需数据
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isBlank(userAccount)){
            log.info("请输入用户名");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"请输入用户名");
        }else if(StringUtils.isBlank(userPassword)){
            log.info("请输入密码");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"请输入密码");
        }else if (StringUtils.isBlank(checkPassword)){
            log.info("请再次确认密码");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"请再次确认密码");
        }

        long id = userService.userRegister(userAccount, userPassword, checkPassword);
        return Result.success(id,StatusCode.SUCCESS);
    }

    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody UserLoginRequest userRegisterRequest, HttpServletRequest request){
        log.info("用户登录:{}",userRegisterRequest);
        //1. 判断请求体是否为空
        if (userRegisterRequest == null){
            return Result.error(StatusCode.REQUEST_IS_NULL);
        }
        //2. 获取所需数据
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();

        if (StringUtils.isBlank(userAccount)){
            log.info("请输入用户名");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"请输入用户名");
        }else if(StringUtils.isBlank(userPassword)){
            log.info("请输入密码");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"请输入密码");
        }

        User user = userService.userLogin(userAccount, userPassword, request);
        return Result.success(user,StatusCode.SUCCESS);
    }

    @GetMapping("/search")
    public Result<List<User>> search(@RequestParam("username") String username ,HttpServletRequest request){
        List<User> search = userService.search(username,request);
        return Result.success(search,StatusCode.SUCCESS);
    }

    @GetMapping("/search/tags")
    public Result<List<User>> searchUsersByTags(@RequestParam(value = "tagNameList",required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            log.info("标签列表不能为空");
            throw new BaseException(StatusCode.PARAMETER_ERROR,"标签列表不能为空");
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return Result.success(userList,StatusCode.SUCCESS);
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestBody long id,HttpServletRequest request){
        boolean delete = userService.delete(id,request);

        if (!delete){
            throw new BaseException(StatusCode.SYSTEM_ERROR,"删除失败");
        }
        return Result.success(StatusCode.SUCCESS);
    }

    @PostMapping("/logout")
    public Result userLogout(HttpServletRequest request){
        if (request == null){
            return Result.error(StatusCode.REQUEST_IS_NULL);
        }
        userService.userLogout(request);
        return Result.success(StatusCode.SUCCESS);
    }

    @GetMapping("/current")
    public Result<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null){
            throw new BaseException(StatusCode.USER_NOT_LOGIN,"用户未登录");
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        return Result.success(user, StatusCode.SUCCESS);
    }

    @PostMapping("/update")
    public Result<Integer> updateUser(@RequestBody User user, HttpServletRequest request){ {
        //校验参数是否为空
        if (user == null){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"用户不存在");
        }
        User loginUser = userService.getLoginUser(request);

        int i = userService.updateUser(user,loginUser);
        return Result.success(i, StatusCode.SUCCESS);
        }
    }

    @GetMapping("/recommend")
    public Result<Page<User>> recommendUsers(long pageSize,long pageNum,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("deapt:user:recommend:%s",loginUser.getId());
        //查询缓存，如果存在直接读取
        Page<User> userPage= (Page<User>) redisTemplate.opsForValue().get(redisKey);
        if (userPage != null) {
            return Result.success(userPage, StatusCode.SUCCESS);
        }
        //如果缓存不存在，则查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum,pageSize),queryWrapper);
        //写缓存
        try {
            redisTemplate.opsForValue().set(redisKey, userPage,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("缓存写入失败: {}", e.getMessage());
        }
        return Result.success(userPage, StatusCode.SUCCESS);
    }
}
