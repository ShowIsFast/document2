package com.mymall.web.manager.controller.system;

import com.mymall.common.web.entity.Result;
import com.mymall.contract.system.AdminService;
import com.mymall.contract.system.RoleService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.system.Role;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/role")
public class RoleController {

    @DubboReference
    private RoleService roleService;

    @DubboReference
    private AdminService adminService;

    @GetMapping("/findAll")
    public List<Role> findAll(){
        return roleService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Role> findPage(int page, int size){
        return roleService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Role> findList(@RequestBody Map<String,Object> searchMap){
        return roleService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Role> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  roleService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Role findById(Integer id){
        return roleService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Role role){
        roleService.add(role);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Role role){
        roleService.update(role);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        roleService.delete(id);
        return new Result();
    }

    @GetMapping("/saveNewRole")
    public Result saveNewRole(String name,String meuns){
        roleService.save(name,meuns);
        return new Result();
    }

    @GetMapping("/updateRoleResource")
    public Result updateRoleResource(String roleId,String meuns,String name){
        roleService.updateRoleResource(roleId,meuns,name);
        return new Result();
    }

    @GetMapping("/saveRole")
    public Result saveRole(String roleId,String name,String meuns){
        if(null != roleId && !"".equals(roleId)){
            roleService.updateRoleResource(roleId,meuns,name);
        }else{//新增
            roleService.save(name,meuns);
        }

        return new Result();
    }

    @GetMapping("/findRoleMenu")
    public List<String> selectRoleMenu(String roleId){
        return roleService.selectRoleMenu(roleId);
    }

}
