package com.mymall.service.goods.impl;

import com.mymall.contract.goods.CategoryService;
import com.mymall.contract.goods.SkuService;

import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 初始化服务类
 */
@DubboService
public class InitService implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuService skuService;

    public void afterPropertiesSet() throws Exception {
        logger.info("商品价格缓存预热");
        categoryService.saveCategoryTreeToRedis();//加载商品分类缓存
        skuService.saveAllPriceToRedis();//加载价格数据
    }

}
