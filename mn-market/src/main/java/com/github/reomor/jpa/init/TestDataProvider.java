package com.github.reomor.jpa.init;

import com.github.reomor.jpa.entity.QuotesEntity;
import com.github.reomor.jpa.entity.SymbolEntity;
import com.github.reomor.jpa.repository.QuotesRepository;
import com.github.reomor.jpa.repository.SymbolsRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@Slf4j
@Singleton
@Requires(notEnv = Environment.TEST)
public class TestDataProvider {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private final SymbolsRepository symbolsRepository;
    private final QuotesRepository quotesRepository;

    public TestDataProvider(
      SymbolsRepository userRepository,
      QuotesRepository quotesRepository
    ) {
        this.symbolsRepository = userRepository;
        this.quotesRepository = quotesRepository;
    }

    @EventListener
    public void init(StartupEvent event) {

        if (symbolsRepository.findAll().isEmpty()) {
            log.info("Add test data for symbols");
            Stream.of("IBM", "AMZN", "INTEL")
              .map(SymbolEntity::new)
              .forEach(symbolsRepository::save);
        }

        if (quotesRepository.findAll().isEmpty()) {
            log.info("Add test data for quotes");
            symbolsRepository.findAll()
              .forEach(symbol -> {
                  var quote = new QuotesEntity();
                  quote.setSymbol(symbol);
                  quote.setAsk(randomValue());
                  quote.setBid(randomValue());
                  quote.setVolume(randomValue());
                  quote.setLastPrice(randomValue());
                  quotesRepository.save(quote);
              });
        }
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(RANDOM.nextDouble(1, 100));
    }
}
