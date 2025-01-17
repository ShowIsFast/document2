package com.mymall.contract.order;

import com.mymall.pojo.order.Preferential;
import com.mymall.pojo.entity.PageResult;

import java.util.*;

/**
 * preferential业务逻辑层
 */
public interface PreferentialService {

    public List<Preferential> findAll();

    public PageResult<Preferential> findPage(int page, int size);

    public List<Preferential> findList(Map<String, Object> searchMap);

    public PageResult<Preferential> findPage(Map<String, Object> searchMap, int page, int size);

    public Preferential findById(Integer id);

    public void add(Preferential preferential);

    public void update(Preferential preferential);

    public void delete(Integer id);

    /**
     * 根据分类和消费额查询优惠金额
     * @param categoryId
     * @param money
     * @return
     */
    public int findPreMoneyByCategoryId(Integer categoryId,int money);

    public Map<String,Object> selectNewByOne();

}
