package com.mymall.web.manager.controller.goods;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.goods.Category;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.goods.CategoryService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.contract.user.UserLogService;

import net.sf.json.JSONObject;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryController {

    private static Logger log = LoggerFactory.getLogger(CategoryController.class);

    @DubboReference
    private CategoryService categoryService;

    @GetMapping("/findAll")
    public List<Category> findAll(){
        return categoryService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Category> findPage(int page, int size){
        return categoryService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Category> findList(@RequestBody Map<String,Object> searchMap){
        return categoryService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Category> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  categoryService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Category findById(Integer id){
        return categoryService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Category category){
        categoryService.add(category);
        Map<String, Object> stringObjectMap = categoryService.selectNewByOne();
        insertUserLog("新增商品分类信息",null,stringObjectMap);

        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Category category){
        Category byId = categoryService.findById(category.getId());
        categoryService.update(category);
        insertUserLog("修改商品分类信息",byId,category);

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        Category byId = categoryService.findById(id);
        categoryService.delete(id);
        insertUserLog("修删除商品分类信息",byId,null);

        return new Result();
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
