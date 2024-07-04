package com.mymall.service.goods.es.api;

import com.mymall.service.goods.es.bean.SkuInf;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SkuRepository extends ElasticsearchRepository<SkuInf,String> {

}
