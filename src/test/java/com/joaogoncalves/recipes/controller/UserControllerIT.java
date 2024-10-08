package com.joaogoncalves.recipes.controller;

import com.joaogoncalves.recipes.model.UserCreate;
import com.joaogoncalves.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestContainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class UserControllerIT {

    @LocalServerPort
    private Integer port;

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    static Stream<Arguments> provideUserCreatesWithError() {
        return Stream.of(
                Arguments.of(
                        new UserCreate("test@test.com", "testtest"),
                        "User already exists! [e-mail: test@test.com]"
                ),
                Arguments.of(
                        new UserCreate("testtest.com", "testtest"),
                        "email: Invalid email format"
                ),
                Arguments.of(
                        new UserCreate("", "testtest"),
                        "email: Email cannot be blank"
                ),
                Arguments.of(
                        new UserCreate("test@test.com", "testtes"),
                        "password: size must be between 8 and 30"
                ),
                Arguments.of(
                        new UserCreate("test@test.com", ""),
                        "password: Password cannot be blank; password: size must be between 8 and 30"
                )
        );
    }

    @Test
    public void testUserCreate() {
        final UserCreate userCreate = new UserCreate("random@random.com", "random12");
        given()
                .contentType(ContentType.JSON)
                .body(userCreate)
                .when()
                .post("/api/register")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @ParameterizedTest
    @MethodSource("provideUserCreatesWithError")
    public void testUserCreateWithError(final UserCreate userCreate, final String expectedExceptionMessage) {
        given()
                .contentType(ContentType.JSON)
                .body(userCreate)
                .when()
                .post("/api/register")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(expectedExceptionMessage));
    }
}

