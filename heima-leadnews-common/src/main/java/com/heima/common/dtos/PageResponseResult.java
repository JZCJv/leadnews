package com.heima.common.dtos;

import java.io.Serializable;

/**
 * 页面响应结果
 */
public class PageResponseResult extends ResponseResult implements Serializable {
    private Integer currentPage; //当前页面
    private Integer size;  //每页显示的记录数
    private Integer total; //总数

    public PageResponseResult(Integer currentPage, Integer size, Integer total) {
        this.currentPage = currentPage;
        this.size = size;
        this.total = total;
    }

    public PageResponseResult() {

    }


    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
