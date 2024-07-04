package com.mymall.web.manager.controller.order;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.order.ReturnOrder;
import com.mymall.contract.order.ReturnOrderService;
import com.mymall.pojo.entity.PageResult;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/returnOrder")
public class ReturnOrderController {

    @DubboReference
    private ReturnOrderService returnOrderService;

    @GetMapping("/findAll")
    public List<ReturnOrder> findAll(){
        return returnOrderService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<ReturnOrder> findPage(int page, int size){
        return returnOrderService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<ReturnOrder> findList(@RequestBody Map<String,Object> searchMap){
        return returnOrderService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<ReturnOrder> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  returnOrderService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public ReturnOrder findById(Long id){
        return returnOrderService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody ReturnOrder returnOrder){
        returnOrderService.add(returnOrder);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody ReturnOrder returnOrder){
        returnOrderService.update(returnOrder);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id){
        returnOrderService.delete(id);
        return new Result();
    }

    /**
     * 同意退款
     * @param id
     * @param money
     */
    public Result agreeRefund(Long id, Integer money){
        Integer adminId=0;//获取当前登陆人ID
        returnOrderService.agreeRefund(id,money,adminId);

        return new Result();
    }

    /**
     * 驳回退款
     * @param id
     * @param remark
     */
    public Result rejectRefund(Long id, String remark){
        Integer adminId=0;//获取当前登陆人ID
        returnOrderService.rejectRefund(id,remark,adminId);

        return new Result();
    }

}
