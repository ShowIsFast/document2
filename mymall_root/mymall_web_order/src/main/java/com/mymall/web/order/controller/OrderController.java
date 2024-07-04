package com.mymall.web.order.controller;

import com.mymall.pojo.entity.PageResult;
import com.mymall.common.web.entity.Result;
import com.mymall.pojo.order.Order;
import com.mymall.pojo.user.Address;
import com.mymall.contract.order.CartService;
import com.mymall.contract.order.OrderService;
import com.mymall.contract.user.AddressService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @DubboReference
    private CartService cartService;

    @DubboReference
    private OrderService orderService;

    @DubboReference
    private AddressService addressService;

    /**
     * 获取刷新单价后的购物车列表
     * @return
     */
    @GetMapping("/findNewOrderItemList")
    public List<Map<String, Object>> findNewOrderItemList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return cartService.findNewOrderItemList(username);
    }

    /**
     * 根据用户名查询地址列表
     * @return
     */
    @GetMapping("/findAddressList")
    public List<Address> findAddressList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findByUsername(username);
    }

    /**
     * 删除选中的购物车
     */
    @GetMapping("/deleteCheckedCart")
    public Result deleteCheckedCart(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.deleteCheckedCart(username);

        return  new Result();
    }

    /**
     * 保存订单
     * @return
     */
    @PostMapping("/saveOrder")
    public Map<String, Object> saveOrder(@RequestBody Order order){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUsername(username);
        Map<String, Object> map = orderService.add(order);

        return map;
    }

    @GetMapping("/findAll")
    public List<Order> findAll(){
        return orderService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Order> findPage(int page, int size){
        return orderService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Order> findList(@RequestBody Map<String,Object> searchMap){
        return orderService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Order> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  orderService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Order findById(String id){
        return orderService.findById(id);
    }

}
