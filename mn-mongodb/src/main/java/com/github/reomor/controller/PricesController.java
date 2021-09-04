package com.github.reomor.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.bson.Document;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.lang.invoke.MethodHandles;

@Controller("/prices")
public class PricesController {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String MONGODB = "prices";

    private final MongoClient mongoClient;

    public PricesController(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Get
    public Flux<Document> fetch() {
        return Flux.from(getCollection().find());
    }

    @Post
    public Publisher<InsertOneResult> insert(@Body ObjectNode json) {
        log.info("Insert: {}", json);
        return getCollection().insertOne(Document.parse(json.toString()));
    }

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(MONGODB).getCollection("example");
    }
}
