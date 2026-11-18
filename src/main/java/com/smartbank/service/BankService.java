package com.smartbank.service;

import com.smartbank.model.BankAccount;

import java.util.Collection;

import java.math.BigDecimal;

public interface BankService {

    void registerAccount(BankAccount account);

    void deposit(String accountNumber, BigDecimal amount);

    void withdraw(String accountNumber, BigDecimal amount);

    BigDecimal checkBalance(String accountNumber);

    void transfer(String fromAccount, String toAccount, BigDecimal amount);

    Collection<BankAccount> getAllAccounts();

//    void displayAccountDetails(String accountNumber);
}