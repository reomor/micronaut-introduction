package com.github.reomor.controller.dto;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class RateLimitedTimeResponse {

    private String response;

    public RateLimitedTimeResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
