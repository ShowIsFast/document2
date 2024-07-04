package com.mymall.web.seckill.controller;

import com.mymall.pojo.entity.Result;
import com.mymall.pojo.seckill.SeckillOrder;
import com.mymall.contract.seckill.SeckillOrderService;
import com.mymall.pojo.entity.SeckillStatus;

import net.sf.json.JSONObject;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/seckill/order")
public class SeckillOrderController {

    private static Logger log = LoggerFactory.getLogger(SeckillOrderController.class);

    @DubboReference
    private SeckillOrderService seckillOrderService;

    @Autowired
    private RedisTemplate redisTemplate;

    /*****
     * URL:/seckill/order/query
     * 查询用户抢单状态
     * 获取用户名
     * 调用Service查询
     */
    @RequestMapping(value = "/query")
    public Result queryStatus(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("[mymall_web_seckill][SeckillOrderController][queryStatus][username]:      "+username);
        //用户未登录
        if(username.equals("anonymousUser")){
            return new Result(403,"用户未登录！");
        }

        try {
            //调用Service查询
            SeckillStatus seckillStatus = seckillOrderService.queryStatus(username);
            log.info("seckillStatus {} ", JSONObject.fromObject(seckillStatus).toString());
            SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.boundHashOps("SeckillOrder").get(username);
            if(seckillStatus!=null){
                Result result = new Result(seckillStatus.getStatus(),"抢单状态！");
                result.setOther(seckillStatus);

                ResultItem results = new ResultItem();
                results.setResult(result);
                results.setMoney(seckillOrder.getMoney().toString());
                results.setOrderId(String.valueOf(seckillOrder.getId()));

                return results;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //0:表示抢单失败
            return new Result(0,e.getMessage());
        }

        return new Result(404,"无相关信息！");
    }

    static class ResultItem extends Result{

        private Result result;
        private String orderId;
        private String money;

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

    }

    /**
     * 前端请求URL: /seckill/order/add?time=123456&id=89
     * @param id:秒杀抢购的商品 id
     * @param time: 秒杀抢购的商品所在的时间区间
     * @return
     */
    @RequestMapping(value = "/add")
    public Result add(Long id,String time){
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); //获取用户名

        //如果用户没登录，则提醒用户登录
        if(username.equals("anonymousUser")){
            return new Result(403,"未登录，请先登录！");
        }

        try {
            //调用数据服务service层的秒杀下单接口
            Result result = seckillOrderService.add(id, time, username);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(2,e.getMessage());
        }
    }

}
