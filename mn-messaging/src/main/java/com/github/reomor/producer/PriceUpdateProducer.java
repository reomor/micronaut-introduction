package com.github.reomor.producer;

import com.github.reomor.domain.PriceUpdate;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.github.reomor.producer.Topic.PRICE_UPDATE;

@KafkaClient(batch = true)
public interface PriceUpdateProducer {

    @Topic(PRICE_UPDATE)
    Flux<RecordMetadata> send(List<PriceUpdate> priceUpdateList);
}
