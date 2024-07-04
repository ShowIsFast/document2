package com.mymall.contract.order;

import com.mymall.pojo.goods.TransactionLog;

public interface TransactionLogService {

    void addTransactionLog(TransactionLog transactionLog);

    TransactionLog getById(String id);

    TransactionLog getByTransId(String transId);

}
