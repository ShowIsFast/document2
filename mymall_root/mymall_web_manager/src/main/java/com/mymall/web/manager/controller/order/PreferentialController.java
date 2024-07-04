package com.mymall.web.manager.controller.order;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.order.Preferential;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.order.PreferentialService;
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
@RequestMapping("/preferential")
public class PreferentialController {

    private static Logger log = LoggerFactory.getLogger(PreferentialController.class);

    @DubboReference
    private PreferentialService preferentialService;

    @GetMapping("/findAll")
    public List<Preferential> findAll(){
        return preferentialService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Preferential> findPage(int page, int size){
        return preferentialService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Preferential> findList(@RequestBody Map<String,Object> searchMap){
        return preferentialService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Preferential> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        log.info("[mymall_web_manager][PreferentialController][findPage][searchMap:]"+searchMap);
        return  preferentialService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Preferential findById(Integer id){
        return preferentialService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Preferential preferential){
        preferentialService.add(preferential);
        Map<String, Object> stringObjectMap = preferentialService.selectNewByOne();
        log.info("stringObjectMap==");
        log.info(stringObjectMap.toString());
        log.info(JSONObject.fromObject(stringObjectMap.toString()).toString());
        insertUserLog("新增满减信息",null,stringObjectMap.toString());

        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Preferential preferential){
        Preferential byId = preferentialService.findById(preferential.getId());
        preferentialService.update(preferential);
        insertUserLog("修改满减信息",byId,preferential);

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        Preferential byId = preferentialService.findById(id);
        preferentialService.delete(id);
        insertUserLog("删除满减信息",byId,null);

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
        if(null !=  oldData&& !"".equals(String.valueOf(oldData))){
            userLog.setOldData(JSONObject.fromObject(oldData).toString());
        }
        if(null != newData && !"".equals(String.valueOf(newData))){
            userLog.setNewData(JSONObject.fromObject(newData).toString());
        }
        userLog.setUpdateTime(new Date());
        log.info(userLog.toString());

        userLogService.add(userLog);
    }

}
