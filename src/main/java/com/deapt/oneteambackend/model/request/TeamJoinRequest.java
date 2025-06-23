package com.deapt.oneteambackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Deapt
 * @description 用户加入队伍请求类
 * @since 2025/6/16 16:38
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 7653195264738715020L;

    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
