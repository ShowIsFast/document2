package com.mymall.contract.goods;

import com.mymall.pojo.goods.Sku;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.order.OrderItem;

import java.util.*;

/**
 * 业务逻辑层
 */
public interface SkuService {

    public List<Sku> findAll();

    public PageResult<Sku> findPage(int page, int size);

    public List<Sku> findList(Map<String,Object> searchMap);

    public PageResult<Sku> findPage(Map<String,Object> searchMap,int page, int size);

    public Sku findById(String id);

    public void add(Sku sku);

    public void update(Sku sku);

    public void delete(String id);

    /**
     * 查询价格
     * @param id
     * @return
     */
    public int findPrice(String id);

    /**
     *  保存价格到缓存
     * @param skuId
     */
    public void savePriceToRedisBySkuId(String skuId,Integer price);

    /**
     *  保存全部价格到缓存
     */
    public void saveAllPriceToRedis();

    /**
     *  根据购物车批量扣减库存
     * @param oderItemList
     */
    public boolean deductionStock(List<OrderItem> oderItemList,String transId);

    /**
     * 根据sku id 删除商品价格缓存
     * @param id
     */
    public void deletePriceFromRedis(String id);

    public Map<String,Object> selectNewByOne();

}
