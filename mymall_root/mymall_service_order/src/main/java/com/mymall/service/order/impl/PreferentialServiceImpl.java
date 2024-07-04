package com.mymall.service.order.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.contract.order.PreferentialService;
import com.mymall.pojo.order.Preferential;
import com.mymall.service.order.dao.PreferentialMapper;
import com.mymall.pojo.entity.PageResult;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@DubboService
public class PreferentialServiceImpl implements PreferentialService {

    @Autowired
    private PreferentialMapper preferentialMapper;

    @Override
    public Map<String,Object> selectNewByOne() {
        return preferentialMapper.selectNewByOne();
    }

    /**
     * 返回全部记录
     * @return
     */
    public List<Preferential> findAll() {
        return preferentialMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Preferential> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Preferential> preferentials = (Page<Preferential>) preferentialMapper.selectAll();

        return new PageResult<Preferential>(preferentials.getTotal(),preferentials.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Preferential> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);

        return preferentialMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Preferential> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Preferential> preferentials = (Page<Preferential>) preferentialMapper.selectByExample(example);

        return new PageResult<Preferential>(preferentials.getTotal(),preferentials.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Preferential findById(Integer id) {
        return preferentialMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param preferential
     */
    public void add(Preferential preferential) {
        preferentialMapper.insert(preferential);
    }

    /**
     * 修改
     * @param preferential
     */
    public void update(Preferential preferential) {
        preferentialMapper.updateByPrimaryKeySelective(preferential);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        preferentialMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int findPreMoneyByCategoryId(Integer categoryId, int money) {
        Example example=new Example(Preferential.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("state","1");//状态
        criteria.andEqualTo("categoryId",categoryId);
        criteria.andLessThanOrEqualTo("buyMoney",money);//小于等于购买金额
        criteria.andGreaterThanOrEqualTo("endTime",new Date());//截至日期大于等于当前日期
        criteria.andLessThanOrEqualTo("startTime",new Date());//开始日期小于等于当前日期
        example.setOrderByClause("buy_money desc");//按照购买金额降序排序
        List<Preferential> preferentials = preferentialMapper.selectByExample(example);

        if(preferentials.size()>=1){
            Preferential preferential = preferentials.get(0);
            if("1".equals(preferential.getType())){//不翻倍
                return preferential.getPreMoney();//返回优惠的金额
            }else{ //翻倍
                int multiple=  money/preferentials.get(0).getBuyMoney();
                return preferential.getPreMoney()*multiple;
            }
        }else{
            return 0;
        }
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Preferential.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 消费金额
            if(searchMap.get("buyMoney")!=null ){
                criteria.andEqualTo("buyMoney",searchMap.get("buyMoney"));
            }
            // 优惠金额
            if(searchMap.get("preMoney")!=null ){
                criteria.andEqualTo("preMoney",searchMap.get("preMoney"));
            }
            // 优惠金额
            if(searchMap.get("state")!=null && !"".equals(searchMap.get("state"))){
                criteria.andEqualTo("state",searchMap.get("state"));
            }        // 优惠金额
            if(searchMap.get("type")!=null  && !"".equals(searchMap.get("type"))){
                criteria.andEqualTo("type",searchMap.get("type"));
            }
        }

        return example;
    }

}
