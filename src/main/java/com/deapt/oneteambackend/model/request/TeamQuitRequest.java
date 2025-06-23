package com.deapt.oneteambackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Deapt
 * @description 用户退出队伍请求类
 * @since 2025/6/16 18:31
 */
@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = 5566514598230026898L;
    /**
     * id
     */
    private Long teamId;
}
