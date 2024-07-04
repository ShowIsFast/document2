package com.mymall.web.portal.controller;

import com.mymall.pojo.goods.Sku;
import com.mymall.contract.business.AdService;
import com.mymall.contract.goods.CategoryService;
import com.mymall.contract.goods.SkuService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.dubbo.config.annotation.DubboReference;

@RestController
@RequestMapping("/item")
public class ItemController {

    @DubboReference
    private AdService adService;

    @DubboReference
    private SkuService skuService;

    @DubboReference
    private CategoryService categoryService;

    @GetMapping("/price")
    public int getPriceBuSkuId(String id){
        Sku sku = skuService.findById(id);
        return sku.getPrice();
    }

}
