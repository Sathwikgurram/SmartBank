package com.smartbank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private final String transactionType;
    private final BigDecimal amount;
    private final LocalDateTime dateTime;

    public Transaction(String transactionType, BigDecimal amount) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.dateTime = LocalDateTime.now();
    }

    public void displayTransaction() {
        System.out.println(
                transactionType + " | Rs. " + amount + " | " + dateTime
        );
    }
}