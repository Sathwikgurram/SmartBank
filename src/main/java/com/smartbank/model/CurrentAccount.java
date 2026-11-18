package com.smartbank.model;

import java.math.BigDecimal;

public class CurrentAccount extends BankAccount {

    private final BigDecimal overdraftLimit;

    public CurrentAccount(
            String accountNumber,
            Customer customer,
            BigDecimal overdraftLimit) {

        super(accountNumber, customer);

        if (overdraftLimit == null ||
                overdraftLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Overdraft limit cannot be negative"
            );
        }

        this.overdraftLimit = overdraftLimit;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public String getAccountType() {
        return "Current Account";
    }

    @Override
    public synchronized void withdraw(BigDecimal amount) {

        withdrawWithLimit(
                amount,
                overdraftLimit.negate()
        );
    }
}