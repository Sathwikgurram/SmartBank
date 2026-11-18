package com.smartbank.model;

import java.math.BigDecimal;

public class SavingsAccount extends BankAccount {

    private BigDecimal interestRate;

    public SavingsAccount(
            String accountNumber,
            Customer customer,
            BigDecimal interestRate) {

        super(accountNumber, customer);

        if (interestRate == null ||
                interestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Interest rate cannot be negative"
            );
        }

        this.interestRate = interestRate;
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public BigDecimal calculateInterest() {

        return getBalance()
                .multiply(interestRate)
                .divide(new BigDecimal("100"));
    }

    public BigDecimal displayInterest() {

        return calculateInterest();
    }
}