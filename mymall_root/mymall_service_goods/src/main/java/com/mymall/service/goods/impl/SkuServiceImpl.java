package com.mymall.service.goods.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.service.goods.dao.TransactionLogMapper;
import com.mymall.pojo.goods.Sku;
import com.mymall.pojo.goods.TransactionLog;
import com.mymall.contract.goods.SkuService;
import com.mymall.service.goods.dao.SkuMapper;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.order.OrderItem;
import com.mymall.common.service.util.CacheKey;
import com.mymall.common.service.util.IdWorker;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import tk.mybatis.mapper.entity.Example;

import java.util.*;

@DubboService(interfaceClass = SkuService.class)
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SkuService skuService;
    @Autowired
    private TransactionLogMapper transactionLogMapper;

    @Autowired
    private IdWorker idWorker;

    /**
     * 返回全部记录
     * @return
     */
    public List<Sku> findAll() {
        return skuMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Sku> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Sku> skus = (Page<Sku>) skuMapper.selectAll();
        return new PageResult<Sku>(skus.getTotal(),skus.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Sku> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return skuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Sku> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Sku> skus = (Page<Sku>) skuMapper.selectByExample(example);
        return new PageResult<Sku>(skus.getTotal(),skus.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Sku findById(String id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param sku
     */
    public void add(Sku sku) {
        skuMapper.insert(sku);
    }

    /**
     * 修改
     * @param sku
     */
    public void update(Sku sku) {
        skuMapper.updateByPrimaryKeySelective(sku);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(String id) {
        skuMapper.deleteByPrimaryKey(id);
       // 删除缓存中的价格
        Map map=new HashMap();
        map.put("spuId",id);
        List<Sku> skuList = skuService.findList(map);
        for(Sku sku:skuList){
            skuService.deletePriceFromRedis(sku.getId());
        }
    }

    @Override
    public Map<String,Object> selectNewByOne() {
        return skuService.selectNewByOne();
    }

    @Autowired
    private RedisTemplate redisTemplate;

    public int findPrice(String id) {
        //从缓存中查询
        return  (Integer)redisTemplate.boundHashOps(CacheKey.SKU_PRICE).get(id);
    }


    public void savePriceToRedisBySkuId(String skuId,Integer price) {
        redisTemplate.boundHashOps(CacheKey.SKU_PRICE).put(skuId,price);
    }

    public void saveAllPriceToRedis() {
        //检查缓存是否存在价格数据
        if(!redisTemplate.hasKey("sku_price")){
            System.out.println("加载全部数据");
            List<Sku> skuList = skuMapper.selectAll();
            for(Sku sku:skuList){
                if("1".equals(sku.getStatus())){
                    redisTemplate.boundHashOps("sku_price").put(sku.getId(),sku.getPrice());
                }
            }
        }else{
            System.out.println("已存在价格数据，没有全部加载");
        }
    }


    @Override
    @Transactional
    public boolean deductionStock(List<OrderItem> orderItemList,String transId) {
        //判断是否可以扣减库存
        boolean isDeduction=true;
        List<Sku> deductionList=new ArrayList<>();  //扣减列表
        for(OrderItem orderItem :orderItemList){
           //根据skuId 查询库存数量
           Sku sku = findById(orderItem.getSkuId());
           if(sku==null){ //如果不存在
               isDeduction=false;
           }
           if(!"1".equals(sku.getStatus())){ //如果下架
               isDeduction=false;
           }
           if(sku.getNum().intValue()<orderItem.getNum()){//如果库存数量不足
               isDeduction=false;
           }
           //加入扣减列表
           Sku deduction=new Sku();
           deduction.setId(sku.getId());
           deduction.setNum(orderItem.getNum());//扣减数量
           deductionList.add(deduction);
        }
        //执行扣减
        if(isDeduction){  //如果所有的商品都库存充足
            for(Sku sku:deductionList){  //循环扣减列表
                skuMapper.updateNum(sku.getId(),-sku.getNum());
                TransactionLog transactionLog = new TransactionLog();
                transactionLog.setId(idWorker.nextId()+"");
                transactionLog.setTransId(transId);
                transactionLog.setBusiness("order");
                transactionLog.setCreated(new Date());
                transactionLog.setUpdated(new Date());
                transactionLog.setForeignKey(sku.getId());
                transactionLogMapper.insertSelective(transactionLog);
            }
        }

        return isDeduction;
    }

    public void deletePriceFromRedis(String id) {
        redisTemplate.boundHashOps(CacheKey.SKU_PRICE).delete(id);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 商品条码
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andLike("sn","%"+searchMap.get("sn")+"%");
            }
            // SKU名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 商品图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 商品图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
            }
            // 类目名称
            if(searchMap.get("categoryName")!=null && !"".equals(searchMap.get("categoryName"))){
                criteria.andLike("categoryName","%"+searchMap.get("categoryName")+"%");
            }
            // 品牌名称
            if(searchMap.get("brandName")!=null && !"".equals(searchMap.get("brandName"))){
                criteria.andLike("brandName","%"+searchMap.get("brandName")+"%");
            }
            // 规格
            if(searchMap.get("spec")!=null && !"".equals(searchMap.get("spec"))){
                criteria.andLike("spec","%"+searchMap.get("spec")+"%");
            }
            // 商品状态 1-正常，2-下架，3-删除
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }

            // 价格（分）
            if(searchMap.get("price")!=null ){
                criteria.andEqualTo("price",searchMap.get("price"));
            }
            // 库存数量
            if(searchMap.get("num")!=null ){
                criteria.andEqualTo("num",searchMap.get("num"));
            }
            // 库存预警数量
            if(searchMap.get("alertNum")!=null ){
                criteria.andEqualTo("alertNum",searchMap.get("alertNum"));
            }
            // 重量（克）
            if(searchMap.get("weight")!=null ){
                criteria.andEqualTo("weight",searchMap.get("weight"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }
        }

        return example;
    }

}
