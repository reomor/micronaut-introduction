package com.example.repository;

import com.example.domain.Quote;
import com.example.domain.Symbol;

import java.util.List;
import java.util.Optional;

public interface Store {

    List<Symbol> getAllSymbols();

    Optional<Quote> getQuote(String symbol);

    void updateQuote(Quote quote);
}
