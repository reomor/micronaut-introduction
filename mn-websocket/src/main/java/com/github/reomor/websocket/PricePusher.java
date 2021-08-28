package com.github.reomor.websocket;

import com.github.reomor.domain.PriceUpdate;
import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.websocket.WebSocketBroadcaster;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class PricePusher {

    private final WebSocketBroadcaster broadcaster;

    public PricePusher(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Scheduled(fixedDelay = "5s")
    public void pushPrice() {
        broadcaster.broadcastSync(
          new PriceUpdate(
            "AMZB",
            randomValue()
          )
        );
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(1500, 2000));
    }
}
