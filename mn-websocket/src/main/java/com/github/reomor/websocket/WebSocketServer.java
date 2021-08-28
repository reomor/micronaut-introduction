package com.github.reomor.websocket;

import io.micronaut.websocket.CloseReason;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import static com.github.reomor.websocket.Api.WEBSOCKET_SERVER;

/**
 * Test via https://websocketking.com
 * ws://localhost:8180/ws/simple/prices
 */
@ServerWebSocket(WEBSOCKET_SERVER)
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    @OnOpen
    public Publisher<String> onOpen(WebSocketSession session) {
        return session.send("Connected!");
    }

    @OnMessage
    public Publisher<String> onMessage(String message, WebSocketSession session) {

        log.info("Received message: {}, from session with id: {}", message, session.getId());

        if (message.contentEquals("disconnect me")) {
            log.info("Client closes the connection!");
            session.close(CloseReason.NORMAL);
            return Mono.empty();
        }

        return session.send("Not supported => ->" + message + "<-");
    }

    @OnClose
    public void onClose(WebSocketSession session) {
        log.info("Session closed: {}", session.getId());
    }
}
