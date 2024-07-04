package com.mymall.service.sms.consumer;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.mymall.service.sms.util.SmsUtil;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RocketMQMessageListener(topic = "quene-sms", consumerGroup = "sms_consumer")
public class SmsMessageConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

	private Logger logger = LoggerFactory.getLogger(SmsMessageConsumer.class);

	@Autowired
	private SmsUtil smsUtil;

	@Value("${templateCode_smscode}")
	private String templateCode_smscode;//短信模板编号

	@Value("${templateParam_smscode}")
	private String templateParam_smscode;//短信参数

	@Override
	public void onMessage(String message) {
		Map<String,String> map = JSON.parseObject(message, Map.class);
		String phone = map.get("phone");
		String code=map.get("code");
		System.out.println("手机号："+phone+"验证码："+code);
		logger.info("给手机号：{},发送验证码：{}",phone,code);
		String param=  templateParam_smscode.replace("[value]",code);
		try {
			SendSmsResponse smsResponse = smsUtil.sendSms(phone, templateCode_smscode, param);
		} catch (ClientException e) {
			logger.error("短信发送失败",e);
		}
	}

	@Override
	public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
		defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
	}

}
