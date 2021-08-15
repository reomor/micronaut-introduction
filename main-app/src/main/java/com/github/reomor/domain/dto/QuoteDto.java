package com.github.reomor.domain.dto;

import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;

@Introspected
public class QuoteDto {

    private Integer id;
    private BigDecimal volume;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
}
