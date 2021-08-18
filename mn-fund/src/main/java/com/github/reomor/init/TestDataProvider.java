package com.github.reomor.init;

import com.github.reomor.domain.Transaction;
import com.github.reomor.repository.TransactionRepository;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import liquibase.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class TestDataProvider {

    private static final Logger log = LoggerFactory.getLogger(TestDataProvider.class);

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private final TransactionRepository transactionRepository;

    public TestDataProvider(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Scheduled(fixedDelay = "2s")
    public void generate() {

        transactionRepository.save(
          new Transaction(
            UUID.randomUUID().toString(),
            "SYMBOL",
            randomValue()
          )
        );

        log.info("Transactions amount: {}", transactionRepository.findAll().size());
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(RANDOM.nextDouble(1, 100));
    }
}
