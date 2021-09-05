package com.github.reomor.controller;

import com.github.reomor.Api;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.asyncsearch.AsyncSearchResponse;
import org.elasticsearch.client.asyncsearch.SubmitAsyncSearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.reomor.Api.MN_ES_IDX;

@Controller("/elastic")
public class ElasticController {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RestHighLevelClient client;

    public ElasticController(RestHighLevelClient client) {
        this.client = client;
    }

    @ExecuteOn(TaskExecutors.IO)
    @Get("/document/sync/{id}")
    public String getById(@PathVariable("id") String documentId) throws IOException {
        GetResponse response = client.get(new GetRequest(MN_ES_IDX, documentId), RequestOptions.DEFAULT);
        String responseSourceAsString = response.getSourceAsString();
        log.debug("Response: documentId={} => {}", documentId, responseSourceAsString);
        return responseSourceAsString;
    }

    @Get("/document/async/{id}")
    public CompletableFuture<String> getByIdAsync(@PathVariable("id") String documentId) {

        var whenDone = new CompletableFuture<String>();
        client.getAsync(
          new GetRequest(MN_ES_IDX, documentId),
          RequestOptions.DEFAULT,
          new ActionListener<>() {

              @Override
              public void onResponse(GetResponse response) {
                  String responseSourceAsString = response.getSourceAsString();
                  log.debug("Response: documentId={} => {}", documentId, responseSourceAsString);
                  whenDone.complete(responseSourceAsString);
              }

              @Override
              public void onFailure(Exception e) {
                  whenDone.completeExceptionally(e);
              }
          }
        );

        return whenDone;
    }

    @Get("/document/async/firstname/{search}")
    public CompletableFuture<String> getByFirstName(@PathVariable("search") String search) {

        var whenDone = new CompletableFuture<String>();

        SearchSourceBuilder source = new SearchSourceBuilder()
          .query(QueryBuilders.matchQuery("first_name", search));
        SubmitAsyncSearchRequest request = new SubmitAsyncSearchRequest(source, MN_ES_IDX);
        client.asyncSearch()
          .submitAsync(
            request,
            RequestOptions.DEFAULT,
            new ActionListener<>() {

                @Override
                public void onResponse(AsyncSearchResponse response) {

                    List<String> responseString = Stream.of(response.getSearchResponse().getHits().getHits())
                      .map(SearchHit::getSourceAsString)
                      .collect(Collectors.toList());

                    log.debug("Got hits: {}", responseString);

                    whenDone.complete(responseString.toString());
                }

                @Override
                public void onFailure(Exception e) {
                    whenDone.completeExceptionally(e);
                }
            }
          );

        return whenDone;
    }
}
