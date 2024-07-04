package com.mymall.web.manager.controller.goods;

import com.aliyun.oss.OSS;
import com.mymall.common.web.entity.Result;
import com.mymall.pojo.goods.AlbumItem;
import com.mymall.pojo.goods.Brand;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.goods.AlbumItemService;
import com.mymall.contract.goods.BrandService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.contract.user.UserLogService;

import net.sf.json.JSONObject;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/brand")
@CrossOrigin
public class BrandController {

    private static Logger log = LoggerFactory.getLogger(BrandController.class);

    @DubboReference
    private BrandService brandService;

    @DubboReference
    private AlbumItemService albumItemService;

    @PreAuthorize("hasAuthority('brand')")
    @GetMapping("/findAll")
    public List<Brand> findAll(){
        return brandService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Brand> findPage(int page, int size){
        return brandService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Brand> findList(@RequestBody Map<String,Object> searchMap){
        return brandService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Brand> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  brandService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Brand findById(Integer id){
        return brandService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Brand brand){
        brandService.add(brand);

        Map<String,Object> map = new HashMap<>();
        map.put("image",brand.getImage());
        List<Brand> list = brandService.findList(map);
        Brand brand1 = list.get(0);
        insertUserLog("新增品牌信息",null,brand1);
        log.info("[mymall_web_manager][BrandController][add][insert tb_alvum_item][object:]");
        log.info(brand1.toString());

        if(null != brand1 && brand1 != null){
            saveOssImages(brand1);
        }

        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Brand brand){
        Brand byId = brandService.findById(brand.getId());
        brandService.update(brand);

        Map<String,Object> map = new HashMap<>();
        map.put("image",brand.getImage());
        insertUserLog("修改品牌信息",byId,brand);
        log.info("[mymall_web_manager][BrandController][update][insert tb_alvum_item][object:]");
        log.info(brand.toString());
        if(null != brand && brand != null){
            updateOssIsDdelete(byId);
            saveOssImages(brand);
        }

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        Brand byId = brandService.findById(id);
        log.info("[mymall_web_manager][BrandController][delete][start][id]:"+id);

        brandService.delete(id);

        log.info("[mymall_web_manager][BrandController][delete][end]");
        insertUserLog("删除品牌信息",byId,null);

        log.info("[mymall_web_manager][BrandController][delete][deleteOss][Object:]");
        log.info(byId.toString());

        String images = byId.getImage();
        String[] split = images.split("/");
        String a = split[split.length-1];
        String b = split[split.length-2];
        images = b+"/"+a;
        deleteOssImage(images);

        Map<String,Object> map = new HashMap<>();
        map.put("url",byId.getImage());
        List<AlbumItem> list = albumItemService.findList(map);
        if(list.size() > 0){
            AlbumItem albumItem = list.get(0);
            if(null != albumItem){
                albumItem.setIsDelete("1");
                albumItemService.update(albumItem);
            }
        }

        return new Result();
    }

    @GetMapping("/findByCategoryId")
    public List<Brand> findByCategoryId(Integer categoryId){
        return brandService.findByCategoryId(categoryId);
    }

    private void saveOssImages(Brand brand){
        log.info("[mymall_web_manager][BrandController][saveOssImages][satrt]");

        Map<String,Object> map = new HashMap<>();
        map.put("url",brand.getImage());
        List<AlbumItem> brand_list = albumItemService.findList(map);
        log.info("[mymall_web_manager][BrandController][saveOssImages][brand_list.size]"+brand_list.size());

        if(null != brand_list.get(0) && brand_list.get(0) != null){
            log.info("[mymall_web_manager][BrandController][saveOssImages][brand_list.get(0)]"+brand_list.get(0));
            AlbumItem albumItem = brand_list.get(0);
            albumItem.setType("2");//品牌类型
            albumItem.setTypeId(String.valueOf(brand.getId()));
            albumItem.setParentId("2");//品牌类型
            log.info("sku_albumItem："+albumItem);
            albumItemService.update(albumItem);
        }
    }

    private void updateOssIsDdelete(Brand brand){
        log.info("[mymall_web_manager][BrandController][updateOssIsDdelete][satrt][Object:]");
        log.info(brand.toString());

        Map<String,Object> map = new HashMap<>();
        map.put("url",brand.getImage());
        List<AlbumItem> brand_list = albumItemService.findList(map);
        log.info("[mymall_web_manager][BrandController][updateOssIsDdelete][brand_list.size]"+brand_list.size());
        if(null != brand_list.get(0) && brand_list.get(0) != null){
            log.info("[mymall_web_manager][BrandController][updateOssIsDdelete][brand_list.get(0)]"+brand_list.get(0));
            AlbumItem albumItem = brand_list.get(0);
            albumItem.setIsDelete("1");
            log.info("sku_albumItem："+albumItem);
            albumItemService.update(albumItem);

            deleteOssImage(albumItem.getFileName());
        }
    }

    @Value("${aliyun.bucketName}")
    private String bucketName;

    @Autowired
    private OSS ossClient;

    private void deleteOssImage(String images){
        log.info("[mymall_web_manager][BrandController][deleteOssImage][start][images:]"+images);

        // 删除文件或目录。如果要删除目录，目录必须为空。
        ossClient.deleteObject(bucketName, images);

        log.info("[mymall_web_manager][BrandController][deleteOssImage][end]");
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
