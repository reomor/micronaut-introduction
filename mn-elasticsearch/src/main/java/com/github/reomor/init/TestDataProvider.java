package com.github.reomor.init;

import com.github.javafaker.Faker;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.UUID;

import static com.github.reomor.Api.MN_ES_IDX;

@Singleton
public class TestDataProvider {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Faker FAKER = new Faker();
    private final RestHighLevelClient client;

    public TestDataProvider(RestHighLevelClient client) {
        this.client = client;
    }

    @Scheduled(fixedDelay = "10s")
    void insertDocument() {

        var document = new HashMap<String, String>();
        document.put("first_name", FAKER.name().firstName());
        document.put("last_name", FAKER.name().lastName());

        IndexRequest indexRequest = new IndexRequest()
          .index(MN_ES_IDX)
          .id(UUID.randomUUID().toString())
          .source(document, XContentType.JSON);

        client.indexAsync(
          indexRequest,
          RequestOptions.DEFAULT,
          new ActionListener<>() {

              @Override
              public void onResponse(IndexResponse indexResponse) {
                  log.debug("Document {} is added with id {}", document, indexResponse.getId());
              }

              @Override
              public void onFailure(Exception e) {
                  log.error("Failed to insert: ", e);
              }
          }
        );
    }
}
