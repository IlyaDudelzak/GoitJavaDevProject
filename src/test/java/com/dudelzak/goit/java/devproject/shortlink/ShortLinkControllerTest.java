package com.dudelzak.goit.java.devproject.shortlink;

import com.dudelzak.goit.java.devproject.shortlink.dto.CreateShortLinkRequest;
import com.dudelzak.goit.java.devproject.shortlink.dto.EditShortLinkRequest;
import com.dudelzak.goit.java.devproject.security.dto.RegisterRequest;
import com.dudelzak.goit.java.devproject.shortlink.dto.ShortLinkResponse;
import com.dudelzak.goit.java.devproject.shortlink.ShortLinkRepository;
import com.dudelzak.goit.java.devproject.security.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShortLinkControllerTest {

    @LocalServerPort
    private Integer port;

    private static String token = null;

    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    ShortLinkRepository shortLinkRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;

        userRepository.deleteAll();
        shortLinkRepository.deleteAll();

        RegisterRequest registerRequest = new RegisterRequest("John_Doe", "Password123");
        token = given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .extract()
                .path("token");
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private Response sendCreateRequest(CreateShortLinkRequest request, boolean authorized) {
        var spec = given().contentType(ContentType.JSON).body(request);
        if (authorized) {
            spec.header("Authorization", "Bearer " + token);
        }
        return spec.when().post("/api/v1/link/create");
    }

    private CreateShortLinkRequest createDefaultRequest(String customCode, Instant endsAt) {
        return CreateShortLinkRequest.builder()
                .customCode(customCode)
                .expiresAt(endsAt)
                .originalUrl("https://example.com/")
                .build();
    }

    private List<ShortLinkResponse> getListOfLinks(String endpoint) {
        ShortLinkResponse[] responses = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .extract()
                .as(ShortLinkResponse[].class);

        return List.of(responses);
    }

    @Test
    void createShortLinkAuthorizedShouldReturn200() {
        var request = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        sendCreateRequest(request, true)
                .then()
                .statusCode(200);
    }

    @Test
    void createShortLinkUnAuthorizedShouldReturn401() {
        var request = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        sendCreateRequest(request, false)
                .then()
                .statusCode(401);
    }

    @Test
    void createShortLinkInvalidUrlShouldReturn400() {
        var request = CreateShortLinkRequest.builder()
                .customCode("testlink")
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .originalUrl("hts:\\/example!com/")
                .build();
        sendCreateRequest(request, true)
                .then()
                .statusCode(400);
    }

    @Test
    void createShortLinkNoUrlShouldReturn400() {
        var request = CreateShortLinkRequest.builder()
                .customCode("testlink")
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
        sendCreateRequest(request, true)
                .then()
                .statusCode(400);
    }

    @Test
    void createSameShortLinkShouldReturn409() {
        var request = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        sendCreateRequest(request, true)
                .then()
                .statusCode(200);
        sendCreateRequest(request, true)
                .then()
                .statusCode(409);
    }

    @Test
    void createShortLinkWithoutCustomCodeShouldGenerateANewOne() {
        var request = createDefaultRequest(null, Instant.now().plus(1, ChronoUnit.DAYS));
        String shortCode = sendCreateRequest(request, true)
                .then()
                .statusCode(200)
                .extract()
                .path("shortCode");

        assertNotNull(shortCode);
        assertFalse(shortCode.isEmpty());
    }
    @Test
    void createShortLinkBlankCustomCodeShouldGenerateANewOne() {
        var request = createDefaultRequest("", Instant.now().plus(1, ChronoUnit.DAYS));
        String shortCode = sendCreateRequest(request, true)
                .then()
                .statusCode(200)
                .extract()
                .path("shortCode");

        assertNotNull(shortCode);
        assertFalse(shortCode.isEmpty());
    }

    @Test
    void editShortLinkAuthorizedShouldReturn200() {
        var createRequest = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        ShortLinkResponse originalResponse = sendCreateRequest(createRequest, true)
                .then()
                .statusCode(200)
                .extract()
                .as(ShortLinkResponse.class);
        EditShortLinkRequest editRequest =
                EditShortLinkRequest.builder()
                        .shortCode("testlink")
                        .originalUrl(originalResponse.getOriginalUrl() + "example/")
                        .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                        .enabled(false)
                        .build();
        ShortLinkResponse response = given()
                .contentType(ContentType.JSON)
                .body(editRequest)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/link/edit")
                .then()
                .statusCode(200)
                .extract()
                .as(ShortLinkResponse.class);
        assertEquals(originalResponse.getOriginalUrl() + "example/", response.getOriginalUrl());
        assertTrue(Instant.now().plus(6, ChronoUnit.DAYS).isBefore(response.getExpiresAt()));
        assertFalse(response.isEnabled());
        assertEquals(
                originalResponse.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                originalResponse.getEditedAt().truncatedTo(ChronoUnit.MILLIS)
        );
        assertEquals(
                originalResponse.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                response.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)
        );
        assertNotEquals(
                originalResponse.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                response.getEditedAt().truncatedTo(ChronoUnit.MILLIS)
        );
    }

    @Test
    void editShortLinkWithoutEditsShouldReturnSameLink() {
        var createRequest = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        ShortLinkResponse originalResponse = sendCreateRequest(createRequest, true)
                .then()
                .statusCode(200)
                .extract()
                .as(ShortLinkResponse.class);
        EditShortLinkRequest editRequest =
                EditShortLinkRequest.builder()
                        .shortCode("testlink")
                        .build();
        ShortLinkResponse response = given()
                .contentType(ContentType.JSON)
                .body(editRequest)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/link/edit")
                .then()
                .statusCode(200)
                .extract()
                .as(ShortLinkResponse.class);
        assertEquals(originalResponse.getEditedAt().truncatedTo(ChronoUnit.MILLIS), response.getEditedAt().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void editShortLinkWithSameEditsShouldReturnSameLink() {
        var createRequest = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        sendCreateRequest(createRequest, true);
        ShortLinkResponse originalResponse =
                 given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/link/details/testlink")
                .then()
                .statusCode(200)
                .extract()
                .as(ShortLinkResponse.class);

        EditShortLinkRequest editRequest =
                EditShortLinkRequest.builder()
                        .shortCode("testlink")
                        .originalUrl(originalResponse.getOriginalUrl())
                        .expiresAt(originalResponse.getExpiresAt())
                        .enabled(originalResponse.isEnabled())
                        .build();
        ShortLinkResponse response = given()
                .contentType(ContentType.JSON)
                .body(editRequest)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/link/edit")
                .then()
                .statusCode(200)
                .extract()
                .as(ShortLinkResponse.class);
        assertEquals(originalResponse.getEditedAt().truncatedTo(ChronoUnit.MILLIS), response.getEditedAt().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void editOthersShortLinkShouldReturn403() {
        var createRequest = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        String originalUrl = sendCreateRequest(createRequest, true)
                .then()
                .statusCode(200)
                .extract()
                .path("originalUrl");
        RegisterRequest registerRequest = new RegisterRequest("testuser", "Password12345");
        String otherToken = given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .extract()
                .path("token");
        EditShortLinkRequest editRequest =
                EditShortLinkRequest.builder()
                        .shortCode("testlink")
                        .originalUrl(originalUrl + "example/")
                        .build();
        given()
                .contentType(ContentType.JSON)
                .body(editRequest)
                .header("Authorization", "Bearer " + otherToken)
                .when()
                .post("/api/v1/link/edit")
                .then()
                .statusCode(403);
    }

    @Test
    void deleteExistentShortLinkShouldReturn204() {
        var request = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        sendCreateRequest(request, true)
                .then()
                .statusCode(200);
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/link/delete/testlink")
                .then()
                .statusCode(204);
    }

    @Test
    void deleteOthersShortLinkShouldReturn403() {
        var request = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        sendCreateRequest(request, true)
                .then()
                .statusCode(200);
        RegisterRequest registerRequest = new RegisterRequest("testuser", "Password12345");
        String otherToken = given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .extract()
                .path("token");
        given()
                .header("Authorization", "Bearer " + otherToken)
                .when()
                .delete("/api/v1/link/delete/testlink")
                .then()
                .statusCode(403);
    }

    @Test
    void deleteNonExistentShortLinkShouldReturn404() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/link/delete/testlink")
                .then()
                .statusCode(404);
    }

    @Test
    void getShortLinkShouldReturn200() {
        var request = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        sendCreateRequest(request, true).then().statusCode(200);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/link/details/testlink")
                .then()
                .statusCode(200);
    }

    @Test
    void getOthersShortLinkShouldReturn403() {
        var request = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        sendCreateRequest(request, true)
                .then()
                .statusCode(200);
        RegisterRequest registerRequest = new RegisterRequest("otheruser", "Password12345");
        String otherToken = given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .extract()
                .path("token");
        given()
                .header("Authorization", "Bearer " + otherToken)
                .when()
                .get("/api/v1/link/details/testlink")
                .then()
                .statusCode(403);
    }

    @Test
    void getAllLinksByUserShouldReturn200() {
        sendCreateRequest(createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS)), true).then().statusCode(200);
        sendCreateRequest(createDefaultRequest("testing", Instant.now().plus(1, ChronoUnit.DAYS)).toBuilder().enabled(false).build(), true).then().statusCode(200);

        List<ShortLinkResponse> responses = getListOfLinks("/api/v1/link/all");
        assertEquals(2, responses.size());
    }

    @Test
    void getAllActiveLinksByUserShouldReturn200() {
        sendCreateRequest(createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS)), true).then().statusCode(200);
        sendCreateRequest(createDefaultRequest("testing", Instant.now().plus(1, ChronoUnit.DAYS)).toBuilder().enabled(false).build(), true).then().statusCode(200);

        List<ShortLinkResponse> responses = getListOfLinks("/api/v1/link/active");
        assertEquals(1, responses.size());
        assertEquals("testlink", responses.getFirst().getShortCode());
    }

    @Test
    void shortLinkExpireTest() {
        sendCreateRequest(createDefaultRequest("testlink", Instant.now().plus(50, ChronoUnit.MILLIS)), true).then().statusCode(200);

        try {
            Thread.sleep(60);
        } catch (InterruptedException ignored) {}

        given()
                .redirects().follow(false)
                .when()
                .get("/api/v1/link/go/testlink")
                .then()
                .statusCode(410);
    }

    @Test
    void shortLinkDisableTest() {
        sendCreateRequest(createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS)).toBuilder().enabled(false).build(), true).then().statusCode(200);

        given()
                .redirects().follow(false)
                .when()
                .get("/api/v1/link/go/testlink")
                .then()
                .statusCode(410);
    }

    @Test
    void redirectToShortLinkShouldReturn302andIncrementVisits() {
        var request = createDefaultRequest("testlink", Instant.now().plus(1, ChronoUnit.DAYS));
        sendCreateRequest(request, true).then().statusCode(200);

        given()
                .redirects().follow(false)
                .when()
                .get("/api/v1/link/go/testlink")
                .then()
                .statusCode(302);

        int visits = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/link/details/testlink")
                .then()
                .statusCode(200)
                .extract()
                .path("visits");

        assertEquals(1, visits);
    }
}