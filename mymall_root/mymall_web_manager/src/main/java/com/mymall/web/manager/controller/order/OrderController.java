package com.mymall.web.manager.controller.order;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.order.Order;
import com.mymall.pojo.order.OrderItem;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.order.OrderService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.order.OrderFull;
import com.mymall.contract.user.UserLogService;

import net.sf.json.JSONObject;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static Logger log = LoggerFactory.getLogger(OrderController.class);

    @DubboReference
    private OrderService orderService;

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

    @PostMapping("/add")
    public Result add(@RequestBody Order order){
        Map<String, Object> add = orderService.add(order);
        Order newOrder = (Order) add.get("orderData");
        insertUserLog("插入订单数据",null,newOrder);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Order order){
        Order byId = orderService.findById(order.getId());
        orderService.update(order);
        insertUserLog("修改订单数据",byId,order);

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id){
        Order byId = orderService.findById(id);
        orderService.delete(id);
        insertUserLog("删除订单数据",byId,null);

        return new Result();
    }

    @GetMapping("/orderFull")
    public OrderFull findOrderFullById(String id) {
        OrderFull orderFull = orderService.findOrderFullById(id);
        Integer goodsAll = 0;
        for (OrderItem i : orderFull.getOrderItems()) {
            goodsAll += i.getPayMoney();
        }
        orderFull.setOrder(pushOrderData(orderFull.getOrder(),String.valueOf(goodsAll)));

        return orderFull;
    }

    @GetMapping("/updateTheGoods")
    public Result updateTheGoods(String id){
        Order byId = orderService.findById(id);
        Order oldData = byId;
        byId.setOrderStatus("1");
        orderService.update(byId);
        insertUserLog("修改订单数据",oldData,byId);

        return new Result();
    }

    @PostMapping("/batchSend")
    public Result batchSend(@RequestBody List<Order> orders){
        orderService.batchSend(orders);
        //循环订单
        for(Order order :orders){
            order.setOrderStatus("3");//订单状态  已发货
            order.setConsignStatus("2"); //发货状态  已发货
            order.setConsignTime(new Date());//发货时间
        }
        insertUserLog("批量修改订单数据",null,orders);

        return new Result();
    }

    @GetMapping("/orderTimeOutLogic")
    public Result orderTimeOutLogic(){
        orderService.orderTimeOutLogic();
        return new Result();
    }

    private OrdedrItemAll pushOrderData(Order order,String goodsAll){
        OrdedrItemAll ordedrItemAll = new OrdedrItemAll();

        ordedrItemAll.setGoodsMoney(goodsAll);
        ordedrItemAll.setId(order.getId());
        ordedrItemAll.setOrderStatus(order.getOrderStatus());
        ordedrItemAll.setConsignStatus(order.getConsignStatus());
        ordedrItemAll.setOrderItemList(order.getOrderItemList());
        ordedrItemAll.setPayStatus(order.getPayStatus());
        ordedrItemAll.setCloseTime(order.getCloseTime());
        ordedrItemAll.setConsignTime(order.getConsignTime());
        ordedrItemAll.setCreateTime(order.getCreateTime());
        ordedrItemAll.setPayMoney(order.getPayMoney());
        ordedrItemAll.setPayTime(order.getPayTime());
        ordedrItemAll.setPreMoney(order.getPreMoney());
        ordedrItemAll.setTotalMoney(order.getTotalMoney());
        ordedrItemAll.setTotalNum(order.getTotalNum());
        ordedrItemAll.setTransactionId(order.getTransactionId());
        ordedrItemAll.setUpdateTime(order.getUpdateTime());
        ordedrItemAll.setUsername(order.getUsername());
        ordedrItemAll.setBuyerMessage(order.getBuyerMessage());
        ordedrItemAll.setBuyerRate(order.getBuyerRate());
        ordedrItemAll.setEndTime(order.getEndTime());
        ordedrItemAll.setIsDelete(order.getIsDelete());
        ordedrItemAll.setPayType(order.getPayType());
        ordedrItemAll.setPostFee(order.getPostFee());
        ordedrItemAll.setReceiverAddress(order.getReceiverAddress());
        ordedrItemAll.setReceiverContact(order.getReceiverContact());
        ordedrItemAll.setReceiverMobile(order.getReceiverMobile());
        ordedrItemAll.setShippingCode(order.getShippingCode());
        ordedrItemAll.setShippingName(order.getShippingName());
        ordedrItemAll.setSourceType(order.getSourceType());

        return ordedrItemAll;
    }

    class OrdedrItemAll extends Order{

        private String goodsMoney;

        public String getGoodsMoney() { return goodsMoney; }

        public void setGoodsMoney(String goodsMoney) { this.goodsMoney = goodsMoney; }

    }

    @DubboReference
    private UserLogService userLogService;

    /**
     * instructions:说明
     * oldData:操作前数据
     * newData:操作后数据
     * */
    private void insertUserLog(String instructions,Object oldData,Object newData){
        log.info("[insertUserLog][insert][userLog:]");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserLog userLog = new UserLog();
        userLog.setUsername(username);
        userLog.setInstructions(instructions);

        if(oldData != null && !"".equals(String.valueOf(oldData))){
            userLog.setOldData(JSONObject.fromObject(oldData).toString());
        }

        if(newData != null && !"".equals(String.valueOf(newData))){
            userLog.setNewData(JSONObject.fromObject(newData).toString());
        }

        userLog.setUpdateTime(new Date());
        log.info(userLog.toString());

        userLogService.add(userLog);
    }

}
