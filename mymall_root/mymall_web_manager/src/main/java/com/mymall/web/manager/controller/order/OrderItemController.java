package com.mymall.web.manager.controller.order;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.order.Order;
import com.mymall.contract.order.OrderItemService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.order.OrderItem;
import com.mymall.contract.order.OrderService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/orderItem")
public class OrderItemController {

    @DubboReference
    private OrderItemService orderItemService;

    @DubboReference
    private OrderService orderService;

    @GetMapping("/findAll")
    public List<OrderItem> findAll(){
        return orderItemService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<OrderItem> findPage(int page, int size){
        return orderItemService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<OrderItem> findList(@RequestBody Map<String,Object> searchMap){
        return orderItemService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<OrderItem> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  orderItemService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public OrderItem findById(String id){
        return orderItemService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody OrderItem orderItem){
        orderItemService.add(orderItem);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody OrderItem orderItem){
        orderItemService.update(orderItem);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id){
        orderItemService.delete(id);
        return new Result();
    }

    @GetMapping("/orderItemAll")
    public void orderItemAll(String id){
        Order order = orderService.findById(id);
        Map<String,Object> map = new HashMap<>();
        map.put("orderId",order.getId());
        List<OrderItem> list = orderItemService.findList(map);
    }

}
