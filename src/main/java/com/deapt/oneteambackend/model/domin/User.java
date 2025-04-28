package com.deapt.oneteambackend.model.domin;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Deapt
 * @description 这是一个用户数据传输对象
 * @TableName user
 */
@TableName(value ="user")
@Data
@NoArgsConstructor //生成无参构造
@AllArgsConstructor //生成全参构造
@Builder // 提供链式构造器模式
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 用户密码
     */
    private String userPassword;

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
     * 是否删除0-否
     */
    @TableLogic(value="0",delval="1")
    private Integer isDelete;

    /**
     * 用户角色 0-普通用户，1-管理员
     */
    private Integer userRole;

    /**
     * 用户编号
     */
    private String userCode;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}