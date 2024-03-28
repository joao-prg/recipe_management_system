package com.joaogoncalves.recipes.controller;

import com.joaogoncalves.recipes.model.UserCreate;
import com.joaogoncalves.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestContainers
public class UserControllerTests {

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    static Stream<Arguments> provideUserCreates() {
        return Stream.of(
                Arguments.of(
                        new UserCreate("test@test.com", "testtest"),
                        200,
                        null
                ),
                Arguments.of(
                        new UserCreate("test@test.com", "testtest"),
                        400,
                        "User already exists! [e-mail: test@test.com]"
                ),
                Arguments.of(
                        new UserCreate("testtest.com", "testtest"),
                        400,
                        "email: Invalid email format"
                ),
                Arguments.of(
                        new UserCreate("", "testtest"),
                        400,
                        "email: Email cannot be blank"
                ),
                Arguments.of(
                        new UserCreate("test@test.com", "testtes"),
                        400,
                        "password: size must be between 8 and 30"
                ),
                Arguments.of(
                        new UserCreate("test@test.com", ""),
                        400,
                        "password: Password cannot be blank; password: size must be between 8 and 30"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideUserCreates")
    public void testUserCreateWithError(final UserCreate userCreate,
                                        final int expectedStatusCode,
                                        final String expectedExceptionMessage) {
        Response response =given()
                .contentType(ContentType.JSON)
                .body(userCreate)
                .when()
                .post("/api/register");

        response.then().statusCode(expectedStatusCode);
        if (expectedExceptionMessage != null) {
            response.then().body("message", equalTo(expectedExceptionMessage));
        }
    }
}
