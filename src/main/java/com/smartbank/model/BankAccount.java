package com.smartbank.model;

import java.math.BigDecimal;
import java.util.*;

public class BankAccount {

    private final String accountNumber;
    private final Customer customer;
    private BigDecimal balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public BankAccount(String accountNumber, Customer customer) {

        this.accountNumber = accountNumber;
        this.customer = customer;
        this.balance = BigDecimal.ZERO;


    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    // Used when restoring an account from MongoDB
    public synchronized void restoreBalance(BigDecimal balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }

        this.balance = balance;
    }

    public synchronized void deposit(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be positive"
            );
        }

        balance = balance.add(amount);

        transactions.add(new Transaction("DEPOSIT", amount));

        System.out.println("Deposit successful!");
    }

    public synchronized void withdraw(BigDecimal amount) {
        withdrawWithLimit(amount, BigDecimal.ZERO);
    }

    public void displayAccountDetails() {

        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Holder: " + customer.getName());
        System.out.println("Balance: Rs. " + balance);
    }

    public String getAccountType() {
        return "General Bank Account";
    }

    protected synchronized void withdrawWithLimit(
            BigDecimal amount,
            BigDecimal minimumBalance) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal amount must be positive"
            );
        }

        if (balance.subtract(amount).compareTo(minimumBalance) < 0) {
            throw new IllegalStateException(
                    "Insufficient available balance"
            );
        }

        transactions.add(new Transaction("WITHDRAWAL", amount));

        balance = balance.subtract(amount);

        System.out.println("Withdrawal successful!");
    }

    public void displayTransactionHistory() {
        System.out.println("Transaction History:");

        for (Transaction transaction : transactions) {
            transaction.displayTransaction();
        }
    }
}