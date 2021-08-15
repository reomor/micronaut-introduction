package com.github.reomor.controller;

import com.github.reomor.domain.Symbol;
import com.github.reomor.jpa.entity.SymbolEntity;
import com.github.reomor.jpa.repository.SymbolsRepository;
import com.github.reomor.repository.Store;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Single;

import java.util.List;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/market")
public class MarketController {

    private final Store store;
    private final SymbolsRepository symbolsRepository;

    public MarketController(Store store, SymbolsRepository symbolsRepository) {
        this.store = store;
        this.symbolsRepository = symbolsRepository;
    }

    @Get("/")
    public List<Symbol> all() {
        return store.getAllSymbols();
    }

    @Get("/jpa")
    public Single<List<SymbolEntity>> allSymbolsViaJpa() {
        return Single.just(symbolsRepository.findAll());
    }
}
