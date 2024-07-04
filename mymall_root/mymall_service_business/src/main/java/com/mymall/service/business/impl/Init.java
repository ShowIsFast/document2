package com.mymall.service.business.impl;

import com.mymall.contract.business.AdService;

import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class Init implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AdService adService;

    public void afterPropertiesSet() throws Exception {
        logger.info("---缓存预热---");
        //加载广告轮播图缓存
        adService.saveAllAd();
    }

}
