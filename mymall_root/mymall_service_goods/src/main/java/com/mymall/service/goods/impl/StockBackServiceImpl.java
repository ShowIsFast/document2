package com.mymall.service.goods.impl;

import com.mymall.pojo.goods.StockBack;
import com.mymall.contract.goods.StockBackService;
import com.mymall.service.goods.dao.SkuMapper;
import com.mymall.service.goods.dao.StockBackMapper;
import com.mymall.pojo.order.OrderItem;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@DubboService(interfaceClass = StockBackService.class)
public class StockBackServiceImpl implements StockBackService {

    @Autowired
    private StockBackMapper stockBackMapper;

    @Override
    public void add(StockBack stockBack) {
        stockBackMapper.insert(stockBack);
    }

    @Override
    @Transactional
    public void addList(List<OrderItem> orderItems) {
        for(OrderItem orderItem:orderItems){
            StockBack stockBack=new StockBack();
            stockBack.setOrderId(orderItem.getOrderId());
            stockBack.setSkuId(orderItem.getSkuId());
            stockBack.setStatus("0");
            stockBack.setNum(orderItem.getNum());
            stockBack.setCreateTime(new Date());
            stockBackMapper.insert(stockBack);
        }
    }

    @Autowired
    private SkuMapper skuMapper;

    @Override
    @Transactional
    public void doBack() {
        StockBack stockBack0=new StockBack();
        stockBack0.setStatus("0");
        List<StockBack> stockBacks = stockBackMapper.selectByExample(stockBack0);
        for(StockBack stockBack:stockBacks ){
            skuMapper.updateNum(stockBack.getSkuId(),stockBack.getNum());
            stockBack.setBackTime(new Date());
            stockBack.setStatus("1");
            stockBackMapper.updateByPrimaryKey(stockBack);
        }
    }

}
