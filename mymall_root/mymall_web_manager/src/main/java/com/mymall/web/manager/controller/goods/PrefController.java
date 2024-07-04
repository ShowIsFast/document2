package com.mymall.web.manager.controller.goods;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.goods.Pref;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.goods.PrefService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.contract.user.UserLogService;

import net.sf.json.JSONObject;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/pref")
@CrossOrigin
public class PrefController {

    private static Logger log = LoggerFactory.getLogger(PrefController.class);

    @DubboReference
    private PrefService prefService;

    @GetMapping("/findAll")
    public List<Pref> findAll(){
        return prefService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Pref> findPage(int page, int size){
        return prefService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Pref> findList(@RequestBody Map<String,Object> searchMap){
        return prefService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Pref> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  prefService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Pref findById(Integer id){
        return prefService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Pref pref){
        prefService.add(pref);
        Map<String, Object> stringObjectMap = prefService.selectNewByOne();
        insertUserLog("新增满减活动",null,stringObjectMap);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Pref pref){
        Pref byId = prefService.findById(pref.getId());
        prefService.update(pref);
        insertUserLog("修改满减活动",byId,pref);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        Pref byId = prefService.findById(id);
        prefService.delete(id);
        insertUserLog("删除满减活动",byId,null);
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
