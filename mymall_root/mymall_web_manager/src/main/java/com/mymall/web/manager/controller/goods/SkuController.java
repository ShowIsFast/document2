package com.mymall.web.manager.controller.goods;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.goods.Sku;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.goods.SkuService;
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
@RequestMapping("/sku")
@CrossOrigin
public class SkuController {

    private static Logger log = LoggerFactory.getLogger(SkuController.class);

    @DubboReference
    private SkuService skuService;

    @GetMapping("/findAll")
    public List<Sku> findAll(){
        return skuService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Sku> findPage(int page, int size){
        return skuService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Sku> findList(@RequestBody Map<String,Object> searchMap){
        return skuService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Sku> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  skuService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Sku findById(String id){
        return skuService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Sku sku){
        skuService.add(sku);
        Map<String, Object> stringObjectMap = skuService.selectNewByOne();
        insertUserLog("新增SKU信息",null,stringObjectMap);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Sku sku){
        Sku byId = skuService.findById(sku.getId());
        skuService.update(sku);
        insertUserLog("修改SKU信息",byId,sku);

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id){
        Sku byId = skuService.findById(id);
        skuService.delete(id);
        insertUserLog("删除SKU信息",byId,null);

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
