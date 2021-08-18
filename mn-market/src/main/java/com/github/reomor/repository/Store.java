package com.github.reomor.repository;

import com.github.reomor.domain.Quote;
import com.github.reomor.domain.Symbol;

import java.util.List;
import java.util.Optional;

public interface Store {

    List<Symbol> getAllSymbols();

    Optional<Quote> getQuote(String symbol);

    void updateQuote(Quote quote);
}
