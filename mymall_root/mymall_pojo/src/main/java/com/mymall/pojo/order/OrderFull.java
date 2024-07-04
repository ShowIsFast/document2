package com.mymall.pojo.order;

import java.io.Serializable;
import java.util.List;

public class OrderFull implements Serializable {

    private Order order;

    private OrderItem orderItem;

    private List<OrderItem> orderItems;

    private String goodsMoney;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public List<OrderItem> getOrderItems() { return orderItems; }

    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public String getGoodsMoney() { return goodsMoney; }

    public void setGoodsMoney(String goodsMoney) { this.goodsMoney = goodsMoney; }

}
