package com.mymall.common.util;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.mymall.SmsProviderApplication;
import com.mymall.service.sms.util.SmsUtil;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmsProviderApplication.class)
public class SmsUtilTest extends TestCase {

    @Autowired
    private SmsUtil smsUtil;
    @Test
    public void testSms() throws ClientException {
        String templateParam_smscode = "{\"code\":\"[value]\"}";
        String param=  templateParam_smscode.replace("[value]","586214");
        SendSmsResponse response = smsUtil.sendSms("15313795039", "SMS_194056180", param);
        System.out.println(JSON.toJSON(response));
    }

}
