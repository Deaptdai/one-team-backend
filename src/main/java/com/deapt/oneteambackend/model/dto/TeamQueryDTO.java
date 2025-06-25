package com.deapt.oneteambackend.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.deapt.oneteambackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


/**
 * @author Deapt
 * @description 队伍查询封装类
 * @since 2025/6/13 12:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQueryDTO extends PageRequest {
    private static final long serialVersionUID = 8443901666574313034L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * id列表，通常用于批量操作
     */
    private List<Long> idList;

    /**
     * 搜索关键词，同时对名称和描述进行搜索
     */
    private String searchText;

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
     * 用户id
     */
    private Long userId;

    /**
     * 状态 0 - 公开,1- 私有,2 - 加密
     */
    private Integer status;

}
