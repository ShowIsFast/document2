package com.mymall.web.manager.controller.order;

import com.mymall.pojo.goods.Category;
import com.mymall.pojo.order.CategoryReport;
import com.mymall.contract.goods.CategoryService;
import com.mymall.contract.order.CategoryReportService;
import com.mymall.contract.user.UserService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categoryReport")
public class CategoryReportController {

    @DubboReference
    private CategoryReportService categoryReportService;

    @DubboReference
    private UserService userService;

    /**
     * 昨天的数据统计(测试)
     * @return
     */
    @GetMapping("/yesterday")
    public List<CategoryReport> yesterday(){
        LocalDate localDate = LocalDate.now().minusDays(1);// 得到昨天的日期
        return categoryReportService.categoryReport(localDate);
    }

    /**
     * 统计任务(每天凌晨1点开始统计昨天数据)
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void createDataTask(){
        System.out.println("任务执行了。");
        categoryReportService.createData();
    }

    @DubboReference
    private CategoryService categoryService;

    /**
     * 统计一级类目
     * @param date1
     * @param date1
     * @return
     */
    @GetMapping("/category1Count")
    public List<Map> category1Count(String date1 ,String date2){
        if(date1 == null || "".equals(date1)){
            date1="2019-01-01 00:00:00";
        }

        if(date2 == null || "".equals(date2)){
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss sss");
            date2 = df.format(new Date());
        }

        Map map=new HashMap();
        map.put("parentId",0);
        List<Category> categoryList = categoryService.findList(map);//查询一级分类列表
        Map<Integer, String> categoryMap = categoryList.stream().collect(Collectors.toMap(Category::getId,Category::getName ));

        List<Map> categoryReports = categoryReportService.category1Count(date1, date2);
        for(Map  report:categoryReports){
            String categoryName = categoryMap.get((Integer)report.get("categoryId1"));
            report.put("categoryName",categoryName);//追加名称
        }

        return categoryReports;
    }

    @GetMapping("/categoryUser")
    public List<Map<String,Object>> categoryUser(){
        List<Map<String,Object>> categoryReports = userService.categoryUser();
        Integer allCount = 0;

        for (Map<String,Object> map: categoryReports) {
            allCount += Integer.parseInt(String.valueOf(map.get("userCount")));
        }

        for (Map<String,Object> map: categoryReports) {
           map.put("allCount",allCount);
        }

        return categoryReports;
    }

}
