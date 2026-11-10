package com.dudelzak.goit.java.devproject;

import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostgresContainerTest {

    @Test
    void testPostgresContainer() {
        // Создаем контейнер PostgreSQL
        try (PostgreSQLContainer
                     postgresContainer = new PostgreSQLContainer("postgres:17-alpine")) {
            postgresContainer.start();
            String username = postgresContainer.getUsername();
            assertEquals("test", username);
        }
    }
}