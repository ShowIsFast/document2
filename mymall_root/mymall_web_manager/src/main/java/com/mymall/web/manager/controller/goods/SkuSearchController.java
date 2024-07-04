package com.mymall.web.manager.controller.goods;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.goods.Sku;
import com.mymall.contract.goods.SkuSearchService;
import com.mymall.contract.goods.SkuService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SkuSearchController {

    @DubboReference
    private SkuSearchService skuSearchService;

    @DubboReference
    private SkuService skuService;

    @GetMapping("/importSkuList")
    public Result importSkuList(){
        for(int i = 1; i <= 18160; i++) {
            PageResult<Sku> result = skuService.findPage(i, 5);
            List<Sku> skuList = result.getRows();
            skuSearchService.importSkuList(skuList);
            System.out.print(" " + i);
        }

        return new Result();
    }

}
