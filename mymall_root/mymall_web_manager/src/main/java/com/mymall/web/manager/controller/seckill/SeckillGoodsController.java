package com.mymall.web.manager.controller.seckill;

import com.mymall.pojo.entity.PageResult;
import com.mymall.common.web.entity.Result;
import com.mymall.pojo.seckill.SeckillGoods;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.seckill.SeckillGoodsService;
import com.mymall.contract.user.UserLogService;
import com.mymall.common.util.DateUtil;

import net.sf.json.JSONObject;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/seckill_goods")
public class SeckillGoodsController {

    private static Logger log = LoggerFactory.getLogger(SeckillGoodsController.class);

    @DubboReference
    private SeckillGoodsService seckillGoodsService;

    /***
     * URL:/seckill/goods/one
     * 根据商品ID查询商品详情a
     * @param time:时间
     * @param id:商品ID
     */
    @GetMapping(value = "/one")
    public SeckillGoods one(String time,Long id){
        return seckillGoodsService.one(time,id);
    }

    /*****
     * URL:/seckill/goods/list
     * 加载对应时区的秒杀商品
     * @param  time:2019052715
     */
    @GetMapping(value = "/list")
    public List<SeckillGoods> list(String time){
        return seckillGoodsService.list(time);
    }

    /****
     * 加载所有时间菜单
     * @return
     */
    @RequestMapping(value = "/menus")
    public List<Date> loadMenus(){
        return DateUtil.getDateMenus();
    }

    @GetMapping("/updateTheGoods")
    public Result updateTheGoods(String id,String status){
        SeckillGoods byId = seckillGoodsService.findById(Long.parseLong(id));
        SeckillGoods oldData = byId;
        byId.setStatus(status);
        byId.setCheckTime(new Date());
        seckillGoodsService.update(byId);
        insertUserLog("修改秒杀活动",oldData,byId);

        return new Result();
    }

    @GetMapping("/findAll")
    public List<SeckillGoods> findAll(){
        return seckillGoodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<SeckillGoods> findPage(int page, int size){
        return seckillGoodsService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<SeckillGoods> findList(@RequestBody Map<String,Object> searchMap){
        return seckillGoodsService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<SeckillGoods> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        log.info("[mymall_web_manager][AlbumItemController][findPage][searchMap:]"+searchMap);
        return seckillGoodsService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public SeckillGoods findById(Long id){
        return seckillGoodsService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody SeckillGoods seckillGoods){
        seckillGoods.setCreateTime(new Date());
        seckillGoods.setStatus("0");
        seckillGoodsService.add(seckillGoods);
        Map<String, Object> stringObjectMap = seckillGoodsService.selectNewByOne();
        insertUserLog("新增秒杀活动",null,stringObjectMap);

        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody SeckillGoods seckillGoods){
        log.info("[mymall_web_manager][AlbumItemController][update][seckillGoods:]"+seckillGoods);
        SeckillGoods byId = seckillGoodsService.findById(seckillGoods.getId());
        seckillGoodsService.update(seckillGoods);
        insertUserLog("修改秒杀活动",byId,seckillGoods);

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id){
        SeckillGoods byId = seckillGoodsService.findById(id);
        seckillGoodsService.delete(id);
        insertUserLog("删除秒杀活动",byId,null);

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
