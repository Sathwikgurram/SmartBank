package com.smartbank.service;

import com.smartbank.model.BankAccount;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;

public class BankServiceImpl implements BankService {

    private final Map<String, BankAccount> accounts =
            new HashMap<>();

    @Override
    public void registerAccount(BankAccount account) {

        if (account == null) {
            throw new IllegalArgumentException(
                    "Account cannot be null"
            );
        }

        String accountNumber = account.getAccountNumber();

        if (accounts.containsKey(accountNumber)) {
            throw new IllegalArgumentException(
                    "Account already exists"
            );
        }

        accounts.put(accountNumber, account);

        System.out.println("Account registered successfully!");
    }

    @Override
    public synchronized void deposit(String accountNumber, BigDecimal amount) {

        BankAccount account = findAccount(accountNumber);

        account.deposit(amount);
    }

    @Override
    public synchronized void withdraw(String accountNumber, BigDecimal amount) {

        BankAccount account = findAccount(accountNumber);

        account.withdraw(amount);
    }

    @Override
    public synchronized BigDecimal checkBalance(String accountNumber) {

        BankAccount account = findAccount(accountNumber);

        return account.getBalance();
    }

    private BankAccount findAccount(String accountNumber) {

        BankAccount account = accounts.get(accountNumber);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Account not found: " + accountNumber
            );
        }

        return account;
    }

    @Override
    public synchronized void transfer(String fromAccount, String toAccount, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid transfer amount");
        }

        if (fromAccount.equals(toAccount)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        BankAccount sender = findAccount(fromAccount);
        BankAccount receiver = findAccount(toAccount);

        sender.withdraw(amount);
        receiver.deposit(amount);

        System.out.println("Transfer successful!");
    }

    @Override
    public synchronized Collection<BankAccount> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }
}