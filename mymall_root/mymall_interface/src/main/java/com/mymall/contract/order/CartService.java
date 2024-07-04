package com.mymall.contract.order;

import java.util.*;

/**
 * 购物车服务
 */
public interface CartService {

    /**
     * 获取用户自己的购物车信息
     * @param username
     * @return
     */
    public List<Map<String,Object>> findCartList(String username);


    /**
     * 添加商品到购物车
     * @param username
     * @param skuId
     * @param num
     */
    public void addItem(String username, String  skuId, Integer num);


    /**
     * 更新选中状态
     * @param username
     * @param skuId
     * @param checked
     */
    public boolean updateChecked(String username, String skuId,boolean checked);


    /**
     * 计算当前选中的购物车的优惠金额
     * @param
     * @return
     */
    public int preferential(String username);

    /**
     * 获取最新的购物车列表
     * @param username 用户名
     * @return
     */
    public List<Map<String, Object>> findNewOrderItemList(String username);


    /**
     * 删除选中的购物车
     * @param username
     */
    public void deleteCheckedCart(String username);

    /**
     * 删除购物车中多个商品
     * */
    public void deleteSkuIds(String username,String sukId);

}
