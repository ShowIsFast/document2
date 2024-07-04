package com.mymall.web.order.controller;

import com.mymall.common.web.entity.Result;
import com.mymall.contract.order.CartService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static Logger log = LoggerFactory.getLogger(CartController.class);

    @DubboReference
    private CartService cartService;

    /**
     * 获取用户自己的购物车信息
     * @return
     */
    @GetMapping("/findCartList")
    public List<Map<String, Object>> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map<String, Object>> cartList = cartService.findCartList(username);

        return cartList;
    }

    @GetMapping("/deleteCheckedCart")
    public Result deleteCheckedCart(String skuId){
        log.info("[CartController][deleteCheckedCart][skuId:]"+skuId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        log.info("[CartController][deleteCheckedCart][username:]"+username);
        cartService.deleteSkuIds(username,skuId);

        return new Result();
    }

    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     */
    @GetMapping("/addItem")
    public Result addItem(String  skuId, Integer num){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addItem(username,skuId,num);

        return new Result();
    }

    /**
     * 测试时使用
     * @param response
     * @param skuId
     * @param num
     * @throws IOException
     */
    @GetMapping("/buy")
    public void buy(HttpServletResponse response, String  skuId, Integer num) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addItem(username,skuId,num);
        response.sendRedirect("/cart.html ");
    }

    @GetMapping("/buyNew")
    public Result buyNew(String  skuId, Integer num,String username) throws IOException {
        cartService.addItem(username,skuId,num);
        return new Result();
    }

    /**
     * 更改购物车项选中状态
     * @param skuId
     * @param checked
     * @return
     */
    @GetMapping("/updateChecked")
    public Result updateChecked(String skuId, boolean checked){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.updateChecked(username,skuId,checked);

        return new Result();
    }

    /**
     * 计算当前选中的购物车的优惠金额
     * @param
     * @return
     */
    @GetMapping("/preferential")
    public Map preferential(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int preferential = cartService.preferential(username);

        Map map=new HashMap();
        map.put("preferential",preferential);

        return map;
    }

}
