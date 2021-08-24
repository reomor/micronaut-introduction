package com.github.reomor;

import com.github.reomor.domain.ExternalQuote;
import com.github.reomor.producer.ExternalQuoteProducer;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StreamUtils;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;
import org.awaitility.Awaitility;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.reomor.producer.Topic.EXTERNAL_QUOTES;
import static com.github.reomor.util.RandomUtils.randomValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class TestExternalQuoteProducer {

    private static final Logger log = LoggerFactory.getLogger(TestExternalQuoteProducer.class);
    public static final String PROPERTY_NAME = "TestExternalQuoteProducer";
    private static ApplicationContext context;

    @Rule
    public static KafkaContainer kafkaContainer = new KafkaContainer(
      DockerImageName.parse("confluentinc/cp-kafka:5.4.3")
    );

    @BeforeAll
    static void beforeAll() {
        kafkaContainer.start();
        log.debug("Bootstrap servers: {}", kafkaContainer.getBootstrapServers());

        context = ApplicationContext.run(
          CollectionUtils.mapOf(
            "kafka.bootstrap.servers", kafkaContainer.getBootstrapServers(),
            PROPERTY_NAME, StringUtils.TRUE
          ),
          Environment.TEST
        );
    }

    @AfterAll
    static void afterAll() {
        kafkaContainer.stop();
        context.close();
    }

    @Test
    void produce10Records() {

        int records = 10;
        ExternalQuoteObserver observer = context.getBean(ExternalQuoteObserver.class);
        ExternalQuoteProducer producer = context.getBean(ExternalQuoteProducer.class);

        IntStream.range(0, records)
          .forEach(index -> {
                String symbol = "TEST-" + index;
                producer.send(
                  symbol,
                  new ExternalQuote(
                    symbol,
                    randomValue(),
                    randomValue())
                );
            }
          );

        Awaitility.await().untilAsserted(() -> {
            assertEquals(records, observer.inspected.size());
        });
    }

    @Singleton
    @Requires(env = Environment.TEST)
    @Requires(property = PROPERTY_NAME, value = StringUtils.TRUE)
    static class ExternalQuoteObserver {

        List<ExternalQuote> inspected = new ArrayList<>();

        @KafkaListener(offsetReset = OffsetReset.EARLIEST)
        @Topic(EXTERNAL_QUOTES)
        void receive(ExternalQuote quote) {
            log.debug("Consumed: {}", quote);
            inspected.add(quote);
        }
    }
}
