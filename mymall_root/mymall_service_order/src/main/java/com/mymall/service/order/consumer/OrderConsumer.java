package com.mymall.service.order.consumer;

import com.mymall.pojo.order.Order;
import com.mymall.contract.order.OrderService;

import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 下单事务消息操作
 * 订单服务收到消息时说明上游已经成功扣减库存，这里直接订单入库即可
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "tx_order_msg", consumerGroup = "tx_order")
public class OrderConsumer implements RocketMQListener<Order>, RocketMQPushConsumerLifecycleListener {

    private final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    @Resource
    private OrderService orderService;

    // 监听消息订单入库
    @Override
    public void onMessage(Order order ) {
        logger.info("获取的商品入库的消息order", order);
        orderService.insertOrder(order);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
    }

}
