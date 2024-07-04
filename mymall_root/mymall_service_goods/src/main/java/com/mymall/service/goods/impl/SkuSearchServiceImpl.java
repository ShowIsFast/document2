package com.mymall.service.goods.impl;

import com.alibaba.fastjson.JSON;
import com.mymall.pojo.entity.PageResult;
import com.mymall.service.goods.dao.BrandMapper;
import com.mymall.service.goods.dao.SearchResultMapperImpl;
import com.mymall.service.goods.dao.SkuMapper;
import com.mymall.service.goods.dao.SpecMapper;
import com.mymall.service.goods.es.api.SkuRepository;
import com.mymall.service.goods.es.bean.SkuInf;
import com.mymall.pojo.goods.Sku;
import com.mymall.contract.goods.BrandService;
import com.mymall.contract.goods.SkuSearchService;
import com.mymall.contract.goods.SkuService;

import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DubboService
public class SkuSearchServiceImpl implements SkuSearchService {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private SkuRepository skuRepository;

    private String indexName="sku";

    private String typeName="doc";

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SkuService skuService;

    /**
     * 批量导入SKU
     */
    public void importSkuList() {
//        System.out.println("导入数据开始");
//        Example example=new Example(Sku.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("status","1");
//        List<Sku> skuList = skuMapper.selectByExample(example);
//        importSkuList(skuList);
    }

    @Override
    public void importSkuList(List<Sku> skuList) {
        Client client = esTemplate.getClient();
        //2.构建BulkRequest
        BulkRequest bulkRequest = new BulkRequest();
        for (Sku sku : skuList) {
            if("1".equals(sku.getStatus())){
                IndexRequest indexRequest = new IndexRequest(indexName, typeName, sku.getId().toString());
                Map skuMap=new HashMap();
                skuMap.put("id",sku.getId());
                skuMap.put("spuId",sku.getSpuId());
                skuMap.put("name",sku.getName());
                skuMap.put("brandName",sku.getBrandName());
                skuMap.put("categoryName",sku.getCategoryName());
                skuMap.put("image",sku.getImage());
                skuMap.put("price",sku.getPrice());
                skuMap.put("createTime",sku.getCreateTime());
                skuMap.put("saleNum",sku.getSaleNum());
                skuMap.put("commentNum",sku.getCommentNum());
                skuMap.put("spec",JSON.parseObject(sku.getSpec(),Map.class) );
                indexRequest.source(skuMap);
                bulkRequest.add(indexRequest);
            }
        }
        //ActionFuture<BulkResponse> future= client.bulk(bulkRequest);  //同步调用方式

        //异步调用方式
        client.bulk(bulkRequest,new ActionListener<BulkResponse>() {
        //restHighLevelClient.bulkAsync(bulkRequest,new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkResponse) {
                //成功
                System.out.println("导入成功"+bulkResponse.status());
            }
            @Override
            public void onFailure(Exception e) {
                //失败
                System.out.println("导入失败"+e.getMessage());
            }
        });
        System.out.println("调用完成");
    }

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询
     * @param searchMap
     * @return
     */
    public Map search(Map<String, String> searchMap){
        Map resultMap = new HashMap();

        //1.列表查询
        resultMap.putAll(searchSkuList_esTemplate(searchMap));

        //2.查询商品分类列表
        List<String> categoryList = searchCategoryList_esTemplate(searchMap);
        resultMap.put("categoryList",categoryList);

        //3.根据商品分类查询品牌
        String categoryName="";                 //商品分类名称
        if(searchMap.get("category") == null){  //如果没有分类条件
            if(categoryList.size()>0){
                categoryName=categoryList.get(0);
            }
        }else{  //如果有商品分类条件
            categoryName=searchMap.get("category");
        }

        //todo redis
        resultMap.put("brandList",brandMapper.findListByCategoryName(categoryName));

        //todo redis
        //4.根据商品分类查询规格
        resultMap.put("specList",getSpecList(categoryName));

        return resultMap;
    }

    /**
     * 查询列表
     *
     * @param searchMap
     * @return
     */
    private Map searchSkuList(Map<String, String> searchMap) {
        Client client = esTemplate.getClient();
        Map resultMap = new HashMap();//返回结果
        //创建搜索请求对象 参数索引名称
        SearchRequest searchRequest = new SearchRequest(indexName);
        //设置索引类型 doc
        searchRequest.types(typeName);
        // 查询构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //String[] sourceFieldArray = fields.split(",");
        //根据需要果过滤不需要查询的字段,提高查询性能呢
        //searchSourceBuilder.fetchSource(sourceFieldArray, new String[]{});
        //根据关键字进行搜索
        BoolQueryBuilder boolQueryBuilder = buildBasicQuery(searchMap);
        //查询列表
        searchSourceBuilder.query(boolQueryBuilder);

        //分页
        Integer pageNo = Integer.parseInt(searchMap.get("pageNo"));//页码
        if(pageNo<=0){
            pageNo = 1;
        }
        Integer pageSize = 30;//页大小
        //起始记录下标
        int fromIndex = (pageNo - 1) * pageSize;
        searchSourceBuilder.from(fromIndex);//开始索引
        searchSourceBuilder.size(pageSize);//页大小

        //排序
        String sortRule = searchMap.get("sortRule");// 排序规则 ASC  DESC
        String sortField = searchMap.get("sortField");//排序字段  price
        if (sortField != null && !"".equals(sortField)) {//有排序
            searchSourceBuilder.sort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
        }

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font style='color:red'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        try {
//            searchRequest.indicesOptions(IndicesOptions.fromOptions(true,true,true,false));
            //搜索查询
            //SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            ActionFuture<SearchResponse> search = client.search(searchRequest);
            SearchResponse searchResponse = search.get();

            //获取查询结果
            SearchHits searchHits = searchResponse.getHits();
            //获取内容数据
            List<Map<String, Object>> skuContentList = getSkuContent(searchHits);
            resultMap.put("rows", skuContentList);

            //计算页码数量
            long totalCount = searchHits.getTotalHits();//总记录数
            long pageCount = (totalCount % pageSize == 0) ? totalCount / pageSize : (totalCount / pageSize + 1);
            resultMap.put("totalPages", pageCount);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }


    /**
     * 构建基本查询
     *
     * @param searchMap
     * @return
     */
    private BoolQueryBuilder buildBasicQuery(Map<String, String> searchMap) {
        // 构建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //1.关键字查询
        queryBuilder.must(QueryBuilders.matchQuery("name", searchMap.get("keywords")));

        //2.商品分类筛选
        if(searchMap.get("category")!=null){
            queryBuilder.filter(QueryBuilders.matchQuery("categoryName", searchMap.get("category")));
        }

        //3.品牌筛选
        if (searchMap.get("brand") != null) {
            queryBuilder.filter(QueryBuilders.matchQuery("brandName", searchMap.get("brand")));
        }

        //4.规格筛选
        for (String key : searchMap.keySet()) {
            String value =  searchMap.get(key);
            if (key.startsWith("spec.")) {//如果是规格参数

                //对版本字段做特殊处理
                if("版本".equals(key.substring(5))){
                    value = value.replace(" ","+");
                }
                queryBuilder.filter(QueryBuilders.matchQuery(key + ".keyword" ,value));

            }
        }

        //5.价格筛选
        if(searchMap.get("price")!=null){
            String[] price = ((String)searchMap.get("price")).split("-");
            if(!price[0].equals("0")){//最低价格不等于0
                queryBuilder.filter(QueryBuilders.rangeQuery("price").gt(price[0]+"00"));
            }
            if(!price[1].equals("*")){//如果价格有上限
                queryBuilder.filter(QueryBuilders.rangeQuery("price").lt(price[1]+"00"));
            }
        }

        return queryBuilder;
    }


    //获取内容数据
    private List<Map<String, Object>> getSkuContent(SearchHits searchHits) {
        SearchHit[] searchHit = searchHits.getHits();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : searchHits) {
            //获取原文档数据
            Map<String, Object> skuMap = hit.getSourceAsMap();
            //提取高亮内容
            String name ="";
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields!=null) {
                HighlightField highlightFieldName = highlightFields.get("name");
                if (highlightFieldName != null) {
                    Text[] fragments = highlightFieldName.fragments();
                    name = fragments[0].toString();
                }
            }
            skuMap.put("name",name); //设置高亮
            resultList.add(skuMap);
        }

        return resultList;
    }

    /**
     * 查询分类列表
     * @param searchMap
     * @return
     */
    private List<String> searchCategoryList(Map searchMap) {
        Client client = esTemplate.getClient();
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(indexName);
        //设置搜索类型
        searchRequest.types(typeName);
        //查询列表
        BoolQueryBuilder boolQueryBuilder = buildBasicQuery(searchMap);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String groupName = "sku_category";
        //聚合分类查询
        TermsAggregationBuilder aggregation = AggregationBuilders.terms(groupName).field("categoryName");
        searchSourceBuilder.aggregation(aggregation);
     //   searchSourceBuilder.size(0);
        //执行查询
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        List<String> categoryList = null;
        try {
            //SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            ActionFuture<SearchResponse> search = client.search(searchRequest);
            SearchResponse searchResponse = search.get();

            Aggregations aggregations = searchResponse.getAggregations();
            Map<String, Aggregation> asMap = aggregations.getAsMap();
            Terms terms = (Terms) asMap.get(groupName);
            categoryList = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryList;
    }

    @Autowired
    private SpecMapper specMapper;

    /**
     * 返回规格列表
     * @param categoryName
     * @return
     */
    private List<Map> getSpecList(String categoryName){
        List<Map> specList = specMapper.findListByCategoryName(categoryName);
        for(Map spec:specList){
            String[] options = spec.get("options").toString().split(",");
            spec.put("options", options);
        }

        return specList;
    }

    /**
     * 查询Sku集合 - 商品列表
     * @param searchMap 查询条件
     * @return
     */
    private Map searchSkuList_esTemplate(Map<String, String>  searchMap) {
        Map resultMap = new HashMap();
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = buildBasicQuery(searchMap);
        // 查询
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        // 排序
        String sortField = (String)searchMap.get("sortField");      // 排序字段
        String sortRule = (String)searchMap.get("sortRule");        // 排序规则 - 顺序(ASC)/倒序(DESC)
        if(sortField!= null && !"".equals(sortField)){
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
        }

        //分页
        Integer pageNo = Integer.parseInt(searchMap.get("pageNo"));//页码
        if(pageNo<=0){
            pageNo = 1;
        }
        Integer pageSize = 30;//页大小
        //起始记录下标
        int fromIndex = (pageNo - 1) * pageSize;
        // 构建分页
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNo,pageSize));

        // 构建高亮查询
        HighlightBuilder.Field field = new HighlightBuilder.Field("name").preTags("<font style='color:red'>").postTags("</font>");
        nativeSearchQueryBuilder.withHighlightFields(field);  // 名字高亮
        NativeSearchQuery build = nativeSearchQueryBuilder.build();

        // 获取查询结果
        AggregatedPage<SkuInf> goodsPage = esTemplate.queryForPage(build, SkuInf.class, new SearchResultMapperImpl());
        long total = goodsPage.getTotalElements();  // 总数据量
        long totalPage = goodsPage.getTotalPages(); // 总页数
        // ...你还要将是否有上页下页等内容传过去
        List<SkuInf> goodsList = goodsPage.getContent();
        goodsList.forEach(System.out::println);
        resultMap.put("rows",goodsList);
        resultMap.put("total",total);
        resultMap.put("totalPages",totalPage);

        return resultMap;
    }

    /**
     * 查询分类列表
     * @param searchMap
     * @return
     */
    private List<String> searchCategoryList_esTemplate(Map searchMap) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        // 构建查询
        BoolQueryBuilder boolQueryBuilder = buildBasicQuery(searchMap);
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        // 分类聚合名
        String groupName = "sku_category";
        // 构建聚合查询
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(groupName).field("categoryName");
        nativeSearchQueryBuilder.addAggregation(termsAggregationBuilder);
        // 获取聚合分页结果
        AggregatedPage<SkuInf> goodsList = (AggregatedPage<SkuInf>) skuRepository.search(nativeSearchQueryBuilder.build());
        StringTerms stringTerms = (StringTerms) goodsList.getAggregation(groupName);
        // 获取桶
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        // 使用流Stream 将分类名存入集合
        List<String> categoryList = buckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

        // 打印分类名称
        categoryList.forEach(System.out::println);

        return categoryList;
    }

}
