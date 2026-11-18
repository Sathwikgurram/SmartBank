package com.smartbank.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection implements AutoCloseable {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoDBConnection() {

        String connectionString = "mongodb://localhost:27017";

        mongoClient = MongoClients.create(connectionString);

        database = mongoClient.getDatabase("smartbank");
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}