package com.github.reomor;

import io.micronaut.runtime.Micronaut;

public class MongoDbApplication {
    public static void main(String[] args) {
        Micronaut.run(MongoDbApplication.class, args);
    }
}
