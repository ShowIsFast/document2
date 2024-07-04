package com.mymall.service.order.consumer;

import com.mymall.contract.order.CartService;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "quene_delete_cart", consumerGroup = "order-group")
public class CartMessageConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

	@Autowired
	private CartService cartService;

	public void onMessage(String username) {
		try {
			cartService.deleteCheckedCart(username);
		} catch (Exception e) {
			e.printStackTrace();
			// todo 记录错误日志
		}
	}

	@Override
	public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
		defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
	}

}
