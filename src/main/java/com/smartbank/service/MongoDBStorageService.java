package com.smartbank.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.smartbank.model.BankAccount;
import org.bson.Document;

import java.util.Collection;

import com.smartbank.model.Customer;
import com.smartbank.model.SavingsAccount;
import com.smartbank.model.CurrentAccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoDBStorageService {

    private final MongoCollection<Document> accountsCollection;

    public MongoDBStorageService(MongoDatabase database) {
        accountsCollection =
                database.getCollection("accounts");
    }

    public void saveAccounts(Collection<BankAccount> accounts) {

        for (BankAccount account : accounts) {

            // 1. Create customer document
            Document customer = new Document()
                    .append("customerId",
                            account.getCustomer().getCustomerId())
                    .append("name",
                            account.getCustomer().getName())
                    .append("email",
                            account.getCustomer().getEmail());

            // 2. Create account document
            Document accountDocument = new Document()
                    .append("accountNumber",
                            account.getAccountNumber())
                    .append("accountType",
                            account.getAccountType())
                    .append("balance",
                            account.getBalance().toPlainString())
                    .append("customer", customer);

            // 3. Save account-specific properties
            if (account instanceof SavingsAccount savings) {
                accountDocument.append(
                        "interestRate",
                        savings.getInterestRate().toPlainString()
                );
            }

            if (account instanceof CurrentAccount current) {
                accountDocument.append(
                        "overdraftLimit",
                        current.getOverdraftLimit().toPlainString()
                );
            }

            // 4. Insert or update MongoDB document
            accountsCollection.replaceOne(
                    eq("accountNumber", account.getAccountNumber()),
                    accountDocument,
                    new ReplaceOptions().upsert(true)
            );
        }

        System.out.println("Accounts saved to MongoDB!");
    }

    public List<BankAccount> loadAccounts() {

        List<BankAccount> accounts = new ArrayList<>();

        for (Document document : accountsCollection.find()) {

            // 1. Read customer information
            Document customerDocument =
                    document.get("customer", Document.class);

            Customer customer = new Customer(
                    customerDocument.getInteger("customerId"),
                    customerDocument.getString("name"),
                    customerDocument.getString("email")
            );

            // 2. Read common account information
            String accountNumber =
                    document.getString("accountNumber");

            String accountType =
                    document.getString("accountType");

            BigDecimal balance =
                    new BigDecimal(document.getString("balance"));

            // 3. Reconstruct the correct account type
            BankAccount account;

            switch (accountType) {

                case "Savings Account":
                    account = new SavingsAccount(
                            accountNumber,
                            customer,
                            new BigDecimal(
                                    document.getString("interestRate")
                            )
                    );
                    break;

                case "Current Account":
                    account = new CurrentAccount(
                            accountNumber,
                            customer,
                            new BigDecimal(
                                    document.getString("overdraftLimit")
                            )
                    );
                    break;

                default:
                    account = new BankAccount(
                            accountNumber,
                            customer
                    );
            }

            // 4. Restore the saved balance
            account.restoreBalance(balance);

            accounts.add(account);
        }

        System.out.println("Accounts loaded from MongoDB!");

        return accounts;
    }
}