package com.mymall.web.manager.controller.user;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.business.Ad;
import com.mymall.pojo.user.User;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.business.AdService;
import com.mymall.contract.user.UserLogService;
import com.mymall.contract.user.UserService;
import com.mymall.pojo.entity.PageResult;
import net.sf.json.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @DubboReference
    private UserService userService;

    @DubboReference
    private AdService adService;

    @GetMapping("/name")
    public PageResult<Ad> name(int page, int size){
        return  adService.findPage(page,size);
    }

    @GetMapping("/findAll")
    public List<User> findAll(){
        return userService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<User> findPage(int page, int size){
        return userService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<User> findList(@RequestBody Map<String,Object> searchMap){
        return userService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<User> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  userService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public User findById(String username){
        return userService.findById(username);
    }


    @PostMapping("/add")
    public Result add(@RequestBody User user){
        userService.add(user);
        Map<String, Object> stringObjectMap = userService.selectNewByOne();
        insertUserLog("新增用户信息",null,stringObjectMap);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody User user){
        User byId = userService.findById(user.getUsername());
        userService.update(user);
        insertUserLog("修改用户信息",byId,user);

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String username){
        User byId = userService.findById(username);
        userService.delete(username);
        insertUserLog("删除用户信息",byId,null);

        return new Result();
    }

    @DubboReference
    private UserLogService userLogService;

    /**
     * instructions:说明
     * oldData:操作前数据
     * newData:操作后数据
     * */
    private void insertUserLog(String instructions,Object oldData,Object newData){
        log.info("[insertUserLog][insert][userLog:]");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserLog userLog = new UserLog();
        userLog.setUsername(username);
        userLog.setInstructions(instructions);
        if(oldData != null && !"".equals(String.valueOf(oldData))){
            userLog.setOldData(JSONObject.fromObject(oldData).toString());
        }
        if(newData != null && !"".equals(String.valueOf(newData))){
            userLog.setNewData(JSONObject.fromObject(newData).toString());
        }
        userLog.setUpdateTime(new Date());
        log.info(userLog.toString());
        userLogService.add(userLog);
    }

}
