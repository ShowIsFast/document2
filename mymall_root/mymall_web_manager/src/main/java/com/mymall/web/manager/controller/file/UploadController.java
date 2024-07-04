package com.mymall.web.manager.controller.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private static Logger log = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private HttpServletRequest request;

    @DubboReference
    private AlbumItemService albumItemService;

    @Value("${aliyun.bucketName}")
    private String bucketName;

    @Value("${aliyun.urlPrefix}")
    private String urlPrefix;

    @PostMapping("/native")
    public String nativeUpload(@RequestParam("file") MultipartFile file) {
        String path=request.getSession().getServletContext().getRealPath("img");
        String filePath = path +"/"+ file.getOriginalFilename();
        File desFile = new File(filePath);
        if(!desFile.getParentFile().exists()){
            desFile.mkdirs();
        }

        try {
            file.transferTo(desFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("path:---"+filePath);

        return "http://localhost:9101/img/"+file.getOriginalFilename();
    }

    @Autowired
    private OSS ossClient;

    @PostMapping("/oss")
    public String ossUpload(@RequestParam("file") MultipartFile file,String folder){
        String uuid = UUID.randomUUID()+"_"+file.getOriginalFilename();
        String fileName= folder+"/"+ uuid;
        try {
            ossClient.putObject(bucketName, fileName, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = "http://"+bucketName+"."+ ((OSSClient)ossClient).getEndpoint().toString().replace("http://","") +"/"+fileName;
        log.info("[UploaController][oss][访问地址：]"+url);

        //保存至图片库
        AlbumItem albumItem = new AlbumItem();
        albumItem.setBucketName(bucketName);
        albumItem.setFileName(fileName);
        albumItem.setUrl(url);
        albumItem.setParentId("100001");
        albumItem.setIsDelete("0");
        albumItemService.add(albumItem);
        Map<String, Object> stringObjectMap = albumItemService.selectNewByOne();
        insertUserLog("新增上传图片",null,stringObjectMap);
        log.info("[mymall_web_manager][UploadConterller][ossUpload][insert tb_alvum_item][object:]");
        log.info(String.valueOf(albumItem));

        return url;
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
