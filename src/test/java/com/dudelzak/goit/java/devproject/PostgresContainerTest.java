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

            // Проверяем, что база поднялась
            String jdbcUrl = postgresContainer.getJdbcUrl();
            String username = postgresContainer.getUsername();
            String password = postgresContainer.getPassword();

            System.out.println("JDBC URL: " + jdbcUrl);
            System.out.println("User: " + username);
            System.out.println("Password: " + password);

            assertEquals("test", username);
        }
    }
}