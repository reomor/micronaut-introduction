package com.github.reomor.util;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private RandomUtils() {
    }

    public static BigDecimal randomValue() {
        return BigDecimal.valueOf(RANDOM.nextDouble(1, 100));
    }
}
