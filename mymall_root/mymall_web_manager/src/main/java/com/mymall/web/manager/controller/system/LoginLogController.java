package com.mymall.web.manager.controller.system;

import com.mymall.common.web.entity.Result;
import com.mymall.contract.system.LoginLogService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.system.LoginLog;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/loginLog")
public class LoginLogController {

    @DubboReference
    private LoginLogService loginLogService;

    @GetMapping("/findAll")
    public List<LoginLog> findAll(){
        return loginLogService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<LoginLog> findPage(int page, int size){
        return loginLogService.findPage(page, size);
    }

    /**
     * 查询当前登录人的登录日志
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/findPageByLogin")
    public PageResult<LoginLog> findPageByLogin(int page, int size){
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map=new HashMap();
        map.put("loginName",loginName);

        return loginLogService.findPage(map,page,size);
    }

    @PostMapping("/findList")
    public List<LoginLog> findList(@RequestBody Map<String,Object> searchMap){
        return loginLogService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<LoginLog> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  loginLogService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public LoginLog findById(Integer id){
        return loginLogService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody LoginLog loginLog){
        loginLogService.add(loginLog);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody LoginLog loginLog){
        loginLogService.update(loginLog);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        loginLogService.delete(id);
        return new Result();
    }

}
