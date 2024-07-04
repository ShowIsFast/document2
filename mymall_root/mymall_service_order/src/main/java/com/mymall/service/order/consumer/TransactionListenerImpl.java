package com.mymall.service.order.consumer;

import com.mymall.pojo.order.Order;
import com.mymall.pojo.order.OrderItem;
import com.mymall.pojo.goods.TransactionLog;
import com.mymall.contract.goods.SkuService;
import com.mymall.contract.order.TransactionLogService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 发送订单入库的半消息
 */
@Service
@RocketMQTransactionListener(txProducerGroup = "tx_order")
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @DubboReference
    private SkuService skuService;

    @DubboReference
    private TransactionLogService transactionLogService ;
    private AtomicInteger transactionIndex = new AtomicInteger(0);

    private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<String, Integer>();

    /**
     * 半消息发送成功后，扣减库存，唤醒发消息线程清理购物车，然后提交半消息
     * @param msg
     * @param param
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object param) {
        String transId = (String)msg.getHeaders().get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID);
        try{
            Map map = (Map) param;
            Order order = (Order) map.get("order");
            CountDownLatch latch = (CountDownLatch) map.get("latch");

            List<OrderItem> orderItemList = (List<OrderItem>)order.getOrderItemList();
            skuService.deductionStock(orderItemList,transId);   //核心操作：扣减库存
            latch.countDown();      //唤醒另一个阻塞的线程，进行清理购物车操作

            return RocketMQLocalTransactionState.COMMIT;    //对半消息进行提交
        }catch (Exception e){
            logger.error("增加订单发生异常：",e);

            return RocketMQLocalTransactionState.ROLLBACK;  //出现异常时，对半消息进行回滚
        }
    }

    /**
     * 如果订单入库成功了那么需要将半消息投递出去
     * @param msg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        String transId = (String)msg.getHeaders().get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID);
        TransactionLog transactionLog = transactionLogService.getByTransId(transId);
        if(transactionLog != null){
            return RocketMQLocalTransactionState.COMMIT;
        }else{
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }

}
