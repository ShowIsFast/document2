package com.mymall.web.order.task;

import com.mymall.pojo.order.Order;
import com.mymall.contract.order.OrderService;

import net.sf.json.JSONObject;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.*;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class OrderTask {

    private static Logger log = LoggerFactory.getLogger(OrderTask.class);
    private static String strDateFormat = "yyyy-MM-dd HH:mm:ss";
    private static SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

    @DubboReference
    private OrderService orderService;

    public static void main(String[] args) {
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.HOUR, -2); //减填负数
        String findDate = sdf.format(calendar.getTime());
        System.out.println(findDate);
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void updateOrder(){
        log.info("[web_order][OrderTask][updateOrder][start]");
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.HOUR, -2); //减填负数
        String findDate = sdf.format(calendar.getTime());
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("orderStatus","0");
        searchMap.put("payStatus","0");
        searchMap.put("createTime",findDate);
        List<Order> list = orderService.findList(searchMap);
        if(null != list){
            for (Order o: list) {
                log.info("[web_order][OrderTask][updateOrder][o]"+ JSONObject.fromObject(o));
                o.setCloseTime(new Date());
                o.setOrderStatus("6");
                orderService.update(o);
            }
        }
    }

}
