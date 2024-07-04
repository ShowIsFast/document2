package com.mymall.web.manager.controller.user;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.user.Address;
import com.mymall.contract.user.AddressService;
import com.mymall.pojo.entity.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/address")
public class AddressController {

    @DubboReference
    private AddressService addressService;

    @GetMapping("/findAll")
    public List<Address> findAll(){
        return addressService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Address> findPage(int page, int size){
        return addressService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Address> findList(@RequestBody Map<String,Object> searchMap){
        return addressService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Address> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  addressService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Address findById(Integer id){
        return addressService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Address address){
        addressService.add(address);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Address address){
        addressService.update(address);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        addressService.delete(id);
        return new Result();
    }

}
