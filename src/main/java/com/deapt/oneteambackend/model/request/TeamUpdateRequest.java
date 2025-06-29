package com.deapt.oneteambackend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Deapt
 * @description
 * @since 2025/6/16 14:47
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -8247806487387884244L;

    /**
     * 队伍ID
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date expireTime;

    /**
     * 状态 0 - 公开,1- 私有,2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

}
