package com.smartbank.threads;

import com.smartbank.service.BankService;
import java.math.BigDecimal;

public class TransactionTask implements Runnable {

    private final BankService service;
    private final String accountNumber;
    private final BigDecimal amount;

    public TransactionTask(
            BankService service,
            String accountNumber,
            BigDecimal amount) {

        this.service = service;
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            service.deposit(accountNumber, amount);

            System.out.println(
                    Thread.currentThread().getName()
                            + " completed deposit: " + amount
            );

        } catch (RuntimeException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
    }
}