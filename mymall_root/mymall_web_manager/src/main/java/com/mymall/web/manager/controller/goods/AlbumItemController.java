package com.mymall.web.manager.controller.goods;

import com.aliyun.oss.OSS;
import com.mymall.pojo.entity.PageResult;
import com.mymall.common.web.entity.Result;
import com.mymall.pojo.goods.AlbumItem;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.goods.AlbumItemService;
import com.mymall.contract.user.UserLogService;

import net.sf.json.JSONObject;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/albumitem")
@CrossOrigin
public class AlbumItemController {

    private static Logger log = LoggerFactory.getLogger(AlbumItemController.class);

    @DubboReference
    private AlbumItemService albumItemService;

    @GetMapping("/findAll")
    public List<AlbumItem> findAll(){
        return albumItemService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<AlbumItem> findPage(int page, int size){
        return albumItemService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<AlbumItem> findList(@RequestBody Map<String,Object> searchMap){
        return albumItemService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<AlbumItem> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        log.info("[mymall_web_manager][AlbumItemController][findPage][searchMap:]"+searchMap);
        return albumItemService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public AlbumItem findById(Long id){
        return albumItemService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody AlbumItem albumItem){
        albumItemService.add(albumItem);

        Map<String, Object> map = albumItemService.selectNewByOne();
        insertUserLog("插入图片明细数据",null,map);

        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody AlbumItem albumItem){
        log.info("[mymall_web_manager][AlbumItemController][update][albumItem:]"+albumItem);
        AlbumItem oldData = albumItemService.findById(albumItem.getId());
        albumItemService.update(albumItem);
        insertUserLog("修改图片明细数据",oldData,albumItem);

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id){
        log.info("[mymall_web_manager][AlbumItemController][delete][id:]"+id);
        AlbumItem byId = albumItemService.findById(id);
        AlbumItem newData = new AlbumItem();
        newData = byId;
        System.out.println("byId:"+byId);
        deleteOssImage(byId.getFileName());
        byId.setIsDelete("1");
        albumItemService.update(byId);
        insertUserLog("逻辑删除图片明细数据", newData, byId);

        return new Result();
    }

    @Value("${aliyun.bucketName}")
    private String bucketName;

    @Autowired
    private OSS ossClient;

    private void deleteOssImage(String images){
        // 删除文件或目录。如果要删除目录，目录必须为空。
        ossClient.deleteObject(bucketName, images);
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
