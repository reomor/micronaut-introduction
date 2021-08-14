package com.github.reomor.controller;

import com.github.reomor.domain.Symbol;
import com.github.reomor.repository.Store;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/market")
public class MarketController {

    private final Store store;

    public MarketController(Store store) {
        this.store = store;
    }

    @Get("/")
    public List<Symbol> all() {
        return store.getAllSymbols();
    }
}
