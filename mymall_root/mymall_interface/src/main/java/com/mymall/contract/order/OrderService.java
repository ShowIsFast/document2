package com.mymall.contract.order;

import com.mymall.pojo.order.Order;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.order.OrderFull;

import java.util.*;

/**
 * order业务逻辑层
 */
public interface OrderService {

    public List<Order> findAll();

    public PageResult<Order> findPage(int page, int size);

    public List<Order> findList(Map<String, Object> searchMap);

    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size);

    public Order findById(String id);

    //分布式事务入库入口
    public Map<String, Object> add(Order order);
    //入库到数据库
    public void insertOrder(Order order);

    public void update(Order order);

    public void delete(String id);

    /**
     * 批量发货
     * @param orders
     */
    public void batchSend(List<Order> orders);

    /**
     * 订单超时处理逻辑
     */
    public void orderTimeOutLogic();

    /**
     * 修改订单状态
     * @param orderId
     * @param transactionId
     */
    public void updatePayStatus(String orderId,String transactionId);

    OrderFull findOrderFullById(String id);

}
