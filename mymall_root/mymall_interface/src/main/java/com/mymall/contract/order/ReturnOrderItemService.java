package com.mymall.contract.order;

import com.mymall.pojo.order.ReturnOrderItem;
import com.mymall.pojo.entity.PageResult;

import java.util.*;

/**
 * returnOrderItem业务逻辑层
 */
public interface ReturnOrderItemService {

    public List<ReturnOrderItem> findAll();

    public PageResult<ReturnOrderItem> findPage(int page, int size);

    public List<ReturnOrderItem> findList(Map<String,Object> searchMap);

    public PageResult<ReturnOrderItem> findPage(Map<String,Object> searchMap,int page, int size);

    public ReturnOrderItem findById(Long id);

    public void add(ReturnOrderItem returnOrderItem);

    public void update(ReturnOrderItem returnOrderItem);

    public void delete(Long id);

}
