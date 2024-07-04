package com.mymall.web.seckill.controller;

import com.mymall.pojo.entity.PageResult;
import com.mymall.common.web.entity.Result;
import com.mymall.pojo.seckill.SeckillGoods;
import com.mymall.contract.seckill.SeckillGoodsService;
import com.mymall.common.util.DateUtil;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/seckill/goods")
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
        System.out.println("time:"+time);
        System.out.println("id:"+id);
        SeckillGoods one = seckillGoodsService.one(time, id);
        System.out.println(one.toString());

        return one;
    }

    /*****
     * URL:/seckill/goods/list
     * 加载对应时区的秒杀商品
     * @param  time:2019052715
     */
    @GetMapping(value = "/list")
    public List<SeckillGoods> list(String time){
        List<SeckillGoods> list = seckillGoodsService.list(time);
        log.info("list.size:"+list.size());

        return list;
    }

    /****
     * 加载所有时间菜单
     * @return
     */
    @RequestMapping(value = "/menus")
    public List<Date> loadMenus(){
        return DateUtil.getDateMenus();
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
        seckillGoodsService.add(seckillGoods);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody SeckillGoods seckillGoods){
        log.info("[mymall_web_manager][AlbumItemController][update][seckillGoods:]"+seckillGoods);
        seckillGoodsService.update(seckillGoods);

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id){
        seckillGoodsService.delete(id);
        return new Result();
    }

}
