import com.alibaba.fastjson.JSON;
import com.mymall.UserProviderApplication;
import junit.framework.TestCase;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserProviderApplication.class)
public class RocketMQTest extends TestCase {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void testRocket() throws UnsupportedEncodingException {
        Map result = new HashMap<>();
        result.put("sd","sdf");
        rocketMQTemplate.syncSend("quene-sms",(JSON.toJSONString(result)).getBytes(RemotingHelper.DEFAULT_CHARSET));
    }

    @Test
    public void testRocketGoods() throws UnsupportedEncodingException {
        Map<String,String> map = new HashMap();
        map.put("sku_id","100000056837");
        map.put("spu_id","10000005684700");
        System.out.println("发送商品对应的消息：{},"+JSON.toJSONString(map));
        rocketMQTemplate.syncSend("goods-es-sms",JSON.toJSONString(map));
    }
}
