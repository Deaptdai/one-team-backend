package com.deapt.oneteambackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Deapt
 * @description 通用分页请求参数
 * @since 2025/6/13 13:59
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -8346459945136266050L;
    /**
     * 页面大小
     */
    protected int pageSize = 10;
    /**
     * 当前是第几页
     */
    protected int pageNum = 1;
}
