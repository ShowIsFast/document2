package com.mymall.web.manager.controller.config;

import com.mymall.common.web.entity.Result;
import com.mymall.contract.config.FreightTemplateService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.config.FreightTemplate;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/freightTemplate")
public class FreightTemplateController {

    @DubboReference
    private FreightTemplateService freightTemplateService;

    @GetMapping("/findAll")
    public List<FreightTemplate> findAll(){
        return freightTemplateService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<FreightTemplate> findPage(int page, int size){
        return freightTemplateService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<FreightTemplate> findList(@RequestBody Map<String,Object> searchMap){
        return freightTemplateService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<FreightTemplate> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  freightTemplateService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public FreightTemplate findById(Integer id){
        return freightTemplateService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody FreightTemplate freightTemplate){
        freightTemplateService.add(freightTemplate);

        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody FreightTemplate freightTemplate){
        freightTemplateService.update(freightTemplate);

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        freightTemplateService.delete(id);

        return new Result();
    }

}
