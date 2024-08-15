package com.joaogoncalves.recipes.controller;

import com.joaogoncalves.recipes.model.RecipeCreate;
import com.joaogoncalves.recipes.model.UserCreate;
import com.joaogoncalves.recipes.service.UserService;
import com.joaogoncalves.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestContainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecipeControllerIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    private UserService userService;

    private final String testEmail = "test@test.com";

    private final String testPassword = "testtest";

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        userService.create(new UserCreate(testEmail, testPassword));
    }

    static Stream<Arguments> provideRecipeCreatesWithError() {
        final RecipeCreate recipeCreate = new RecipeCreate(
                "tomato soup",
                "tomato soup",
                List.of("tomato"),
                List.of("make the soup"),
                "soup"
        );
        return Stream.of(
                Arguments.of(
                        recipeCreate,
                        "test@testwrong.com",
                        "testtest",
                        HttpStatus.UNAUTHORIZED.value(),
                        "Bad credentials"
                )
        );
    }

    @Test
    @Order(1)
    public void testRecipeCreate() {
        final RecipeCreate recipeCreate = new RecipeCreate(
                "tomato soup",
                "tomato soup",
                List.of("tomato"),
                List.of("make the soup"),
                "soup"
        );
        given()
                .auth()
                .basic(testEmail, testPassword)
                .contentType(ContentType.JSON)
                .body(recipeCreate)
                .when()
                .post("/api/recipe/new")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @ParameterizedTest
    @Order(2)
    @MethodSource("provideRecipeCreatesWithError")
    public void testRecipeCreateWithError(final RecipeCreate recipeCreate,
                                          final String userEmail,
                                          final String userPassword,
                                          final int expectedStatusCode,
                                          final String expectedExceptionMessage) {
        given()
                .auth()
                .basic(userEmail, userPassword)
                .contentType(ContentType.JSON)
                .body(recipeCreate)
                .when()
                .post("/api/recipe/new")
                .then()
                .statusCode(expectedStatusCode)
                .body("message", equalTo(expectedExceptionMessage));
    }
}
