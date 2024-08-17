package com.joaogoncalves.recipes.controller;

import com.joaogoncalves.recipes.model.RecipeCreate;
import com.joaogoncalves.recipes.model.RecipeRead;
import com.joaogoncalves.recipes.model.RecipeUpdate;
import com.joaogoncalves.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
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

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestContainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecipeControllerIT {

    @LocalServerPort
    private Integer port;
    private final static List<String> testEmails = List.of("test@test.com", "test2@test.com");
    private final static List<String> testPasswords = List.of("testtest", "test2test2");
    private final static String testEmailWrong = "test@testwrong.com";

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    static Stream<Arguments> provideRecipeCreatesWithError() {
        return Stream.of(
                Arguments.of(
                        new RecipeCreate(
                                "tomato soup",
                                "tomato soup",
                                List.of("tomato"),
                                List.of("make the soup"),
                                "soup"
                        ),
                        testEmailWrong,
                        testPasswords.get(0),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Bad credentials"
                ),
                Arguments.of(
                        new RecipeCreate("",
                                "tomato soup",
                                List.of("tomato"),
                                List.of("make the soup"),
                                "soup"
                        ),
                        testEmails.get(0),
                        testPasswords.get(0),
                        HttpStatus.BAD_REQUEST.value(),
                        "name: Recipe name cannot be blank"
                ),
                Arguments.of(
                        new RecipeCreate("tomato soup",
                                "",
                                List.of("tomato"),
                                List.of("make the soup"),
                                "soup"
                        ),
                        testEmails.get(0),
                        testPasswords.get(0),
                        HttpStatus.BAD_REQUEST.value(),
                        "description: Recipe description cannot be blank"
                ),
                Arguments.of(
                        new RecipeCreate("tomato soup",
                                "tomato soup",
                                List.of(),
                                List.of("make the soup"),
                                "soup"
                        ),
                        testEmails.get(0),
                        testPasswords.get(0),
                        HttpStatus.BAD_REQUEST.value(),
                        "ingredients: Recipe ingredients cannot be empty"
                ),
                Arguments.of(
                        new RecipeCreate("tomato soup",
                                "tomato soup",
                                List.of("tomato"),
                                List.of(),
                                "soup"
                        ),
                        testEmails.get(0),
                        testPasswords.get(0),
                        HttpStatus.BAD_REQUEST.value(),
                        "directions: Recipe directions cannot be empty"
                ),
                Arguments.of(
                        new RecipeCreate(
                                "tomato soup",
                                "tomato soup",
                                List.of("tomato"),
                                List.of("make the soup"),
                                ""
                        ),
                        testEmails.get(0),
                        testPasswords.get(0),
                        HttpStatus.BAD_REQUEST.value(),
                        "category: Recipe category cannot be blank"
                )
        );
    }

    static Stream<Arguments> provideRecipeReadsWithError() {
        return Stream.of(
                Arguments.of(
                        1L,
                        testEmailWrong,
                        testPasswords.get(0),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Bad credentials"
                ),
                Arguments.of(
                        1000L,
                        testEmails.get(0),
                        testPasswords.get(0),
                        HttpStatus.NOT_FOUND.value(),
                        "Recipe not found! [Id: 1000]"
                )
        );
    }

    static Stream<Arguments> provideRecipeUpdatesWithError() {
        return Stream.of(
                Arguments.of(
                        1L,
                        testEmailWrong,
                        testPasswords.get(0),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Bad credentials"
                ),
                Arguments.of(
                        1000L,
                        testEmails.get(0),
                        testPasswords.get(0),
                        HttpStatus.NOT_FOUND.value(),
                        "Recipe not found! [Id: 1000]"
                ),
                Arguments.of(
                        1L,
                        testEmails.get(1),
                        testPasswords.get(1),
                        HttpStatus.FORBIDDEN.value(),
                        "User test2@test.com is not the author of the recipe [Id: 1]"
                )
        );
    }

    static Stream<Arguments> provideRecipeDeletesWithError() {
        return Stream.of(
                Arguments.of(
                        1L,
                        testEmailWrong,
                        testPasswords.get(0),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Bad credentials"
                ),
                Arguments.of(
                        1000L,
                        testEmails.get(0),
                        testPasswords.get(0),
                        HttpStatus.NOT_FOUND.value(),
                        "Recipe not found! [Id: 1000]"
                ),
                Arguments.of(
                        1L,
                        testEmails.get(1),
                        testPasswords.get(1),
                        HttpStatus.FORBIDDEN.value(),
                        "User test2@test.com is not the author of the recipe [Id: 1]"
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
        final RecipeRead expectedRecipe = new RecipeRead(
                "tomato soup",
                "tomato soup",
                List.of("tomato"),
                List.of("make the soup"),
                "soup",
                null
        );
        final RecipeRead actualRecipe = given()
                .auth()
                .basic(testEmails.get(0), testPasswords.get(0))
                .contentType(ContentType.JSON)
                .body(recipeCreate)
                .when()
                .post("/api/recipe/new")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(RecipeRead.class);
        Assertions.assertEquals(expectedRecipe, actualRecipe);
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

    @Test
    @Order(3)
    public void testRecipeRead() {
        final RecipeRead expectedRecipe = new RecipeRead(
                "onion soup",
                "onion soup",
                List.of("onion"),
                List.of("make the soup"),
                "soup",
                null
        );
        final RecipeRead actualRecipe = given()
                .auth()
                .basic(testEmails.get(0), testPasswords.get(0))
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .when()
                .get("/api/recipe/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RecipeRead.class);
        Assertions.assertEquals(expectedRecipe, actualRecipe);
    }

    @ParameterizedTest
    @Order(4)
    @MethodSource("provideRecipeReadsWithError")
    public void testRecipeReadWithError(final Long id,
                                        final String userEmail,
                                        final String userPassword,
                                        final int expectedStatusCode,
                                        final String expectedExceptionMessage) {
        given()
                .auth()
                .basic(userEmail, userPassword)
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .when()
                .get("/api/recipe/{id}")
                .then()
                .statusCode(expectedStatusCode)
                .body("message",equalTo(expectedExceptionMessage));
    }

    @Test
    @Order(5)
    public void testRecipeUpdate() {
        final RecipeUpdate recipeUpdate = new RecipeUpdate(
                "onion soup",
                "onion soup with anchovies",
                List.of("onion", "anchovies"),
                List.of("make the soup", "add the anchovies"),
                "soup"
        );
        final RecipeRead expectedRecipe = new RecipeRead(
                "onion soup",
                "onion soup with anchovies",
                List.of("onion", "anchovies"),
                List.of("make the soup", "add the anchovies"),
                "soup",
                null
        );
        final RecipeRead actualRecipe = given()
                .auth()
                .basic(testEmails.get(0), testPasswords.get(0))
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body(recipeUpdate)
                .when()
                .put("/api/recipe/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RecipeRead.class);
        Assertions.assertEquals(expectedRecipe, actualRecipe);
    }

    @ParameterizedTest
    @Order(6)
    @MethodSource("provideRecipeUpdatesWithError")
    public void testRecipeUpdateWithError(final Long id,
                                        final String userEmail,
                                        final String userPassword,
                                        final int expectedStatusCode,
                                        final String expectedExceptionMessage) {
        final RecipeUpdate recipeUpdate = new RecipeUpdate(
                "onion soup",
                "onion soup with anchovies",
                List.of("onion", "anchovies"),
                List.of("make the soup", "add the anchovies"),
                "soup"
        );
        given()
                .auth()
                .basic(userEmail, userPassword)
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .body(recipeUpdate)
                .when()
                .put("/api/recipe/{id}")
                .then()
                .statusCode(expectedStatusCode)
                .body("message",equalTo(expectedExceptionMessage));
    }

    @Test
    @Order(8)
    public void testRecipeDelete() {
        given()
                .auth()
                .basic(testEmails.get(0), testPasswords.get(0))
                .pathParam("id", 1L)
                .when()
                .delete("/api/recipe/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @ParameterizedTest
    @Order(7)
    @MethodSource("provideRecipeDeletesWithError")
    public void testRecipeDeleteWithError(final Long id,
                                          final String userEmail,
                                          final String userPassword,
                                          final int expectedStatusCode,
                                          final String expectedExceptionMessage) {
        given()
                .auth()
                .basic(userEmail, userPassword)
                .pathParam("id", id)
                .when()
                .delete("/api/recipe/{id}")
                .then()
                .statusCode(expectedStatusCode)
                .body("message",equalTo(expectedExceptionMessage));
    }

    // TODO - search tests
}
