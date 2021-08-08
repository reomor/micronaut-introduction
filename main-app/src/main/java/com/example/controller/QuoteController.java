package com.example.controller;

import com.example.error.RestError;
import com.example.repository.Store;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/quote")
public class QuoteController {

    private final Store store;

    public QuoteController(Store store) {
        this.store = store;
    }

    @Get("/{symbol}")
    public HttpResponse<?> getQuote(@PathVariable String symbol) {
        return store.getQuote(symbol)
            .map(quote -> HttpResponse.ok().<Object>body(quote))
            .orElseGet(() ->
                HttpResponse.notFound(
                    RestError.builder()
                        .status(HttpStatus.NOT_FOUND.getCode())
                        .error(HttpStatus.NOT_FOUND.name())
                        .message("Quote is not found")
                        .path("/quote/" + symbol)
                        .build()
                )
            );
    }
}
