package com.example.controller;

import com.example.domain.Symbol;
import com.example.domain.WatchList;
import com.example.error.RestError;
import com.example.repository.AccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.controller.WatchListController.ACCOUNT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@MicronautTest
class WatchListControllerTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxHttpClient httpClient;

    @Inject
    AccountStore accountStore;

    @Test
    void unAuthorized() {
        try {
            httpClient.toBlocking().retrieve(HttpRequest.GET("/account/watchlist"), WatchList.class);
            fail("Exception is expected");
        } catch (HttpClientResponseException e) {
            HttpResponse<?> response = e.getResponse();
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        }
    }

    @Test
    void getWatchList_empty() {

        BearerAccessRefreshToken token = getAccessToken();

        log.info("Bearer: {}", token);

        accountStore.deleteWatchList(ACCOUNT_ID);
        MutableHttpRequest<Object> getRequest = HttpRequest.GET("/account/watchlist")
            .accept(MediaType.APPLICATION_JSON)
            .bearerAuth(token.getAccessToken());

        WatchList watchList = httpClient.toBlocking().retrieve(getRequest, WatchList.class);

        assertTrue(watchList.getSymbols().isEmpty());
        assertTrue(accountStore.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void getWatchList_notEmpty() {

        BearerAccessRefreshToken token = getAccessToken();

        List<Symbol> symbols = Stream.of("BOM", "BAM")
            .map(Symbol::new)
            .collect(Collectors.toList());
        accountStore.updateWatchList(ACCOUNT_ID, new WatchList(symbols));

        MutableHttpRequest<Object> getRequest = HttpRequest.GET("/account/watchlist")
            .accept(MediaType.APPLICATION_JSON)
            .bearerAuth(token.getAccessToken());

        WatchList watchList = httpClient.toBlocking().retrieve(getRequest, WatchList.class);

        assertEquals(symbols.size(), watchList.getSymbols().size());
        assertEquals(symbols.size(), accountStore.getWatchList(ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void updateWatchList() {

        BearerAccessRefreshToken token = getAccessToken();

        List<Symbol> symbols = Stream.of("BOM", "BAM")
            .map(Symbol::new)
            .collect(Collectors.toList());

        WatchList expectedWatchList = new WatchList(symbols);
        MutableHttpRequest<WatchList> putRequest = HttpRequest.PUT("/account/watchlist", expectedWatchList)
            .accept(MediaType.APPLICATION_JSON)
            .bearerAuth(token.getAccessToken());

        HttpResponse<WatchList> response = httpClient.toBlocking().exchange(putRequest, WatchList.class);

        Optional<WatchList> actualWatchList = response.getBody();
        assertTrue(actualWatchList.isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expectedWatchList, actualWatchList.get());
    }

    @Test
    void deleteWatchList() {

        BearerAccessRefreshToken token = getAccessToken();

        List<Symbol> symbols = Stream.of("BOM", "BAM")
            .map(Symbol::new)
            .collect(Collectors.toList());
        WatchList expectedWatchList = new WatchList(symbols);
        accountStore.updateWatchList(ACCOUNT_ID, new WatchList(symbols));

        MutableHttpRequest<Object> deleteRequest = HttpRequest.DELETE("/account/watchlist/" + ACCOUNT_ID)
            .accept(MediaType.APPLICATION_JSON)
            .bearerAuth(token.getAccessToken());
        HttpResponse<WatchList> response = httpClient.toBlocking().exchange(deleteRequest, WatchList.class);

        Optional<WatchList> actualWatchList = response.getBody();
        assertTrue(actualWatchList.isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expectedWatchList, actualWatchList.get());
        assertTrue(accountStore.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());
    }

    private BearerAccessRefreshToken getAccessToken() {

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("myuser", "secret");
        MutableHttpRequest<UsernamePasswordCredentials> loginRequest = HttpRequest.POST("/login", credentials);
        HttpResponse<BearerAccessRefreshToken> tokenHttpResponse = httpClient.toBlocking().exchange(loginRequest, BearerAccessRefreshToken.class);

        return tokenHttpResponse.body();
    }
}
