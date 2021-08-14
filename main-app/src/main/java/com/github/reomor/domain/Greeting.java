package com.github.reomor.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class Greeting {
    private final String myText = "Hello world";
    private final BigDecimal id = BigDecimal.valueOf(1234567890);
    private final Instant timeUTC = Instant.ofEpochSecond(1_000_000_00);
}
