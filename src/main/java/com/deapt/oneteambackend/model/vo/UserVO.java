package com.deapt.oneteambackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Deapt
 * @description 用户包装类(脱敏)
 * @since 2025/6/16 10:36
 */
@Data
public class UserVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态0 - 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户角色 0-普通用户，1-管理员
     */
    private Integer userRole;

    /**
     * 用户编号
     */
    private String userCode;

    /**
     * 用户标签列表 Json格式
     */
    private String tags;

    private static final long serialVersionUID = 1L;
}
