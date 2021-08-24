package com.github.reomor.producer;

import com.github.reomor.domain.ExternalQuote;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

import static com.github.reomor.producer.Topic.EXTERNAL_QUOTES;

@KafkaClient(
  id = "external-quote-producer"
)
public interface ExternalQuoteProducer {

    @Topic(EXTERNAL_QUOTES)
    void send(@KafkaKey String symbol, ExternalQuote externalQuote);
}
