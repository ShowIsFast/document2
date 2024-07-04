package com.mymall.web.manager.controller.business;

import com.aliyun.oss.OSS;
import com.mymall.common.web.entity.Result;
import com.mymall.pojo.goods.AlbumItem;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.business.AdService;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.business.Ad;
import com.mymall.contract.goods.AlbumItemService;
import com.mymall.contract.goods.BrandService;
import com.mymall.contract.user.UserLogService;

import net.sf.json.JSONObject;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/ad")
public class AdController {

    private static Logger log = LoggerFactory.getLogger(AdController.class);

    @DubboReference
    private AdService adService;

    @DubboReference
    private UserLogService userLogService;

    @DubboReference
    private BrandService brandService;

    @DubboReference
    private AlbumItemService albumItemService;

    @GetMapping("/findAll")
    public List<Ad> findAll(){
        return adService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Ad> findPage(int page, int size){
        return adService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Ad> findList(@RequestBody Map<String,Object> searchMap){
        return adService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Ad> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  adService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Ad findById(Integer id){
        return adService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Ad ad){
        adService.add(ad);

        Map<String,Object> map = new HashMap<>();
        map.put("image",ad.getImage());
        List<Ad> list = adService.findList(map);
        Ad ad1 = list.get(0);
        insertUserLog("新增广告信息",null,ad);
        log.info("[mymall_web_manager][BrandController][add][insert tb_alvum_item][object:]");
        log.info(ad1.toString());
        if(null != ad1 && ad1 != null){
            saveOssImages(ad1);
        }

        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Ad ad){
        Ad byId = adService.findById(ad.getId());
        adService.update(ad);

        Map<String,Object> map = new HashMap<>();
        map.put("image",byId.getImage());
        insertUserLog("修改广告信息",byId,ad);
        log.info("[mymall_web_manager][BrandController][update][insert tb_alvum_item][object:]");
        log.info(byId.toString());
        if(null != byId && byId != null){
            updateOssIsDdelete(byId);
            saveOssImages(byId);
        }

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        Ad byId = adService.findById(id);
        adService.delete(id);
        insertUserLog("删除广告信息",byId,null);

        return new Result();
    }

    @GetMapping("/upStatus")
    public Result upStatus(Integer id,String status){
        Ad byId = adService.findById(id);
        byId.setStatus(status);
        adService.update(byId);

        return new Result();
    }

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

    private void saveOssImages(Ad ad1){
        log.info("[mymall_web_manager][AdController][saveOssImages][satrt]");

        Map<String,Object> map = new HashMap<>();
        map.put("url",ad1.getImage());
        List<AlbumItem> ad_list = albumItemService.findList(map);

        log.info("[mymall_web_manager][AdController][saveOssImages][ad_list.size]"+ad_list.size());
        if(null != ad_list.get(0) && ad_list.get(0) != null){
            log.info("[mymall_web_manager][AdController][saveOssImages][ad_list.get(0)]"+ad_list.get(0));
            AlbumItem albumItem = ad_list.get(0);
            albumItem.setType("4");//广告类型
            albumItem.setTypeId(String.valueOf(ad1.getId()));
            albumItem.setParentId("4");//广告类型
            log.info("sku_albumItem："+albumItem);

            albumItemService.update(albumItem);
        }
    }

    private void updateOssIsDdelete(Ad ad1){
        log.info("[mymall_web_manager][AdController][updateOssIsDdelete][satrt][Object:]");
        log.info(ad1.toString());

        Map<String,Object> map = new HashMap<>();
        map.put("url",ad1.getImage());
        List<AlbumItem> ad_list = albumItemService.findList(map);

        log.info("[mymall_web_manager][AdController][updateOssIsDdelete][ad_list.size]"+ad_list.size());
        if(null != ad_list.get(0) && ad_list.get(0) != null){
            log.info("[mymall_web_manager][AdController][updateOssIsDdelete][ad_list.get(0)]"+ad_list.get(0));
            AlbumItem albumItem = ad_list.get(0);
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
        log.info("[mymall_web_manager][AdController][deleteOssImage][start][images:]"+images);

        // 删除文件或目录。如果要删除目录，目录必须为空。
        ossClient.deleteObject(bucketName, images);

        log.info("[mymall_web_manager][AdController][deleteOssImage][end]");
    }

}
