package com.mymall.web.manager.controller.user;

import com.mymall.pojo.entity.PageResult;
import com.mymall.common.web.entity.Result;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.user.UserLogService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user_log")
@CrossOrigin
public class UserLogController {

    private static Logger log = LoggerFactory.getLogger(UserLogController.class);

    @DubboReference
    private UserLogService userLogService;


    @GetMapping("/findAll")
    public List<UserLog> findAll(){
        return userLogService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<UserLog> findPage(int page, int size){
        return userLogService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<UserLog> findList(@RequestBody Map<String,Object> searchMap){
        return userLogService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<UserLog> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        searchMap.put("username",username);
        log.info("[mymall_web_manager][UserLogController][findPage][searchMap:]"+searchMap);
        return userLogService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public UserLog findById(Long id){
        return userLogService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody UserLog userLog){
        userLogService.add(userLog);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody UserLog userLog){
        log.info("[mymall_web_manager][UserLogController][update][userLog:]"+userLog);
        userLogService.update(userLog);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id){
        log.info("[mymall_web_manager][UserLogController][delete][id:]"+id);
        userLogService.delete(id);

        return new Result();
    }

}
