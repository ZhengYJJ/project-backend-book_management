package com.library.common;

import java.util.List;

/**
 * 分页返回结构。
 */
public class PageResult<T> {

    private long total;
    private int pageNum;
    private int pageSize;
    private List<T> list;

    public PageResult() {
    }

    public static <T> PageResult<T> of(int pageNum, int pageSize, long total, List<T> list) {
        PageResult<T> p = new PageResult<>();
        p.pageNum = pageNum;
        p.pageSize = pageSize;
        p.total = total;
        p.list = list;
        return p;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
