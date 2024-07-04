package com.mymall.web.portal.controller;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.user.User;
import com.mymall.contract.user.UserService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

   @DubboReference
    private UserService userService;

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
        userService.add(user,smsCode);
        return new Result();
    }

    @GetMapping("/register")
    public String register(Model model){
        return "register.html";
    }

}
