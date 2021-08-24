package com.github.reomor.producer;

import com.github.reomor.domain.ExternalQuote;
import com.github.reomor.domain.PriceUpdate;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.reomor.producer.Topic.EXTERNAL_QUOTES;
import static java.util.stream.Collectors.*;

@KafkaListener(
  clientId = "mn-pricing-external-quote-consumer",
  groupId = "external-quote-consumer",
  batch = true
)
public class ExternalQuoteConsumer {

    private static final Logger log = LoggerFactory.getLogger(ExternalQuoteConsumer.class);

    private final PriceUpdateProducer priceUpdateProducer;

    public ExternalQuoteConsumer(PriceUpdateProducer priceUpdateProducer) {
        this.priceUpdateProducer = priceUpdateProducer;
    }

    @Topic(EXTERNAL_QUOTES)
    void receive(List<ExternalQuote> quoteList) {
        log.debug("Consuming: {}", quoteList);
        priceUpdateProducer.send(
            quoteList.stream()
              .map(quote ->
                new PriceUpdate(
                  quote.getSymbol(),
                  quote.getLastPrice()
                )
              ).collect(toList())
          )
          .doOnError(e -> log.error("Fail during produce: {}", e.getMessage()))
          .subscribe(recordMetadata ->
            log.debug(
              "Record sent: topic={}, offset={}",
              recordMetadata.topic(),
              recordMetadata.offset()
            )
          );
    }
}
