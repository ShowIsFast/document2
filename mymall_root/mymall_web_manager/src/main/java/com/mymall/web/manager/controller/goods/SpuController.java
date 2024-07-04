package com.mymall.web.manager.controller.goods;

import com.aliyun.oss.OSS;
import com.mymall.common.web.entity.Result;
import com.mymall.pojo.goods.AlbumItem;
import com.mymall.pojo.goods.Goods;
import com.mymall.pojo.goods.Sku;
import com.mymall.pojo.goods.Spu;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.goods.AlbumItemService;
import com.mymall.contract.goods.SpuService;
import com.mymall.pojo.entity.PageResult;
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
@RequestMapping("/spu")
@CrossOrigin
public class SpuController {

    private static Logger log = LoggerFactory.getLogger(SpuController.class);

    @DubboReference
    private SpuService spuService;

    @DubboReference
    private AlbumItemService albumItemService;

    @PostMapping("/findAuditList")
    public PageResult<Spu> findAuditList(@RequestBody Map<String,Object> searchMap,String status,int page, int size) {
        searchMap.put("status",status);
        log.info("[mymall_web_manager][SpuController][findAuditList][request param][status:]"+status+"[page:]"+page+"[size:]"+size+"[searchMap:]"+JSONObject.fromObject(searchMap));

        return spuService.findPage(searchMap,page, size);
    }

    @GetMapping("/findUpsStatus")
    public String findUpsStatus(String status,String id){
        log.info("[mymall_web_manager][SpuController][findUpsStatus][request param][status:]"+status+"[id:]"+id);

        Spu byId = spuService.findById(id);
        Spu spu = byId;
        spu.setId(id);
        spu.setStatus(status);
        spuService.update(spu);

        insertUserLog("修改SPU信息",byId,spu);

        return responseMethod(new ArrayList<>(),"1","执行成功！");
    }

    @PostMapping("/update")
    public String update(@RequestBody Spu spu){
        Spu byId = spuService.findById(spu.getId());
        log.info("[mymall_web_manager][SpuController][update][request param][spu:]"+JSONObject.fromObject(spu));
        spuService.update(spu);
        insertUserLog("修改SPU信息",byId,spu);

        return responseMethod(new ArrayList<>(),"1","执行成功！");
    }

    @PostMapping("/add")
    public String add(@RequestBody Spu spu){
        log.info("[mymall_web_manager][SpuController][add][request param][spu:]"+JSONObject.fromObject(spu));
        spuService.add(spu);
        Map<String, Object> stringObjectMap = spuService.selectNewByOne();
        insertUserLog("新增SPU信息",null,stringObjectMap);

        return responseMethod(new ArrayList<>(),"1","执行成功！");
    }

    @GetMapping("/findAll")
    public List<Spu> findAll(){
        return spuService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Spu> findPage(int page, int size){
        return spuService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Spu> findList(@RequestBody Map<String,Object> searchMap){
        return spuService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Spu> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  spuService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Spu findById(String id){
        return spuService.findById(id);
    }

    @PostMapping("/save")
    public Result save(@RequestBody Goods goods){
        log.info("[mymall_web_manager][SpuController][save][request param][goods:]");
        log.info(goods.toString());

        String images = goods.getSpu().getImages();
        log.info("[mymall_web_manager][SpuController][save][images]"+images);
        if(null != images && !"".equals(images)){
            goods.getSpu().setImages(images.substring(0,images.length()-1));
        }

        spuService.saveGoods(goods);    //写 spu表和sku表
        insertUserLog("新增GOODS信息",null,goods);
        log.info("[mymall_web_manager][SpuController][save][response][success!]");

        Map<String,Object> map = new HashMap<>();
        map.put("images",goods.getSpu().getImages());
        log.info("[mymall_web_manager][SpuController][save][select goods  ][start] [map:]"+map);
        List<Spu> list = spuService.findList(map);
        goods.setSpu(list.get(0));
        log.info("[mymall_web_manager][SpuController][save][select goods  ][end] [spu:]"+list.get(0));

        log.info("[mymall_web_manager][SpuController][save][insert oss address ][start]");
        saveOssGoods(goods); //基于阿里的 OSS 来存储图片
        log.info("[mymall_web_manager][SpuController][save][insert oss address ][end]");

        return new Result();
    }

    @GetMapping("/delete")
    public String delete(String id){
        try {
            Spu byId = spuService.findById(id);
            log.info("[mymall_web_manager][SpuController][delete][start][id]:"+id);
            spuService.delete(id);
            log.info("[mymall_web_manager][SpuController][delete][end]");
            insertUserLog("删除SPU信息",byId,null);
            log.info("[mymall_web_manager][SpuController][delete][deleteOss][Object:]");
            log.info(byId.toString());
            String images = byId.getImages();
            String[] split = images.split("/");
            String a = split[split.length-1];
            String b = split[split.length-2];
            images = b+"/"+a;
            deleteOssImage(images);

            Map<String,Object> map = new HashMap<>();
            map.put("url",byId.getImages());
            List<AlbumItem> list = albumItemService.findList(map);
            if(list.size() > 0){
                AlbumItem albumItem = list.get(0);
                if(null != albumItem){
                    albumItem.setIsDelete("1");
                    albumItemService.update(albumItem);
                }
            }

            return  responseMethod(new ArrayList<>(),"1","执行成功！");
        }catch (Exception e){
            return responseMethod(new ArrayList<>(),"1","执行失败，请检查商品状态是否可删除！");
        }
    }

    @GetMapping("/logicDelete")
    public Result logicDelete(String id){
        Spu byId = spuService.findById(id);
        spuService.delete(id);
        insertUserLog("删除SPU信息",byId,null);

        return new Result();
    }

    @GetMapping("/restore")
    public Result restore(String id){
        Spu byId = spuService.findById(id);
        spuService.restore(id);
        Spu newData = spuService.findById(id);
        insertUserLog("恢复SPU信息",byId,newData);

        return new Result();
    }

    @GetMapping("/audit")
    public Result audit(String id){
        Spu byId = spuService.findById(id);
        spuService.audit(id);
        Spu newData = spuService.findById(id);
        insertUserLog("审核SPU信息",byId,newData);

        return new Result();
    }

    @GetMapping("/pull")
    public Result pull(String id){
        Spu byId = spuService.findById(id);
        spuService.pull(id);
        Spu newData = spuService.findById(id);
        insertUserLog("下架SPU信息",byId,newData);

        return new Result();
    }

    @GetMapping("/put")
    public Result put(String id){
        Spu byId = spuService.findById(id);
        spuService.put(id);
        Spu newData = spuService.findById(id);
        insertUserLog("上架SPU信息",byId,newData);

        return new Result();
    }

    @GetMapping("/putMany")
    public Result putMany(String[] ids){
        int count = spuService.putMany(ids);
        insertUserLog("批量上架SPU信息",null,ids);

        return new Result(0,"上架"+count+"个商品");
    }

    @GetMapping("/findGoodsById")
    public Goods findGoodsById(String id){
        return spuService.findGoodsById(id);
    }

    private String responseMethod(Object object,String code,String message){
        Map<String,Object> map = new HashMap<>();
        map.put("data",object);
        map.put("code",code);
        map.put("message",message);
        String toString = JSONObject.fromObject(map).toString();
        log.info("[mymall_web_manager][SpuController][responseSuccess][response body]");
        log.info(toString);

        return toString;
    }

    private void saveOssGoods(Goods goods){
        String spu_file_name;
        List<Sku> skuList = goods.getSkuList();
        Spu spu = goods.getSpu();
        log.info("====spu====:"+spu);
        Map<String,Object> map = new HashMap<>();

        log.info("===================spu=====================stare");
        String spuImages = spu.getImages();
        map.put("url",spuImages);
        log.info("spu_spuImages:"+spuImages);
        List<AlbumItem> list = albumItemService.findList(map);
        if(list.size() > 0){
            AlbumItem albumItem = list.get(0);
            albumItem.setType("1");
            albumItem.setTypeId(spu.getId());
            albumItem.setParentId("1");
            log.info("spu_albumItem："+albumItem);
            albumItemService.update(albumItem);
        }
        log.info("===================spu=====================end");

        log.info("===================sku=====================stare");
        log.info("skuList.size():"+skuList.size());
        for (int i = 0;i <skuList.size() ; i++) {
            map.put("url",skuList.get(i).getImage());
            log.info("skuList.get(i).getImage():"+skuList.get(i).getImage());
            List<AlbumItem> sk_list = albumItemService.findList(map);
            log.info("sk_list.size():"+sk_list.get(0));
            if(null != sk_list.get(0) && sk_list.get(0) != null){
                AlbumItem albumItem = sk_list.get(0);
                albumItem.setType("1");
                albumItem.setTypeId(spu.getId());
                albumItem.setParentId("1");
                log.info("sku_albumItem："+albumItem);
                albumItemService.update(albumItem);
            }
        }
        log.info("===================sku=====================end");
    }

    @Value("${aliyun.bucketName}")
    private String bucketName;

    @Autowired
    private OSS ossClient;

    private void deleteOssImage(String images){
        log.info("[mymall_web_manager][SpuController][deleteOssImage][start][images:]"+images);

        // 删除文件或目录。如果要删除目录，目录必须为空。
        ossClient.deleteObject(bucketName, images);

        log.info("[mymall_web_manager][SpuController][deleteOssImage][end]");
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
