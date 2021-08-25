package com.github.reomor;

import com.github.reomor.domain.ExternalQuote;
import com.github.reomor.domain.PriceUpdate;
import io.micronaut.configuration.kafka.annotation.*;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
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
import java.util.List;
import java.util.stream.IntStream;

import static com.github.reomor.producer.Topic.EXTERNAL_QUOTES;
import static com.github.reomor.producer.Topic.PRICE_UPDATE;
import static com.github.reomor.util.RandomUtils.randomValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class TestExternalQuoteConsumer {

    private static final Logger log = LoggerFactory.getLogger(TestExternalQuoteConsumer.class);
    public static final String PROPERTY_NAME = "TestExternalQuoteConsumer";
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
    void consuming() {
        int size = 4;
        TestExternalQuoteProducer producer = context.getBean(TestExternalQuoteProducer.class);
        IntStream.range(0, size)
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
        PriceUpdateObserver observer = context.getBean(PriceUpdateObserver.class);
        Awaitility.await().untilAsserted(() -> {
            assertEquals(4, observer.inspected.size());
        });
    }

    @KafkaListener(
      clientId = "price-update-observer",
      offsetReset = OffsetReset.EARLIEST
    )
    @Requires(env = Environment.TEST)
    @Requires(property = PROPERTY_NAME, value = StringUtils.TRUE)
    static class PriceUpdateObserver {

        List<PriceUpdate> inspected = new ArrayList<>();

        @KafkaListener(offsetReset = OffsetReset.EARLIEST)
        @Topic(PRICE_UPDATE)
        void receive(List<PriceUpdate> priceUpdates) {
            log.debug("Consumed: {}", priceUpdates);
            inspected.addAll(priceUpdates);
        }
    }

    @KafkaClient(
      id = "external-quote-producer"
    )
    @Requires(env = Environment.TEST)
    @Requires(property = PROPERTY_NAME, value = StringUtils.TRUE)
    public interface TestExternalQuoteProducer {

        @Topic(EXTERNAL_QUOTES)
        void send(@KafkaKey String symbol, ExternalQuote externalQuote);
    }
}
