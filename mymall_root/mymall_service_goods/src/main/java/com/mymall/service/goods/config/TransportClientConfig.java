package com.mymall.service.goods.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.ElasticsearchEntityMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class TransportClientConfig extends ElasticsearchConfigurationSupport {

  @Value("${es-name}")
  private String es_name;

  @Value("${es-cluster}")
  private String es_cluster;

  @Value("${es-ip}")
  private String es_ip;

  @Value("${es-port}")
  private String es_port;

  @Bean
  public Client elasticsearchClient() throws UnknownHostException {
    Settings settings = Settings.builder().put(es_name, es_cluster).build();
    //Settings settings = Settings.builder().put(es_name, es_cluster).put("client.transport.sniff", true).build();

    TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(es_ip), Integer.valueOf(es_port)));

    return client;
  }

  @Bean(name = {"elasticsearchOperations", "elasticsearchTemplate"})
  public ElasticsearchTemplate elasticsearchTemplate() throws UnknownHostException {
      return new ElasticsearchTemplate(elasticsearchClient(), entityMapper());
  }

  @Bean
  @Override
  public EntityMapper entityMapper() {
    ElasticsearchEntityMapper entityMapper = new ElasticsearchEntityMapper(elasticsearchMappingContext(),
  	  new DefaultConversionService());

    return entityMapper;
  }

}