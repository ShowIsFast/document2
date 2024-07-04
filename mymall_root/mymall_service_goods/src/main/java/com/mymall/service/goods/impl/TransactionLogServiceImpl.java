package com.mymall.service.goods.impl;

import com.mymall.service.goods.dao.TransactionLogMapper;
import com.mymall.pojo.goods.TransactionLog;
import com.mymall.contract.order.TransactionLogService;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

@DubboService
public class TransactionLogServiceImpl implements TransactionLogService {

    @Autowired
    private TransactionLogMapper transactionLogMapper;

    @Override
    public void addTransactionLog(TransactionLog transactionLog) {
        transactionLogMapper.insertSelective(transactionLog);
    }

    @Override
    public TransactionLog getById(String id) {
        return transactionLogMapper.selectByPrimaryKey(id);
    }

    @Override
    public TransactionLog getByTransId(String transId) {
        Example example=new Example(TransactionLog.class);

        if (!(transId!=null)&&!("".equals(transId))){
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("transId",transId);
        }

        return transactionLogMapper.selectOneByExample(example);
    }

}
