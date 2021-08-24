package com.github.reomor.scheduler;

import com.github.reomor.domain.ExternalQuote;
import com.github.reomor.producer.ExternalQuoteProducer;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.reomor.util.RandomUtils.randomValue;

@Singleton
@Requires(notEnv = Environment.TEST)
public class EventScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventScheduler.class);
    private static final List<String> SYMBOLS = Arrays.asList("AAPL", "IBM", "AMZ", "NTFX");
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private final ExternalQuoteProducer externalQuoteProducer;

    public EventScheduler(ExternalQuoteProducer externalQuoteProducer) {
        this.externalQuoteProducer = externalQuoteProducer;
    }

    @Scheduled(fixedDelay = "10s")
    void generate() {
        ExternalQuote quote = new ExternalQuote(
          SYMBOLS.get(RANDOM.nextInt(0, SYMBOLS.size() - 1)),
          randomValue(),
          randomValue()
        );
        log.debug("Producing: {}", quote);
        externalQuoteProducer.send(quote.getSymbol(), quote);
    }
}
