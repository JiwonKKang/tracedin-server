package com.univ.tracedin.infra.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.univ.tracedin")
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.client.host}")
    private String esHost;

    @Value("${elasticsearch.port}")
    private int esPort;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder().connectedTo(esHost + ':' + esPort).build();
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        final RestClient restClient = RestClient.builder(new HttpHost(esHost, esPort)).build();
        final RestClientTransport transport =
                new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
