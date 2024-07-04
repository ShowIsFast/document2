package com.mymall.web.portal.controller;

import com.mymall.common.web.entity.ResponseDTO;
import com.mymall.pojo.business.Ad;
import com.mymall.pojo.goods.Category;
import com.mymall.contract.business.AdService;
import com.mymall.contract.goods.CategoryService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class IndexController {

    @DubboReference
    private AdService adService;

    @DubboReference
    private CategoryService categoryService;

    /**
     * 网站首页
     * @return
     */
    @GetMapping("/index")
    public ResponseDTO index(Model model){
        List<Ad> lbtList = adService.findByPosition("web_index_lb");    //广告轮播图
        List<Map> categoryList = categoryService.findCategoryTree();    //商品分类

        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("isShow","1");
        searchMap.put("isMenu","1");
        List<Category> indexList = categoryService.findList(searchMap); //商品菜单
        System.out.println(indexList);

        List<String> indexCategoryList = new ArrayList<>();
        for (Category c: indexList) {
            indexCategoryList.add(c.getName());
        }

        Map result = new HashMap<String,Object>();
        result.put("lbt",lbtList);
        result.put("categoryList",categoryList);
        result.put("indexCategoryList",indexCategoryList);

        return ResponseDTO.success(result);
    }

}
