package com.github.reomor.controller;

import com.github.reomor.domain.Symbol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@MicronautTest
class MarketControllerTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxHttpClient httpClient;

    @Test
    void testMarketAll() {

        List<Symbol> symbols = httpClient.toBlocking()
            .retrieve(
                HttpRequest.GET("/market"),
                Argument.of(List.class, Symbol.class)
            );

        assertFalse(symbols.isEmpty());
        assertThat(symbols.stream().map(Symbol::getName).collect(Collectors.toList()))
            .containsExactlyInAnyOrder("BIM", "hYandex", "Lol");
    }

    @Test
    void testMarketAll_viaMapper() throws JsonProcessingException {

        String responseString = httpClient.toBlocking().retrieve(HttpRequest.GET("/market"), String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Symbol> symbols = objectMapper.readValue(responseString, new TypeReference<List<Symbol>>() {});

        assertFalse(symbols.isEmpty());
        assertThat(symbols.stream().map(Symbol::getName).collect(Collectors.toList()))
            .containsExactlyInAnyOrder("BIM", "hYandex", "Lol");
    }
}
