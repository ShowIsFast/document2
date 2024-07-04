package com.mymall.service.seckill.consumer;

import com.mymall.service.seckill.dao.SeckillGoodsMapper;
import com.mymall.pojo.seckill.SeckillGoods;
import com.mymall.pojo.seckill.SeckillOrder;
import com.mymall.contract.pay.WeixinPayService;
import com.mymall.pojo.entity.SeckillStatus;

import lombok.extern.slf4j.Slf4j;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RocketMQMessageListener(consumerGroup = "consumerGroup",topic = "seckill_order",selectorExpression = "*")
public class SeckillOrderMessageListener implements RocketMQListener<SeckillStatus> {

    private static Logger log = LoggerFactory.getLogger(SeckillOrderMessageListener.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private WeixinPayService weixinPayService;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    /**
     * 消息监听
     * @param seckillStatus
    */
    @Override
    public void onMessage(SeckillStatus seckillStatus) {
        log.debug("监听到的消息："+seckillStatus);

        //回滚操作
        if(seckillStatus==null){
            return;
        }

        //判断Redis中是否存在对应的订单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(seckillStatus.getUsername());

        //如果存在，开始回滚
        if(seckillOrder!=null) {
            //1.关闭微信支付
            Map<String, String> map = weixinPayService.closePay(seckillStatus.getOrderId().toString());

            if (map.get("return_code").equals("SUCCESS") && map.get("result_code").equals("SUCCESS")) {
                //2.删除用户订单
                redisTemplate.boundHashOps("SeckillOrder").delete(seckillOrder.getUserId());

                //3.查询出商品数据
                SeckillGoods goods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).get(seckillStatus.getGoodsId());
                if (goods == null) {
                    //数据库中加载数据
                    goods = seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());
                }

                //4.递增库存  incr
                Long seckillGoodsCount = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillStatus.getGoodsId(), 1);
                goods.setStockCount(seckillGoodsCount.intValue());

                //5.将商品数据同步到Redis
                redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).put(seckillStatus.getGoodsId(), goods);
                redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId()).leftPush(seckillStatus.getGoodsId());

                //6.清理用户抢单排队信息
                //清理重复排队标识
                redisTemplate.boundHashOps("UserQueueCount").delete(seckillStatus.getUsername());

                //清理排队存储信息
                redisTemplate.boundHashOps("UserQueueStatus").delete(seckillStatus.getUsername());
            }
        }
    }

}
