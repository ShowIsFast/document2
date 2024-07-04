package com.mymall.web.manager.controller.business;

import com.mymall.common.web.entity.Result;
import com.mymall.contract.business.ActivityService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.business.Activity;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @DubboReference
    private ActivityService activityService;

    @GetMapping("/findAll")
    public List<Activity> findAll(){
        return activityService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Activity> findPage(int page, int size){
        return activityService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Activity> findList(@RequestBody Map<String,Object> searchMap){
        return activityService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Activity> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  activityService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Activity findById(Integer id){
        return activityService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Activity activity){
        activityService.add(activity);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Activity activity){
        activityService.update(activity);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        activityService.delete(id);
        return new Result();
    }

}
