package com.mymall.service.goods.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mymall.service.goods.dao.SkuMapper;
import com.mymall.service.goods.es.api.SkuRepository;
import com.mymall.service.goods.es.bean.SkuInf;
import com.mymall.pojo.goods.Goods;
import com.mymall.pojo.goods.Sku;
import com.mymall.pojo.goods.Spu;
import com.mymall.contract.goods.CategoryService;
import com.mymall.contract.goods.SpuService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
@RocketMQMessageListener(topic = "goods-es-sms", consumerGroup = "goods_consumer")
public class SkuInsertEsConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    private static Logger log = LoggerFactory.getLogger(SkuInsertEsConsumer.class);

    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private SkuMapper skuMapper;

    @DubboReference
    private CategoryService categoryService;

    @Autowired
    private TemplateEngine templateEngine;

    @DubboReference
    private SpuService spuService;

    @Value("${static.html.path}")
    private String pagePath;

    @Override
    public void onMessage(String message) {
        Map<String,String> sms = JSON.parseObject(message, Map.class);

        log.info("[mymall_service_goods][SkuInsertEsConsumer]"+sms);

        String sku_id = sms.get("sku_id");
        String spu_id = sms.get("spu_id");

        createPage(spu_id); //为商品详情生成静态页面
        insertEsGoods(sku_id);  //将SKU数据写入到 es
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
    }

    public void insertEsGoods(String sku_id){
        log.info("[mymall_service_goods][SkuInsertEsConsumer][insertEsGoods][插入es开始！]");
        Sku sku = skuMapper.selectByPrimaryKey(sku_id);
        log.info("sku {} ",sku);
        List skuInfs = new ArrayList<SkuInf>();
        SkuInf skuInf = new SkuInf();
        skuInf.setSkuId(sku.getId());
        skuInf.setName(sku.getName());
        skuInf.setBrandName(sku.getBrandName());
        skuInf.setCategoryName(sku.getCategoryName());
        skuInf.setPrice(sku.getPrice());
        skuInf.setCreateTime(sku.getCreateTime());
        skuInf.setSaleNum(sku.getSaleNum());
        skuInf.setCommentNum(sku.getCommentNum());
        String spec = sku.getSpec();
        Map map = JSONObject.parseObject(spec, Map.class);
        skuInf.setSpec(map);
        skuInf.setImage(sku.getImage());
        skuInf.setSpuId(sku.getSpuId());
        skuInfs.add(skuInf);
        skuRepository.saveAll(skuInfs);

        log.info("[mymall_service_goods][SkuInsertEsConsumer][insertEsGoods][插入es成功！]");
    }

    /**
     * 根据SPU id生成商品详细页
     * @param id
     */
    public void createPage(String id){
        log.info("[mymall_service_goods][SkuInsertEsConsumer][insertEsGoods][生成商品静态页开始！]");

        //查询商品信息
        Goods goods = spuService.findGoodsById(id);
        log.info("[mymall_service_goods][SkuInsertEsConsumer][insertEsGoods][goods:]");
        log.info(goods.toString());

        //获取SPU 信息
        Spu spu = goods.getSpu();

        //获取sku列表
        List<Sku> skuList = goods.getSkuList();

        //查询商品分类
        List<String> categoryList=new ArrayList<String>();
        categoryList.add(categoryService.findById(spu.getCategory1Id()).getName());//一级分类
        categoryList.add(categoryService.findById(spu.getCategory2Id()).getName());//二级分类
        categoryList.add(categoryService.findById(spu.getCategory3Id()).getName());//三级分类

        //生成SKU地址列表
        Map urlMap=new HashMap();
        for(Sku sku:skuList){
            //对规格json字符串进行排序
            String specJson=  JSON.toJSONString(  JSON.parseObject(sku.getSpec()), SerializerFeature.SortField.MapSortField );
            urlMap.put(specJson,sku.getId()+".html");
        }

        //创建页面（每个SKU为一个页面）
        for(Sku sku:skuList){
            log.info("sku {} :",sku);
            log.info("spu {} :",spu);
            // 1.上下文
            Context context = new Context();
            //创建数据模型
            Map<String, Object> dataModel =new HashMap();
            dataModel.put("spu",spu);
            dataModel.put("sku",sku);

            dataModel.put("categoryList",categoryList);//商品分类面包屑

            if(null != sku.getImages() && !"".equals(sku.getImages())){
                dataModel.put("skuImages",  sku.getImages().split(","));//SKU图片列表
            }
            if(null != spu.getImages() && !"".equals(spu.getImages())){
                dataModel.put("spuImages",  spu.getImages().split(","));//SPU图片列表
            }

            Map paraItems = JSON.parseObject(spu.getParaItems());//SPU参数列表
            dataModel.put("paraItems", paraItems);
            Map specItems = JSON.parseObject(sku.getSpec());//当前SKU规格
            dataModel.put("specItems", specItems);

            //规格选择面板
            Map<String,List> specMap  = (Map) JSON.parse(spu.getSpecItems());
            log.info("spu.getSpecItems() {} ",spu.getSpecItems());
            log.info("specMap {} ",specMap);
            for(String key:specMap.keySet()  ){//循环规格名称
                log.info("specMap.get(key) {} ",specMap.get(key));
                List<String> list = specMap.get(key);
                List<Map> mapList=new ArrayList<Map>();
                for(String value:list ){//循环规格选项值
                    Map map=new HashMap();
                    map.put("option",value);
                    map.put("checked",false);
                    if(specItems.get(key)!=null&&specItems.get(key).equals(value)){  //判断此规格组合是否是当前SKU的，标记选中状态
                        map.put("checked",true);
                    }
                    //商品详细页地址
                    Map spec= JSON.parseObject(sku.getSpec());//当前SKU规格
                    spec.put(key,value);
                    String specJson=  JSON.toJSONString( spec, SerializerFeature.SortField.MapSortField );
                    map.put("url",urlMap.get(specJson));

                    mapList.add(map);
                }
                specMap.put(key,mapList);//用新集合覆盖原集合
            }
            dataModel.put("specMap", specMap);//规格面板
            context.setVariables(dataModel);

            // 2.准备文件
            File dir = new File(pagePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(dir, sku.getId() + ".html");

            // 3.生成页面
            try {
                PrintWriter writer = new PrintWriter(dest, "UTF-8");
                templateEngine.process("statichtml/item", context, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        log.info("[mymall_service_goods][SkuInsertEsConsumer][insertEsGoods][生成商品静态页结束！]");
    }

}
