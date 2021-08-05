package com.example.controller;

import com.example.domain.Symbol;
import com.example.domain.WatchList;
import com.example.repository.AccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Single;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.controller.WatchListController.ACCOUNT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@MicronautTest
class WatchListReactiveControllerTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/account/watchlist-reactive")
    RxHttpClient httpClient;

    @Inject
    AccountStore accountStore;

    @Test
    void getWatchList_reactive_empty() {

        accountStore.deleteWatchList(ACCOUNT_ID);
        Single<WatchList> response = httpClient.retrieve(HttpRequest.GET("/"), WatchList.class).singleOrError();

        WatchList watchList = response.blockingGet();
        assertTrue(watchList.getSymbols().isEmpty());
        assertTrue(accountStore.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void getWatchList_reactiveSingle_empty() {

        accountStore.deleteWatchList(ACCOUNT_ID);
        Single<WatchList> response = httpClient.retrieve(HttpRequest.GET("/single"), WatchList.class).singleOrError();

        WatchList watchList = response.blockingGet();
        assertTrue(watchList.getSymbols().isEmpty());
        assertTrue(accountStore.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void updateWatchList() {

        List<Symbol> symbols = Stream.of("BOM", "BAM")
            .map(Symbol::new)
            .collect(Collectors.toList());

        WatchList expectedWatchList = new WatchList(symbols);
        MutableHttpRequest<WatchList> putRequest = HttpRequest.PUT("/", expectedWatchList);
        HttpResponse<WatchList> response = httpClient.exchange(putRequest, WatchList.class).blockingLast();

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

        MutableHttpRequest<WatchList> putRequest = HttpRequest.DELETE("/" + ACCOUNT_ID);
        HttpResponse<WatchList> response = httpClient.toBlocking().exchange(putRequest, WatchList.class);

        Optional<WatchList> actualWatchList = response.getBody();
        assertTrue(actualWatchList.isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expectedWatchList, actualWatchList.get());
        assertTrue(accountStore.getWatchList(ACCOUNT_ID).getSymbols().isEmpty());
    }
}
