package com.github.reomor.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class HelloControllerTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxHttpClient httpClient;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testIndex() {
        String response = httpClient.toBlocking().retrieve("/hello/index");
        assertEquals("Hello", response);
    }

    @Test
    void testJson() {
        ObjectNode objectNode = httpClient.toBlocking().retrieve("/hello/json", ObjectNode.class);
        assertEquals(
            "{\"myText\":\"Hello world\",\"id\":1234567890,\"timeUTC\":\"1973-03-03T09:46:40Z\"}",
            objectNode.toString()
        );
    }
}
