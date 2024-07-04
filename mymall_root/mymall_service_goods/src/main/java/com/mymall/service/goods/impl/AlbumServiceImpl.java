package com.mymall.service.goods.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.service.goods.dao.AlbumItemMapper;
import com.mymall.pojo.goods.Album;
import com.mymall.pojo.goods.AlbumItem;
import com.mymall.contract.goods.AlbumService;
import com.mymall.service.goods.dao.AlbumMapper;
import com.mymall.pojo.entity.PageResult;

import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class AlbumServiceImpl implements AlbumService {

    private static Logger log = LoggerFactory.getLogger(AlbumService.class);

    @Autowired
    private AlbumMapper albumMapper;

    @Autowired
    private AlbumItemMapper albumItemMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Album> findAll() {
        return albumMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Album> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Album> albums = (Page<Album>) albumMapper.selectAll();

        return new PageResult<Album>(albums.getTotal(),albums.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Album> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);

        return albumMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Album> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Album> albums = (Page<Album>) albumMapper.selectByExample(example);

        return new PageResult<Album>(albums.getTotal(),albums.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Album findById(Long id) {
        return albumMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param album
     */
    public void add(Album album) {
        albumMapper.insert(album);
    }

    /**
     * 修改
     * @param album
     */
    public void update(Album album) {
        String image = album.getImage();
        log.info("[mymall_service_goods][AlbumServiceImpl][update][image:]"+image);
        if(null != image && !"".equals(image)){
            Map<String,Object> searchMap = new HashMap<>();
            searchMap.put("url",image);
            Example example = createItemExample(searchMap);
            List<AlbumItem> albumItems = albumItemMapper.selectByExample(example);
            log.info("[mymall_service_goods][AlbumServiceImpl][update][albumItems.size():]"+albumItems.size());
            if(albumItems.size() > 0){
                AlbumItem albumItem = albumItems.get(0);
                albumItem.setType("3");
                albumItem.setParentId("3");
                albumItemMapper.updateByPrimaryKeySelective(albumItem);
            }
        }

        albumMapper.updateByPrimaryKeySelective(album);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Long id) {
        albumMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Album.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 相册名称
            if(searchMap.get("title")!=null && !"".equals(searchMap.get("title"))){
                criteria.andLike("title","%"+searchMap.get("title")+"%");
            }
            // 相册封面
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 图片列表
            if(searchMap.get("imageItems")!=null && !"".equals(searchMap.get("imageItems"))){
                criteria.andLike("imageItems","%"+searchMap.get("imageItems")+"%");
            }
        }

        return example;
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createItemExample(Map<String, Object> searchMap){
        Example example=new Example(AlbumItem.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 图片访问地址
            if(searchMap.get("url")!=null && !"".equals(searchMap.get("url"))){
                criteria.andLike("url","%"+searchMap.get("url")+"%");
            }
            // bucketName
            if(searchMap.get("bucketName")!=null && !"".equals(searchMap.get("bucketName"))){
                criteria.andLike("bucketName","%"+searchMap.get("bucketName")+"%");
            }
            // 文件名称
            if(searchMap.get("fileName")!=null && !"".equals(searchMap.get("fileName"))){
                criteria.andLike("fileName","%"+searchMap.get("fileName")+"%");
            }
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andLike("isDelete","%"+searchMap.get("isDelete")+"%");
            }
        }

        return example;
    }

}
