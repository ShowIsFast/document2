package com.mymall.web.manager.controller.system;

import com.mymall.common.web.entity.Result;
import com.mymall.contract.system.MenuService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.system.Menu;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @DubboReference
    private MenuService menuService;

    @GetMapping("/findAll")
    public List<Menu> findAll(){
        return menuService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Menu> findPage(int page, int size){
        return menuService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Menu> findList(@RequestBody Map<String,Object> searchMap){
        return menuService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Menu> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  menuService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Menu findById(String id){
        return menuService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Menu menu){
        menuService.add(menu);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Menu menu){
        menuService.update(menu);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id){
        menuService.delete(id);
        return new Result();
    }

    @GetMapping("/findMenu")
    public List<Map> findMenu(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return menuService.findAllMenu(username);
    }

    @GetMapping("/findMenuByUser")
    public List findMenuByUser(String roleId){
        return menuService.findMenuByRole(roleId);
    }

}
