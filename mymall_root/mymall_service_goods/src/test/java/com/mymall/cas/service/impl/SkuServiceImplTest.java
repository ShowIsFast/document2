package com.mymall.cas.service.impl;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.mymall.GoodsProviderApplication;
import com.mymall.service.goods.es.api.SkuRepository;
import com.mymall.service.goods.es.bean.SkuInf;
import com.mymall.pojo.goods.Sku;
import com.mymall.contract.goods.SkuService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GoodsProviderApplication.class)
public class SkuServiceImplTest extends TestCase {
    @Autowired
    private SkuService skuService;
    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Test
    public void createIndexStu(){
        Stu stu = new Stu();
        stu.setStuId(32L);
        stu.setAge(23);
        stu.setName("sdf");
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        List indexQueryList = new ArrayList<IndexQuery>();
        indexQueryList.add(indexQuery);
        esTemplate.bulkIndex(indexQueryList);
    }
    @Test
    public void createIndexSku(){
        SkuInf skuInf = new SkuInf();
        skuInf.setSkuId("222334");
        skuInf.setName("华为手机");
        skuInf.setPrice(999);
        skuInf.setImage("sdfds");
        skuInf.setCreateTime(new Date());
        skuInf.setCategoryName("手机");
        skuInf.setBrandName("华为");
        Map map = new HashMap<>();
        map.put("sdf","sdf");
        skuInf.setSpec(map);
        skuInf.setSaleNum(0);
        skuInf.setCommentNum(0);


     IndexQuery indexQuery = new IndexQueryBuilder().withObject(skuInf).build();
        List indexQueryList = new ArrayList<IndexQuery>();
        indexQueryList.add(indexQuery);
        esTemplate.index(indexQuery);
    }
    @Test
    public void testFindAll() throws IOException {
        List<Sku> skuList = skuService.findAll();
        List skuInfs = new ArrayList<SkuInf>();
        for (Sku sku : skuList) {
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
        }
        skuRepository.saveAll(skuInfs);
    }


}