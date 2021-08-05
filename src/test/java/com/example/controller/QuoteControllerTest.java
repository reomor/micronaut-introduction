package com.example.controller;

import com.example.domain.Quote;
import com.example.domain.Symbol;
import com.example.error.RestError;
import com.example.repository.Store;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class QuoteControllerTest {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxHttpClient httpClient;

    @Inject
    Store store;

    @Test
    void testQuote() {

        String bimSymbol = "BIM";

        Quote quoteExpected = initRandomQuote(bimSymbol);

        store.updateQuote(quoteExpected);

        Quote quoteActual = httpClient.toBlocking()
            .retrieve(
                HttpRequest.GET("/quote/" + bimSymbol),
                Quote.class
            );

        assertThat(quoteActual)
            .usingRecursiveComparison()
            .isEqualTo(quoteExpected);
    }

    @Test
    void testUnsupportedQuote() {

        String uri = "/quote/UNSUPPORTED";

        try {
            httpClient.toBlocking()
                .retrieve(
                    HttpRequest.GET(uri),
                    Argument.of(Quote.class),
                    Argument.of(RestError.class)
                );
        } catch (HttpClientResponseException e) {
            HttpResponse<?> response = e.getResponse();
            assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
            Optional<RestError> errorOptional = response.getBody(RestError.class);
            assertTrue(errorOptional.isPresent());
            RestError error = errorOptional.get();
            assertEquals(HttpStatus.NOT_FOUND.getCode(), error.getStatus());
            assertEquals(HttpStatus.NOT_FOUND.name(), error.getError());
            assertEquals("Quote is not found", error.getMessage());
            assertEquals(uri, error.getPath());
        }
    }

    private Quote initRandomQuote(String bimSymbol) {
        return Quote.builder()
            .symbol(new Symbol(bimSymbol))
            .bid(randomBigDecimal())
            .ask(randomBigDecimal())
            .lastPrice(randomBigDecimal())
            .volume(randomBigDecimal())
            .build();
    }

    private BigDecimal randomBigDecimal() {
        return BigDecimal.valueOf(random.nextDouble(1, 100));
    }
}
