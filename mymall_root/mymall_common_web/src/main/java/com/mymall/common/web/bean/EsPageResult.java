package com.mymall.common.web.bean;

import java.io.Serializable;
import java.util.Map;

public class EsPageResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map result;     //远程调用结果

    private Map searchMap;  // 返回查询对象

    private String url;     // 返回的url字符串

    private Integer pageNo; // 当前页码

    private Integer startPage;  // 开始页码

    private Integer endPage;    // 结束页码

    public Map getResult() {
        return result;
    }

    public void setResult(Map result) {
        this.result = result;
    }

    public Map getSearchMap() {
        return searchMap;
    }

    public void setSearchMap(Map searchMap) {
        this.searchMap = searchMap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getStartPage() {
        return startPage;
    }

    public void setStartPage(Integer startPage) {
        this.startPage = startPage;
    }

    public Integer getEndPage() {
        return endPage;
    }

    public void setEndPage(Integer endPage) {
        this.endPage = endPage;
    }

}
