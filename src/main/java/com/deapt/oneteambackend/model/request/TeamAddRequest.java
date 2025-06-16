package com.deapt.oneteambackend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Deapt
 * @description
 * @since 2025/6/15 18:53
 */
@Data
public class TeamAddRequest implements Serializable {
    private static final long serialVersionUID = -5526729283906410288L;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

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
