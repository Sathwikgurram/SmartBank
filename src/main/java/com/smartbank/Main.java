
package com.smartbank;

import com.smartbank.model.*;
import com.smartbank.service.*;
import com.smartbank.threads.TransactionTask;
import com.smartbank.config.MongoDBConnection;

import org.bson.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        try (MongoDBConnection connection = new MongoDBConnection()) {

            // 1. Test MongoDB connection
            connection.getDatabase().runCommand(
                    new Document("ping", 1)
            );

            System.out.println("MongoDB connected successfully!");

            // 2. Create storage service
            MongoDBStorageService storage =
                    new MongoDBStorageService(
                            connection.getDatabase()
                    );

            // 3. Create banking service
            BankService service = new BankServiceImpl();

            // 4. Load existing accounts from MongoDB
            List<BankAccount> savedAccounts = storage.loadAccounts();

            if (savedAccounts.isEmpty()) {

                System.out.println("No existing accounts found.");
                System.out.println("Creating initial accounts...");

                Customer customer = new Customer(
                        101,
                        "Sathwik",
                        "sathwik@gmail.com"
                );

                BankAccount savings = new SavingsAccount(
                        "SB101",
                        customer,
                        new BigDecimal("5")
                );

                BankAccount current = new CurrentAccount(
                        "CA101",
                        customer,
                        new BigDecimal("5000")
                );

                service.registerAccount(savings);
                service.registerAccount(current);

                // Initial deposits
                service.deposit("SB101", new BigDecimal("10000"));
                service.deposit("CA101", new BigDecimal("2000"));

            } else {

                System.out.println("Existing accounts found!");

                // Register loaded accounts in banking service
                for (BankAccount account : savedAccounts) {
                    service.registerAccount(account);
                }
            }

            // 5. Display loaded account balances
            System.out.println("\nAccount Balances:");

            for (BankAccount account : service.getAllAccounts()) {
                account.displayAccountDetails();
                System.out.println("----------------------");
            }

            // 6. Perform a sample transfer
            service.transfer(
                    "SB101",
                    "CA101",
                    new BigDecimal("500")
            );

            // 7. Multithreading: perform concurrent deposits
            ExecutorService executor =
                    Executors.newFixedThreadPool(5);

            for (int i = 1; i <= 10; i++) {
                executor.submit(
                        new TransactionTask(
                                service,
                                "SB101",
                                new BigDecimal("100")
                        )
                );
            }

            executor.shutdown();

            try {
                if (!executor.awaitTermination(
                        10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }

            // 8. Display final balances
            System.out.println("\nFinal Account Balances:");

            System.out.println(
                    "Savings: " + service.checkBalance("SB101")
            );

            System.out.println(
                    "Current: " + service.checkBalance("CA101")
            );

            // 9. Display transaction history
            System.out.println("\nSavings Account History:");

            for (BankAccount account : service.getAllAccounts()) {
                if (account.getAccountNumber().equals("SB101")) {
                    account.displayTransactionHistory();
                }
            }

            // 10. Save updated accounts to MongoDB
            storage.saveAccounts(service.getAllAccounts());

        } catch (Exception e) {
            System.out.println("Application error:");
            e.printStackTrace();
        }
    }
}
