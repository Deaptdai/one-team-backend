package com.deapt.oneteambackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Deapt
 * @description 通用删除请求参数
 * @since 2025/6/25 16:18
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 4757541239642323812L;

    private long id; // 要删除的ID

}
