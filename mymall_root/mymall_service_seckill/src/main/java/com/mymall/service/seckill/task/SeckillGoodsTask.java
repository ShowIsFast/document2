package com.mymall.service.seckill.task;

import com.mymall.service.seckill.dao.SeckillGoodsMapper;
import com.mymall.pojo.seckill.SeckillGoods;
import com.mymall.common.util.DateUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration      // 标记配置类，兼备 Component
@EnableScheduling   // 开启定时任务
public class SeckillGoodsTask {

    private static Logger log = LoggerFactory.getLogger(SeckillGoodsTask.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    public static void main(String[] args) {
        List<Date> dateMenus = DateUtil.getDateMenus();

        for (Date startTime : dateMenus) {
            String s1 = DateUtil.date2Str(startTime);
            s1 = s1.replace("<", "");
            s1 = s1.replace(">", "");

            log.info("startTime:"+sdf.format(startTime));
            log.info("endTime:"+sdf.format(DateUtil.addDateHour(startTime,2)));
            log.info(s1);
        }
    }

    /**
     * 每10秒执行一次的定时任务
     * 秒杀的前置任务：将待要秒杀的商品从库中读入到 redis
     * 待要秒杀的商品：状态为 1，库存 > 0，时间区间开始时间 <= 秒杀开始时间， 时间区间 + 2h > 秒杀结束时间
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void loadGoods(){
        log.info("当前时间: "+sdf.format(getDaDate()));

        // 1.查询所有时间区间
        List<Date> dateMenus = DateUtil.getDateMenus();

        // 2.循环时间区间，查询每个时间区间的秒杀商品
        for (Date startTime : dateMenus) {
            String s1 = DateUtil.date2Str(startTime);
            s1=s1.replace("<","");
            s1=s1.replace(">","");

            // 过滤 redis 中已经存在的该区间的秒杀商品
            Set set = redisTemplate.boundHashOps("SeckillGoods_" + s1).keys();

            String startTimes = sdf.format(startTime);
            String endTimes = sdf.format(DateUtil.addDateHour(startTime, 2));
            String keys = "";
            String notIn = "";
            if(set!=null && set.size()>0){
                Iterator<String> it = set.iterator();
                while (it.hasNext()) {
                    keys += String.valueOf(it.next() )+ ",";
                }
                keys=keys.substring(0,keys.length()-1);
                notIn = " AND id NOT IN (" +keys+")";
            }

            String sql = "SELECT id,goods_id goodsId,item_id itemId,title,small_pic smallPic," +
                    "price,cost_price costPrice,seller_id sellerId,create_time create_Time," +
                    "check_time checkTime,`status`,start_time startTime,end_time endTime,num," +
                    "stock_count stockCount,introduction  FROM tb_seckill_goods  \n" +
                    "WHERE ( STATUS = 1 AND stock_count > 0 AND start_time >= '"+startTimes+"' AND end_time < '"+endTimes+"' )"+notIn;

            log.info("sql:" + sql);

            List<SeckillGoods> seckillGoods =seckillGoodsMapper.selectSeckillGoodsList(sql);
            log.info("seckillGoods.size:"+seckillGoods.size());

            // 3.将秒杀商品存入到 redis 缓存
            for (SeckillGoods seckillGood : seckillGoods) {
                log.info("seckillGood:"+seckillGood);

                // hash 结构
                // key-SeckillGoods_s1
                // field-秒杀商品记录id
                // value-秒杀商品信息seckillGood
                redisTemplate.boundHashOps("SeckillGoods_"+s1).put(seckillGood.getId(),seckillGood);

                //将商品剩余库存放到 redis，用来解决超卖问题
                //假设商品 id 是 9，库存是 3，那么 redis 的 list 的 key 是  SeckillGoodsCountList_9，元素是 [9,9,9]
                Long[] ids = pushIds(seckillGood.getStockCount(), seckillGood.getId());
                redisTemplate.boundListOps("SeckillGoodsCountList_"+seckillGood.getId()).leftPushAll(ids);

                //创建自定 key 的值、解决两个线程抢单实际库存为0但是不入mysql的问题
                //redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillGood.getId(),seckillGood.getStockCount());
            }
        }
    }

    /**
     * 获取系统当前时间
     * @return Date
     */
    public static Date getDaDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //设置为东八区
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date date = new Date();
        String dateStr = sdf.format(date);

        //将字符串转成时间
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate=null;
        try {
            newDate = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }

    /**
     * 将商品ID，按库存数量组装成数组，数组每个元素为商品id
     * @param len:库存
     * @param id：商品Iid
     * @return
     */
    public Long[] pushIds(int len, Long id){
        Long[] ids = new Long[len];
        for (int i = 0; i <len ; i++) {
            ids[i]=id;
        }

        return ids;
    }

}
