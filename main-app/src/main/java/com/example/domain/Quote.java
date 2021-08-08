package com.example.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Quote", description = "Quote description")
public class Quote {

    @Schema(name = "Symbol", description = "Symbol description")
    private Symbol symbol;

    @Schema(name = "Some value")
    private BigDecimal bid;

    @Schema(name = "Some value")
    private BigDecimal ask;

    @Schema(name = "Some value")
    private BigDecimal lastPrice;

    @Schema(name = "Some value")
    private BigDecimal volume;
}
