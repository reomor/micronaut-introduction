package com.github.reomor;

import com.github.reomor.websocket.WebSocketClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.rxjava2.http.client.websockets.RxWebSocketClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static com.github.reomor.websocket.Api.WEBSOCKET_SERVER;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class WebSocketServerTest {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServerTest.class);

    @Inject
    @Client("http://localhost:8180")
    RxWebSocketClient client;

    WebSocketClient webSocketClient;

    @BeforeEach
    void beforeEach() {

        webSocketClient = client.connect(WebSocketClient.class, WEBSOCKET_SERVER)
          .blockingFirst();

        log.info("Session id: {}", webSocketClient.getSession().getId());
    }

    @Test
    void canReceiveMessagesSynchronously() {

        webSocketClient.send("Hello");

        await()
          .timeout(Duration.of(3, ChronoUnit.SECONDS))
          .untilAsserted(() -> {

              String[] messages = webSocketClient.getObservedMessages().toArray(new String[0]);

              log.info(
                "Received messages: count={}, messages={}",
                messages.length,
                messages
              );

              assertTrue(messages.length > 0);
              assertEquals("Connected!", messages[0]);
              assertEquals("Not supported => ->Hello<-", messages[1]);
            }
          );
    }

    @Test
    void canReceiveMessagesAsynchronously() {

        log.info("Send: {}", webSocketClient.sendReactive("Hello").blockingGet());

        await()
          .timeout(Duration.of(3, ChronoUnit.SECONDS))
          .untilAsserted(() -> {

                String[] messages = webSocketClient.getObservedMessages().toArray(new String[0]);

                log.info(
                  "Received messages: count={}, messages={}",
                  messages.length,
                  messages
                );

                assertTrue(messages.length > 0);
                assertEquals("Connected!", messages[0]);
                assertEquals("Not supported => ->Hello<-", messages[1]);
            }
          );
    }
}
