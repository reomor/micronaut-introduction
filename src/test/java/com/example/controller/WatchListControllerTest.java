package com.example.controller;

import com.example.client.JwtWatchListClient;
import com.example.domain.Symbol;
import com.example.domain.WatchList;
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
import io.reactivex.Single;
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
    @Client("/")
    JwtWatchListClient client;

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

        accountStore.deleteWatchList(ACCOUNT_ID);
        Single<WatchList> response = client.retrieve(getAuthorizationHeader()).singleOrError();

        var watchList = response.blockingGet();
        assertTrue(watchList.getSymbols().isEmpty());
        assertTrue(accountStore.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void getWatchList_notEmpty() {

        List<Symbol> symbols = Stream.of("BOM", "BAM")
            .map(Symbol::new)
            .collect(Collectors.toList());
        accountStore.updateWatchList(ACCOUNT_ID, new WatchList(symbols));

        WatchList watchList = client.retrieve(getAuthorizationHeader()).singleOrError().blockingGet();

        assertEquals(symbols.size(), watchList.getSymbols().size());
        assertEquals(symbols.size(), accountStore.getWatchList(ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void updateWatchList() {

        List<Symbol> symbols = Stream.of("BOM", "BAM")
            .map(Symbol::new)
            .collect(Collectors.toList());

        WatchList expectedWatchList = new WatchList(symbols);

        HttpResponse<WatchList> response = client.put(getAuthorizationHeader(), expectedWatchList);

        Optional<WatchList> actualWatchList = response.getBody();
        assertTrue(actualWatchList.isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expectedWatchList, actualWatchList.get());
    }

    @Test
    void deleteWatchList() {

        List<Symbol> symbols = Stream.of("BOM", "BAM")
            .map(Symbol::new)
            .collect(Collectors.toList());
        WatchList expectedWatchList = new WatchList(symbols);
        accountStore.updateWatchList(ACCOUNT_ID, new WatchList(symbols));

        HttpResponse<WatchList> response = client.delete(getAuthorizationHeader(), ACCOUNT_ID);

        Optional<WatchList> actualWatchList = response.getBody();
        assertTrue(actualWatchList.isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expectedWatchList, actualWatchList.get());
        assertTrue(accountStore.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());
    }

    private BearerAccessRefreshToken getBearerToken() {
       return client.login(new UsernamePasswordCredentials("myuser", "secret"));
    }

    private String getAuthorizationHeader() {
        return "Bearer " + getBearerToken().getAccessToken();
    }
}
