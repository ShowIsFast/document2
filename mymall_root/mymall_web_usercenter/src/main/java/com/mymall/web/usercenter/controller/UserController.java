package com.mymall.web.usercenter.controller;

import com.mymall.pojo.entity.PageResult;
import com.mymall.common.web.entity.Result;
import com.mymall.pojo.order.Order;
import com.mymall.pojo.seckill.SeckillOrder;
import com.mymall.pojo.user.Address;
import com.mymall.pojo.user.User;
import com.mymall.contract.order.OrderService;
import com.mymall.contract.seckill.SeckillOrderService;
import com.mymall.contract.user.AddressService;
import com.mymall.contract.user.UserService;

import net.sf.json.JSONObject;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @DubboReference
    private UserService userService;

    @DubboReference
    private OrderService orderService;

    @DubboReference
    private SeckillOrderService seckillOrderService;

    @DubboReference
    private AddressService addressService;

    @GetMapping("/login")
    public void buyNew(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:8080/");
    }

    @GetMapping("loginhtml")
    public String loginhtml(Model model){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username",username);

        return "center-login";
    }

    @GetMapping("/findOrder")
    public String findOrder(Model model, @RequestParam Map<String, Object> searchMap,String username,String type){
        if(null == username || "".equals(username) || "anonymousUser".equals(username)){
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        if(null == username || "".equals(username) || "anonymousUser".equals(username)){
            username = "samuel";
        }

        searchMap.put("username",username);

        //秒杀订单
        if(null != type && type.equals("2")){
            PageResult<SeckillOrder> pageResult = seckillOrderService.findPage(1, 10);
            model.addAttribute("pageResult",pageResult);
        }else{
            PageResult<Order> pageResult = orderService.findPage(searchMap, 1, 10);
            model.addAttribute("pageResult",pageResult);
        }

        model.addAttribute("username",username);
        return "center-index";
    }

    @GetMapping("/findOrderNew")
    @ResponseBody
    public String findOrderNew(Integer page , Integer pageSize,String username,String type){
        Map returnMap = new HashMap();
        Map dataMap = new HashMap();
        Map<String, Object> searchMap = new HashMap<>();
        log.info("username====="+username);

        if(null == username || "".equals(username) || "anonymousUser".equals(username)){
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        if(null == username || "".equals(username) || "anonymousUser".equals(username)){
            username = "samuel";
        }

        log.info("username====="+username);

        searchMap.put("username",username);

        //秒杀订单
        if(null != type && type.equals("2")){
            PageResult<SeckillOrder> pageResult = seckillOrderService.findPage(searchMap, page, pageSize);
            dataMap.put("orderData",pageResult);
        }else{
            PageResult<Order> pageResult = orderService.findPage(searchMap, page, pageSize);
            dataMap.put("orderData",pageResult);
        }

        dataMap.put("username",username);
        returnMap.put("code","200");
        returnMap.put("message","请求成功！");
        returnMap.put("data",dataMap);

        return JSONObject.fromObject(returnMap).toString();
    }

    /**
     * 发送短信验证码
     * @param phone
     */
    @ResponseBody
    @GetMapping(value="/sendSms")
    public Result sendSms(String phone){
        userService.sendSms(phone);
        return new Result();
    }

    @ResponseBody
    @PostMapping("/save")
    public Result save(@RequestBody User user, String smsCode){
        //密码加密
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        //加密
        String newPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(newPassword);
        userService.add(user,smsCode);

        List<Address> list = new ArrayList<>();

        Address A = new Address();
        A.setAddress("永春武馆");
        A.setContact("李小龙");
        A.setPhone("11011011");
        A.setAlias("家里");
        A.setIsDefault("0");
        A.setUsername(user.getUsername());
        list.add(A);

        Address B = new Address();
        A.setAddress("咏春武馆总部");
        A.setContact("叶问");
        A.setPhone("999111");
        A.setAlias("师爷家");
        A.setIsDefault("0");
        A.setUsername(user.getUsername());
        list.add(B);

        Address C = new Address();
        A.setAddress("北京市昌平区");
        A.setContact("张三");
        A.setPhone("13301212233");
        A.setAlias("北京家");
        A.setIsDefault("1");
        A.setUsername(user.getUsername());
        list.add(C);
        for (Address are: list) {
            addressService.add(are);
        }

        return new Result();
    }

    @GetMapping("/register")
    public String register(Model model){
        return "register";
    }

    @GetMapping("/username")
    @ResponseBody
    public String username(String callback){
        log.info("=======callback:======"+callback);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map=new HashMap();
        map.put("username",username);

        return callback+"("+JSONObject.fromObject(map).toString()+")";
    }

    @GetMapping("/name")
    @ResponseBody
    public Map name(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(username.equals("anonymousUser")){
            username = "";
        }

        Map map=new HashMap();
        map.put("username",username);
        System.out.println("username："+username);

        return map;
    }

    @GetMapping("/getJSESessionId")
    @ResponseBody
    public void jsesessionid(){

    }

}
