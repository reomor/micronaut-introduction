package com.example.controller;

import com.example.domain.WatchList;
import com.example.repository.AccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.UUID;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/account/watchlist")
public class WatchListController {

    static final UUID ACCOUNT_ID = UUID.randomUUID();

    private final AccountStore accountStore;

    public WatchListController(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public WatchList get() {

        return accountStore.getWatchList(ACCOUNT_ID);
    }

    @Put(
        consumes = MediaType.APPLICATION_JSON,
        produces = MediaType.APPLICATION_JSON
    )
    public WatchList update(@Body WatchList watchList) {
        return accountStore.updateWatchList(ACCOUNT_ID, watchList);
    }

    @Delete(
        value = "/{accountId}",
        consumes = MediaType.APPLICATION_JSON,
        produces = MediaType.APPLICATION_JSON
    )
    public WatchList delete(@PathVariable UUID accountId) {
        return accountStore.deleteWatchList(accountId);
    }
}
