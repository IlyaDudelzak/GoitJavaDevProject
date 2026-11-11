package com.dudelzak.goit.java.devproject;

import com.dudelzak.goit.java.devproject.security.User;
import com.dudelzak.goit.java.devproject.security.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PostgresContainerTest {

    static PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:17-alpine");

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    UserRepository userRepository;

    @Test
    void testPostgresContainer() {
        String username = postgresContainer.getUsername();
        assertEquals("test", username);
        long initialCount = userRepository.count();
        User user = userRepository.save(User.builder()
                .username("testuser_db_" + System.currentTimeMillis())
                .password("wrongPassword123")
                .createdAt(java.time.Instant.now())
                .build());
        assertNotNull(user.getId());
        assertEquals(initialCount + 1, userRepository.count());
    }
}
