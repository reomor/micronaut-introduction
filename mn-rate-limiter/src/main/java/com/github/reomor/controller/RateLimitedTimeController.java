package com.github.reomor.controller;

import com.github.reomor.controller.dto.RateLimitedTimeResponse;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalTime;

import static java.time.LocalTime.now;

@Controller("/time")
public class RateLimitedTimeController {

    private static final Logger log = LoggerFactory.getLogger(RateLimitedTimeController.class);

    private static final String TIME_RATE_LIMITER_KEY = "EXAMPLE::TIME";
    private static final String UTC_TIME_RATE_LIMITER_KEY = "EXAMPLE::UTC";
    private static final int QUOTA_PER_MINUTE = 5;

    private final StatefulRedisConnection<String, String> redisConnection;

    public RateLimitedTimeController(StatefulRedisConnection<String, String> redisConnection) {
        this.redisConnection = redisConnection;
    }

    @Get
    public RateLimitedTimeResponse time() {
        return getRateLimitedTimeResponse(TIME_RATE_LIMITER_KEY, now());
    }

    // by default everything is on default-nioEventLoopGroup, so blocking call should run on this executor
//    @ExecuteOn(TaskExecutors.IO)
    @Get("/utc")
    public RateLimitedTimeResponse timeUtc() {
        return getRateLimitedTimeResponse(UTC_TIME_RATE_LIMITER_KEY, now(Clock.systemUTC()));
    }

    private RateLimitedTimeResponse getRateLimitedTimeResponse(String redisKey, LocalTime time) {

        String value = redisConnection.sync().get(redisKey);
        int currentQuota = value == null ? 0 : Integer.parseInt(value);

        if (currentQuota >= QUOTA_PER_MINUTE) {

            String errorMessage = String.format(
              "Rate limit reached key=%s %s/%s",
              redisKey,
              currentQuota,
              QUOTA_PER_MINUTE
            );

            log.warn(errorMessage);

            return new RateLimitedTimeResponse(errorMessage);
        }

        log.info("Current limit: {}/{}", currentQuota, QUOTA_PER_MINUTE);
        increaseCurrentQuota(redisKey);

        return new RateLimitedTimeResponse(time.toString());
    }

    private void increaseCurrentQuota(String key) {
        // or change this to async
//        RedisCommands<String, String> commands = redisConnection.sync();
        RedisAsyncCommands<String, String> commands = redisConnection.async();
        commands.multi();
        commands.incrby(key, 1);
        int remainingSeconds = 60 - now().getSecond();
        commands.expire(key, remainingSeconds);
        commands.exec();
    }
}
