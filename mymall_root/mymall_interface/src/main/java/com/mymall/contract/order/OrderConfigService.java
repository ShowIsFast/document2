package com.mymall.contract.order;

import com.mymall.pojo.order.OrderConfig;
import com.mymall.pojo.entity.PageResult;

import java.util.*;

/**
 * orderConfig业务逻辑层
 */
public interface OrderConfigService {

    public List<OrderConfig> findAll();

    public PageResult<OrderConfig> findPage(int page, int size);

    public List<OrderConfig> findList(Map<String, Object> searchMap);

    public PageResult<OrderConfig> findPage(Map<String, Object> searchMap, int page, int size);

    public OrderConfig findById(Integer id);

    public void add(OrderConfig orderConfig);

    public void update(OrderConfig orderConfig);

    public void delete(Integer id);

}
