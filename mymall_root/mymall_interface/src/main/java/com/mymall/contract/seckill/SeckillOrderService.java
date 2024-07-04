package com.mymall.contract.seckill;

import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.entity.Result;
import com.mymall.pojo.entity.SeckillStatus;
import com.mymall.pojo.seckill.SeckillOrder;

import java.util.List;
import java.util.Map;

public interface SeckillOrderService {

    public List<SeckillOrder> findAll();

    public PageResult<SeckillOrder> findPage(int page, int size);

    public List<SeckillOrder> findList(Map<String,Object> searchMap);

    public PageResult<SeckillOrder> findPage(Map<String,Object> searchMap,int page, int size);

    public SeckillOrder findById(String id);

    public void add(SeckillOrder seckillOrder);

    public void update(SeckillOrder seckillOrder);

    public void delete(String id);

    /****
     * 根据用户名查询订单后
     * @param username
     */
    SeckillOrder queryByUserName(String username);

    /***
     * 修改订单
     * @param outtradeno
     * @param username
     * @param transactionid
     */
    void updateStatus(String outtradeno,String username,String transactionid);

    /***
     * 查询抢单状态
     * @param username
     */
    SeckillStatus queryStatus(String username);


    /****
     * 下单实现
     * @param id:商品ID
     * @param time:商品时区
     * @param username:用户名
     * @return
     */
    Result add(Long id, String time, String username);

}
