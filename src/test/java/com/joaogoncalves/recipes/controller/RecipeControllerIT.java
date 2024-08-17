package com.joaogoncalves.recipes.controller;

import com.joaogoncalves.recipes.model.RecipeCreate;
import com.joaogoncalves.recipes.model.RecipeUpdate;
import com.joaogoncalves.recipes.model.UserCreate;
import com.joaogoncalves.recipes.service.RecipeService;
import com.joaogoncalves.recipes.service.UserService;
import com.joaogoncalves.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
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
import static org.hamcrest.Matchers.hasItems;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestContainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecipeControllerIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;

    private final static String testEmail = "test@test.com";

    private final static String testPassword = "testtest";

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        userService.create(new UserCreate(testEmail, testPassword));
        recipeService.create(
                testEmail,
                new RecipeCreate(
                    "onion soup",
                    "onion soup",
                    List.of("onion"),
                    List.of("make the soup"),
                    "soup"
                )
        );
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
                        "test@testwrong.com",
                        "testtest",
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
                        testEmail,
                        testPassword,
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
                        testEmail,
                        testPassword,
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
                        testEmail,
                        testPassword,
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
                        testEmail,
                        testPassword,
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
                        testEmail,
                        testPassword,
                        HttpStatus.BAD_REQUEST.value(),
                        "category: Recipe category cannot be blank"
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

    @Test
    @Order(3)
    public void testRecipeRead() {
        final Response response = given()
                .auth()
                .basic(testEmail, testPassword)
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .when()
                .get("/api/recipe/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();
        response.then().body("name", equalTo("onion soup"));
        response.then().body("description", equalTo("onion soup"));
        response.then().body("ingredients", hasItems("onion"));
        response.then().body("directions", hasItems("make the soup"));
        response.then().body("category", equalTo("soup"));
    }

    @Test
    @Order(4)
    public void testRecipeReadNotFound() {
        given()
                .auth()
                .basic(testEmail, testPassword)
                .contentType(ContentType.JSON)
                .pathParam("id", 1000)
                .when()
                .get("/api/recipe/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message",equalTo("Recipe not found! [Id: 1000]"));
    }

    // add test read with wrong credentials

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
        given()
                .auth()
                .basic(testEmail, testPassword)
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body(recipeUpdate)
                .when()
                .put("/api/recipe/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
