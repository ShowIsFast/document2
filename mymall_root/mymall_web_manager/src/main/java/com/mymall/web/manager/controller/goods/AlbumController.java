package com.mymall.web.manager.controller.goods;

import com.mymall.common.web.entity.Result;
import com.mymall.pojo.goods.Album;
import com.mymall.contract.goods.AlbumService;
import com.mymall.pojo.entity.PageResult;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/album")
@CrossOrigin
public class AlbumController {

    private static Logger log = LoggerFactory.getLogger(AlbumController.class);

    @DubboReference
    private AlbumService albumService;

    @GetMapping("/findAll")
    public List<Album> findAll(){
        return albumService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Album> findPage(int page, int size){
        return albumService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Album> findList(@RequestBody Map<String,Object> searchMap){
        return albumService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Album> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  albumService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Album findById(Long id){
        return albumService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Album album){
        albumService.add(album);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Album album){
        log.info("[mymall_web_manager][SpuController][update][request param][album:]"+album);
        albumService.update(album);

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id){
        albumService.delete(id);

        return new Result();
    }

}
