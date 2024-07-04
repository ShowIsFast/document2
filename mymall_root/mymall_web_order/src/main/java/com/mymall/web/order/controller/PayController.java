package com.mymall.web.order.controller;

import com.github.wxpay.sdk.WXPayUtil;
import com.mymall.pojo.order.Order;
import com.mymall.contract.order.OrderService;
import com.mymall.contract.pay.WeixinPayService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @DubboReference
    private WeixinPayService weixinPayService;

    @DubboReference
    private OrderService orderService;

    @Value("${weinxin.pay.notifyUrl}")
    private String notifyUrl;

    /**
     * 生成二维码
     * @param orderId
     * @return
     */
    @GetMapping("/createNative")
    public Map createNative(String orderId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Order order =   orderService.findById(orderId);
        if(order!=null){
            //校验该订单是否时当前用户的订单，并且时未支付的订单
            if("0".equals(order.getPayStatus()) &&  "0".equals(order.getOrderStatus()) && username.equals(order.getUsername())){
//                Map map = weixinPayService.createNative(order.getId(), order.getPayMoney(), notifyUrl);
                Map map = weixinPayService.createNative(order.getId(), 1, notifyUrl);//支付1分，用于测试
                return map;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    /**
     * 回调通知
     * @param request
     * @return
     */
    @RequestMapping("/notify")
    public Map notifyLogic(HttpServletRequest request){
        System.out.println("回调了.....");
        InputStream inStream;
        try {
            inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String result = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用我们notify_url的返回信息
            System.out.println(result);
            Map<String, String> map = WXPayUtil.xmlToMap(result);

            orderService.updatePayStatus(map.get("out_trade_no"),map.get("transaction_id"));//更新订单状态
        } catch (Exception e) {
            e.printStackTrace();
            //TODO 记录错误日志
        }

        return new HashMap();
    }

    @GetMapping("/queryPayStatus")
    public Map queryPayStatus(String orderId){
        Map<String,Object> map = new HashMap<>();
        Order byId = orderService.findById(orderId);
        if(byId.getOrderStatus().equals("0")){
            map.put("code","1");
            map.put("message","请求成功!");
            map.put("data",weixinPayService.queryPayStatus(orderId));
        }else{
            map.put("code","0");
            map.put("message","订单超时已关闭!");
            map.put("data",new HashMap<>());
        }

        return map;
    }

    /**
     * 这个接口和定时任务配合，支付时间超时即调用
     * @param outtradeno
     * @return
     */
    @GetMapping("/closePay")
    public Map closePay(String outtradeno){
        return weixinPayService.closePay(outtradeno);
    }

}
