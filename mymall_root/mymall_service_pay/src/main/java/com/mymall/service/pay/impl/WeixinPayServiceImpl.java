package com.mymall.service.pay.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.mymall.contract.goods.SkuService;
import com.mymall.contract.order.OrderItemService;
import com.mymall.contract.pay.WeixinPayService;
import com.mymall.pojo.goods.Sku;
import com.mymall.pojo.order.OrderItem;
import com.mymall.common.service.util.HttpClient;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @DubboReference
    private SkuService skuService;

    @DubboReference
    private OrderItemService orderItemService;

    /**
     * 关闭订单，还原库存
     * 商品秒杀时，用户秒到商品但未支付，调用该接口
     * @param outtradeno
     * @return
     */
    @Override
    public Map closePay(String outtradeno) {
        Map searchMap = new HashMap(2);
        searchMap.put("orderId",outtradeno);
        List<OrderItem> orderItemList = orderItemService.findList(searchMap);   //通过订单id查找订单明细
        for (OrderItem orderItem : orderItemList){
            Sku sku = skuService.findById(orderItem.getSkuId());    //通过订单明细中的skuid查找到库存sku
            sku.setNum(sku.getNum()+orderItem.getNum());            //回滚库存
            skuService.update(sku);
        }

        Map param=new HashMap();
        param.put("appid", appid);              //公众账号ID
        param.put("mch_id", partner);           //商户号
        param.put("out_trade_no", outtradeno);  //订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/closeorder";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成用于支付的二维码
     * @param orderId 订单号
     * @param money 金额(分)
     * @param notifyUrl 回调地址
     * @param attach:附加数据
     * @return
     */
    public Map createNative(String orderId, Integer money, String notifyUrl, String... attach) {
        //第一步： 封装参数
        Map<String,String> param=new HashMap();
        param.put("appid", appid);                  //公众账号ID
        param.put("mch_id", partner);               //商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "我的商城");                 //商品描述
        param.put("out_trade_no", orderId);          //商户订单号
        param.put("total_fee",money+"");             //总金额（分）
        param.put("spbill_create_ip", "127.0.0.1");  //IP
        param.put("notify_url", notifyUrl );         //回调url
        param.put("trade_type", "NATIVE");           //交易类型
        if(attach!=null && attach.length>0){
            param.put("attach",attach[0]);
        }

        try {
            //第二步：向微信服务器发送请求
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println(xmlParam);
            HttpClient client=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();

            //第三步：获取结果
            String result = client.getContent();
            System.out.println(result);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String, String> map=new HashMap();
            map.put("code_url", resultMap.get("code_url"));//支付地址
            map.put("total_fee", money+"");     //总金额
            map.put("out_trade_no",orderId);    //订单号

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    /**
     * 查询支付状态
     * @param orderId 订单号
     * @return
     */
    public Map queryPayStatus(String orderId) {
        Map param=new HashMap();
        param.put("appid", appid);          //公众账号ID
        param.put("mch_id", partner);       //商户号
        param.put("out_trade_no", orderId); //订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());   //随机字符串
        String url="https://api.mch.weixin.qq.com/pay/orderquery";

        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
