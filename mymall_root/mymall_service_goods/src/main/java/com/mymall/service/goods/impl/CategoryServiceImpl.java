package com.mymall.service.goods.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.contract.goods.CategoryService;
import com.mymall.pojo.goods.Category;
import com.mymall.service.goods.dao.CategoryMapper;
import com.mymall.pojo.entity.PageResult;
import com.mymall.common.service.util.CacheKey;

import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class CategoryServiceImpl implements CategoryService {

   private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Category> findAll() {
        return categoryMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Category> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Category> categorys = (Page<Category>) categoryMapper.selectAll();

        return new PageResult<Category>(categorys.getTotal(),categorys.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Category> findList(Map<String, Object> searchMap) {
//        List<Category> categories = null;
//
//        long size = redisTemplate.boundListOps("CategoryIndex").size();
//        if (size > 0) {
//            categories = redisTemplate.boundListOps("CategoryIndex").range(0,size);
//        } else {
//            Example example = createExample(searchMap);
//            categories = categoryMapper.selectByExample(example);
//            redisTemplate.boundListOps("CategoryIndex").leftPushAll(categories);
//        }
//
//        return categories;
        Example example = createExample(searchMap);

        return categoryMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Category> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Category> categorys = (Page<Category>) categoryMapper.selectByExample(example);

        return new PageResult<Category>(categorys.getTotal(),categorys.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Category findById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param category
     */
    public void add(Category category) {
        categoryMapper.insert(category);
        saveCategoryTreeToRedis();
    }

    /**
     * 修改
     * @param category
     */
    public void update(Category category) {
        categoryMapper.updateByPrimaryKeySelective(category);
        saveCategoryTreeToRedis();
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        //判断是否存在下级分类
        Example example=new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId",id);
        int count = categoryMapper.selectCountByExample(example);
        if(count>0){
            throw  new RuntimeException("存在下级分类不能删除");
        }
        categoryMapper.deleteByPrimaryKey(id);
        saveCategoryTreeToRedis();
    }

    @Override
    public Map<String,Object> selectNewByOne() {
        return categoryMapper.selectNewByOne();
    }



    @Autowired
    private RedisTemplate redisTemplate;

    public List<Map> findCategoryTree() {
        ///从缓存中查询
        return (List<Map>)redisTemplate.boundValueOps(CacheKey.CATEGORY_TREE).get();
    }


    public void saveCategoryTreeToRedis() {
        logger.info("加载商品分类数据到缓存");
        Example example=new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isShow","1");//显示
        List<Category> categories = categoryMapper.selectByExample(example);
        List<Map> categoryTree =findByParentId(categories,0);

        redisTemplate.boundValueOps(CacheKey.CATEGORY_TREE).set(categoryTree);
    }


    private List<Map> findByParentId(List<Category> categoryList, Integer parentId){
        List<Map> mapList=new ArrayList<Map>();
        for(Category category:categoryList){
            if(category.getParentId().equals(parentId)){
                Map map =new HashMap();
                map.put("name",category.getName());
                map.put("menus",findByParentId(categoryList,category.getId()));
                mapList.add(map);
            }
        }

        return mapList;
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 分类名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 是否显示
            if(searchMap.get("isShow")!=null && !"".equals(searchMap.get("isShow"))){
                criteria.andLike("isShow","%"+searchMap.get("isShow")+"%");
            }
            // 是否导航
            if(searchMap.get("isMenu")!=null && !"".equals(searchMap.get("isMenu"))){
                criteria.andLike("isMenu","%"+searchMap.get("isMenu")+"%");
            }

            // 分类ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 商品数量
            if(searchMap.get("goodsNum")!=null ){
                criteria.andEqualTo("goodsNum",searchMap.get("goodsNum"));
            }
            // 排序
            if(searchMap.get("seq")!=null ){
                criteria.andEqualTo("seq",searchMap.get("seq"));
            }
            // 级别
            if(searchMap.get("level")!=null ){
                criteria.andEqualTo("level",searchMap.get("level"));
            }
            // 上级ID
            if(searchMap.get("parentId")!=null ){
                criteria.andEqualTo("parentId",searchMap.get("parentId"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
        }

        return example;
    }

}
