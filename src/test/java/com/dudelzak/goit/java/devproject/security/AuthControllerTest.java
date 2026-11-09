package com.dudelzak.goit.java.devproject.security;

import com.dudelzak.goit.java.devproject.security.dto.AuthRequest;
import com.dudelzak.goit.java.devproject.security.dto.RegisterRequest;
import com.dudelzak.goit.java.devproject.security.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            "postgres:17-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        userRepository.deleteAll();
        RegisterRequest registerRequest = new RegisterRequest("John_Doe", "Password123");
        given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/v1/auth/register");
    }

    @Test
    void registerNewUserShouldReturn200() {
        RegisterRequest request = new RegisterRequest("ExampleUser", "Passwrd125");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .log().all()
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(200);
    }

    @Test
    void registerSameUserShouldReturn401() {
        RegisterRequest request = new RegisterRequest("TheThirdUser", "SecurePwd103");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .log().all()
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(200);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .log().all()
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(409);
    }


    @Test
    void loginOldUserShouldReturn200() {
        AuthRequest request = new AuthRequest("John_Doe", "Password123");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .log().all()
                .when()
                .post("/api/v1/auth/token")
                .then()
                .statusCode(200);
    }

    @Test
    void loginWrongUserShouldReturn401() {
        AuthRequest request = new AuthRequest("John_Doe", "Password");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .log().all()
                .when()
                .post("/api/v1/auth/token")
                .then()
                .statusCode(401);
    }
}