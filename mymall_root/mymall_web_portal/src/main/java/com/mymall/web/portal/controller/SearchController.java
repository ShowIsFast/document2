package com.mymall.web.portal.controller;

import com.alibaba.fastjson.JSON;
import com.mymall.common.web.entity.ResponseDTO;
import com.mymall.contract.goods.SkuSearchService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SearchController {

    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @DubboReference
    private SkuSearchService skuSearchService;

    @GetMapping("/search")
    public ResponseDTO search(Model model, @RequestParam Map<String, String> searchMap) throws Exception {
        logger.info("进行商品搜索 searchMap:{}", JSON.toJSON(searchMap));

        Map resultData = new HashMap<String,Object>();

        //页码容错处理
        if(searchMap.get("pageNo")==null){
            searchMap.put("pageNo","1");        //设置默认页码
        }

        //排序参数容错处理
        if(searchMap.get("sortRule")==null){
            searchMap.put("sortRule","desc");   //默认降序
        }

        if(searchMap.get("sortField")==null){
            searchMap.put("sortField","");
        }

        //远程调用接口
        Map result = skuSearchService.search(searchMap);

        resultData.put("result", result);
        resultData.put("searchMap", searchMap);

        //返回的url字符串
        StringBuffer url = new StringBuffer("/search?");
        for (String key : searchMap.keySet()) {
            url.append("&" + key + "=" + searchMap.get(key));
        }
        resultData.put("url",url.toString());

        //当前页码
        int pageNo =Integer.parseInt(searchMap.get("pageNo"));          //当前页
        resultData.put("pageNo",pageNo);

        //页码显示优化
        int totalPages= ((Long) result.get("totalPages")).intValue();   //得到总页数
        int startPage=1;            //开始页码
        int endPage=totalPages;     //截至页码
        if(totalPages>7){
            //得到当前页
            startPage=pageNo-2;
            //负数处理（如果开始页码小于1）
            if(startPage<1){
                startPage=1;
            }
            endPage=startPage+4;
        }

        resultData.put("startPage",startPage);
        resultData.put("endPage",endPage);

        return ResponseDTO.success(resultData);
    }

}
