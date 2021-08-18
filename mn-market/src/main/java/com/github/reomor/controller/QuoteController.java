package com.github.reomor.controller;

import com.github.reomor.domain.dto.QuoteDto;
import com.github.reomor.error.RestError;
import com.github.reomor.jpa.entity.QuotesEntity;
import com.github.reomor.jpa.entity.SymbolEntity;
import com.github.reomor.jpa.repository.QuotesRepository;
import com.github.reomor.repository.Store;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/quote")
public class QuoteController {

    private final Store store;
    private final QuotesRepository quotesRepository;

    public QuoteController(Store store, QuotesRepository quotesRepository) {
        this.store = store;
        this.quotesRepository = quotesRepository;
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

    @Get("/jpa")
    public List<QuotesEntity> getQuotesViaJpa() {
        return quotesRepository.findAll();
    }

    @Get("/jpa/symbol/{symbol}")
    public HttpResponse<?> getQuoteViaJpa(@PathVariable String symbol) {
        return quotesRepository.findBySymbol(new SymbolEntity(symbol))
          .map(quote -> HttpResponse.ok().<Object>body(quote))
          .orElseGet(() ->
            HttpResponse.notFound(
              RestError.builder()
                .status(HttpStatus.NOT_FOUND.getCode())
                .error(HttpStatus.NOT_FOUND.name())
                .message("Quote is not found")
                .path("/jpa/symbol/" + symbol)
                .build()
            )
          );
    }

    @Get("/jpa/{id}")
    public HttpResponse<?> getQuoteViaJpa(@PathVariable Integer id) {
        return quotesRepository.findById(id)
          .map(quote -> HttpResponse.ok().<Object>body(quote))
          .orElseGet(() ->
            HttpResponse.notFound(
              RestError.builder()
                .status(HttpStatus.NOT_FOUND.getCode())
                .error(HttpStatus.NOT_FOUND.name())
                .message("Quote is not found")
                .path("/quote/jpa/" + id)
                .build()
            )
          );
    }

    @Get("/jpa/list/desc")
    public List<QuoteDto> getQuotesViaJpaDesc() {
        return quotesRepository.listOrderByVolumeDesc();
    }

    @Get("/jpa/list/asc")
    public List<QuotesEntity> getQuotesViaJpaAsc() {
        return quotesRepository.listOrderByVolumeAsc();
    }

    @Get("/jpa/list/filter")
    public List<QuoteDto> getQuotesWithFilterViaJpaDesc(@QueryValue BigDecimal volume) {
        return quotesRepository.findAllByVolumeGreaterThanOrderByVolumeAsc(volume);
    }

    @Get("/jpa/list/paginationWithFilter{?volume,page,size}")
    public List<QuoteDto> getQuotesViaJpaPagination(
      @QueryValue Optional<Integer> page,
      @QueryValue Optional<Integer> size,
      @QueryValue Optional<BigDecimal> volume
    ) {
        var volumeValue = volume.orElse(BigDecimal.ZERO);
        return quotesRepository.findAllByVolumeGreaterThan(
          volumeValue,
          Pageable.from(
            page.orElse(0),
            size.orElse(10)
          )
        );
    }

    @Get("/jpa/list/pagination{?page,size}")
    public List<QuoteDto> getQuotesViaJpaPagination(
      @QueryValue Optional<Integer> page,
      @QueryValue Optional<Integer> size
    ) {
        return quotesRepository.list(
          Pageable.from(
            page.orElse(0),
            size.orElse(10)
          )
        ).getContent();
    }
}
