package com.github.reomor.repository.impl;

import com.github.reomor.domain.Quote;
import com.github.reomor.domain.Symbol;
import com.github.reomor.repository.Store;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@Singleton
public class InMemoryStore implements Store {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final List<Symbol> symbols = Stream.of("BIM", "hYandex", "Lol")
        .map(Symbol::new)
        .collect(Collectors.toList());
    private final Map<String, Quote> quoteMap = new ConcurrentHashMap<>();

    public InMemoryStore() {
        symbols.forEach(symbol -> quoteMap.put(symbol.getName(), randomQuoteFunction(symbol)));
    }

    @Override
    public List<Symbol> getAllSymbols() {
        return symbols;
    }

    @Override
    public Optional<Quote> getQuote(String symbol) {
        return ofNullable(quoteMap.get(symbol));
    }

    @Override
    public void updateQuote(Quote quote) {
        quoteMap.put(quote.getSymbol().getName(), quote);
    }

    private BigDecimal randomBigDecimal() {
        return BigDecimal.valueOf(random.nextDouble(1, 100));
    }

    private Quote randomQuoteFunction(Symbol symbol) {
        return Quote.builder()
            .symbol(symbol)
            .bid(randomBigDecimal())
            .ask(randomBigDecimal())
            .lastPrice(randomBigDecimal())
            .volume(randomBigDecimal())
            .build();
    }
}
