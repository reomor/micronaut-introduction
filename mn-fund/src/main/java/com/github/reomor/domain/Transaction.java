package com.github.reomor.domain;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.model.naming.NamingStrategies;

import java.math.BigDecimal;

@MappedEntity(value = "transactions")
public class Transaction {

    @Id
    private Long transactionId;
    private final String user;
    private final String symbol;
    private final BigDecimal modification;

    public Transaction(Long transactionId, String user, String symbol, BigDecimal modification) {
        this.transactionId = transactionId;
        this.user = user;
        this.symbol = symbol;
        this.modification = modification;
    }

    public Transaction(String user, String symbol, BigDecimal modification) {
        this.user = user;
        this.symbol = symbol;
        this.modification = modification;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getUser() {
        return user;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getModification() {
        return modification;
    }
}
