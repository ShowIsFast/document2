package com.mymall.common.cas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CasProperties {

    /**
     * 秘钥
     */
    @Value("${key}")
    private String casKey;

    /**
     * cas服务端地址
     */
    @Value("${server.host.url}")
    private String casServerUrl;

    /**
     * cas服务端登录地址
     */
    @Value("${server.host.login_url}")
    private String casServerLoginUrl;

    /**
     * cas服务端登出地址 并回跳到制定页面
     */
    @Value("${server.host.logout_url}")
    private String casServerLogoutUrl;

    /**
     * cas客户端地址
     */
    @Value("${service.host.url}")
    private String casServiceUrl;

    /**
     * cas客户端地址登录地址
     */
    @Value("${service.host.login_url}")
    private String casServiceLoginUrl;

    /**
     * cas客户端地址登出地址
     */
    @Value("${service.host.logout_url}")
    private String casServiceLogoutUrl;

    public String getCasKey() {
        return casKey;
    }

    public void setCasKey(String casKey) {
        this.casKey = casKey;
    }

    public String getCasServerUrl() {
        return casServerUrl;
    }

    public void setCasServerUrl(String casServerUrl) {
        this.casServerUrl = casServerUrl;
    }

    public String getCasServerLoginUrl() {
        return casServerLoginUrl;
    }

    public void setCasServerLoginUrl(String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }

    public String getCasServerLogoutUrl() {
        return casServerLogoutUrl;
    }

    public void setCasServerLogoutUrl(String casServerLogoutUrl) {
        this.casServerLogoutUrl = casServerLogoutUrl;
    }

    public String getCasServiceUrl() {
        return casServiceUrl;
    }

    public void setCasServiceUrl(String casServiceUrl) {
        this.casServiceUrl = casServiceUrl;
    }

    public String getCasServiceLoginUrl() {
        return casServiceLoginUrl;
    }

    public void setCasServiceLoginUrl(String casServiceLoginUrl) {
        this.casServiceLoginUrl = casServiceLoginUrl;
    }

    public String getCasServiceLogoutUrl() {
        return casServiceLogoutUrl;
    }

    public void setCasServiceLogoutUrl(String casServiceLogoutUrl) {
        this.casServiceLogoutUrl = casServiceLogoutUrl;
    }

}
