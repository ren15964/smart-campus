package com.smartcampus.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> records; // 当前页数据
    private Long total;      // 总记录数
    private Long size;       // 每页记录数
    private Long current;    // 当前页码
    private Long pages;      // 总页数

}
