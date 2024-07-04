package com.mymall.service.goods.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.service.goods.dao.AlbumItemMapper;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.goods.AlbumItem;
import com.mymall.contract.goods.AlbumItemService;

import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@DubboService
public class AlbumItemImpl implements AlbumItemService {

    private static Logger log = LoggerFactory.getLogger(AlbumItemImpl.class);

    @Autowired
    private AlbumItemMapper albumItemMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<AlbumItem> findAll() {
        return albumItemMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<AlbumItem> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<AlbumItem> albumsItem = (Page<AlbumItem>) albumItemMapper.selectAll();

        return new PageResult<AlbumItem>(albumsItem.getTotal(),albumsItem.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<AlbumItem> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);

        return albumItemMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<AlbumItem> findPage(Map<String, Object> searchMap, int page, int size) {
        log.info("[mymall_service_goods][AlbumItemImpl][findPage][searchMap:]"+searchMap);
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<AlbumItem> albumsItem = (Page<AlbumItem>) albumItemMapper.selectByExample(example);

        return new PageResult<AlbumItem>(albumsItem.getTotal(),albumsItem.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public AlbumItem findById(Long id) {
        return albumItemMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param albumItem
     */
    public void add(AlbumItem albumItem) {
        albumItemMapper.insert(albumItem);
    }

    /**
     * 修改
     * @param albumItem
     */
    public void update(AlbumItem albumItem) {
        albumItemMapper.updateByPrimaryKeySelective(albumItem);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Long id) {
        albumItemMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Map<String,Object> selectNewByOne() {
        return albumItemMapper.selectNewByOne();
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
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
                criteria.andEqualTo("isDelete",String.valueOf(searchMap.get("isDelete")));
            }
            // 相册ID
            if(searchMap.get("parentId")!=null && !"".equals(searchMap.get("parentId"))){
                criteria.andEqualTo("parentId",String.valueOf(searchMap.get("parentId")));
            }
            // 关联ID
            if(searchMap.get("typeId")!=null && !"".equals(searchMap.get("typeId"))){
                criteria.andEqualTo("typeId",String.valueOf(searchMap.get("typeId")));
            }
            // 图片类型（1：商品sku_id；2：品牌ID；3：相册封面；100001：未关联图片）
            if(searchMap.get("type")!=null && !"".equals(searchMap.get("type"))){
                criteria.andEqualTo("type",String.valueOf(searchMap.get("type")));
            }
        }

        return example;
    }

}
