package com.mymall.web.manager.controller.system;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.system.AdminVo;
import com.mymall.contract.system.AdminService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.system.Admin;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @DubboReference
    private AdminService adminService;

    @GetMapping("/findAll")
    public List<Admin> findAll(){
        return adminService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Admin> findPage(int page, int size){
        return adminService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Admin> findList(@RequestBody Map<String,Object> searchMap){
        return adminService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Admin> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  adminService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Admin findById(Integer id){
        return adminService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Admin admin){
        adminService.add(admin);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Admin admin){
        adminService.update(admin);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        adminService.delete(id);
        return new Result();
    }

    @GetMapping("/roleList")
    public List<Map> roleList(int page,int size){
        return adminService.adminList(page,size);
    }

    @GetMapping("treeRole")
    public String treeRole(){
        return adminService.treeRole();
    }

    @GetMapping("/saveNewAdmin")
    public Result saveNewAdmin(String logName,String passWord,String roleId){
        adminService.saveNewAdmin(logName,passWord,roleId);
        return new Result();
    }

    @GetMapping("/saveOldAdmin")
    public Result saveOldAdmin(String adminId,String passWord,String roleId){
        adminService.saveOldAdmin(adminId,passWord,roleId);
        return new Result();
    }

    @GetMapping("/saveAdmin")
    public Result saveAdmin(String adminId,String logName,String passWord,String roleId){
        BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
        String hashPass = bcryptPasswordEncoder.encode(passWord);

        //修改
        if(null != adminId && !"".equals(adminId)){
            adminService.saveOldAdmin(adminId,hashPass,roleId);
        }else{//新增
            adminService.saveNewAdmin(logName,hashPass,roleId);
        }

        return new Result();
    }

    @GetMapping("/findRoleByAdminId")
    public AdminVo findRoleByAdminId(String adminId){
        AdminVo adminVo = new AdminVo();
        Admin byId = adminService.findById(Integer.parseInt(adminId));
        String roleId = adminService.findRoleByAdminId(adminId);
        adminVo.setRoleId(roleId);
        adminVo.setId(byId.getId());
        adminVo.setLoginName(byId.getLoginName());
        adminVo.setPassword(byId.getPassword());
        adminVo.setStatus(byId.getStatus());

        return adminVo;
    }

}
