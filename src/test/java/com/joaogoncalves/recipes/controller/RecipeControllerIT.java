package com.joaogoncalves.recipes.controller;

import com.joaogoncalves.recipes.model.RecipeCreate;
import com.joaogoncalves.recipes.model.RecipeRead;
import com.joaogoncalves.recipes.model.RecipeUpdate;
import com.joaogoncalves.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestContainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
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
                        "4b437406-abf0-459a-a22f-5c65f1cf102a",
                        testEmailWrong,
                        testPasswords.get(0),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Bad credentials"
                ),
                Arguments.of(
                        "dec7fcec-5b86-43ae-902f-83180dd37cf1",
                        testEmails.get(0),
                        testPasswords.get(0),
                        HttpStatus.NOT_FOUND.value(),
                        "Recipe not found! [Id: dec7fcec-5b86-43ae-902f-83180dd37cf1]"
                )
        );
    }

    static Stream<Arguments> provideRecipeUpdatesWithError() {
        return Stream.of(
                Arguments.of(
                        "4b437406-abf0-459a-a22f-5c65f1cf102a",
                        testEmailWrong,
                        testPasswords.get(0),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Bad credentials"
                ),
                Arguments.of(
                        "dec7fcec-5b86-43ae-902f-83180dd37cf1",
                        testEmails.get(0),
                        testPasswords.get(0),
                        HttpStatus.NOT_FOUND.value(),
                        "Recipe not found! [Id: dec7fcec-5b86-43ae-902f-83180dd37cf1]"
                ),
                Arguments.of(
                        "4b437406-abf0-459a-a22f-5c65f1cf102a",
                        testEmails.get(1),
                        testPasswords.get(1),
                        HttpStatus.FORBIDDEN.value(),
                        "User test2@test.com is not the author of the recipe [Id: 4b437406-abf0-459a-a22f-5c65f1cf102a]"
                )
        );
    }

    static Stream<Arguments> provideRecipeDeletesWithError() {
        return Stream.of(
                Arguments.of(
                        "4b437406-abf0-459a-a22f-5c65f1cf102a",
                        testEmailWrong,
                        testPasswords.get(0),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Bad credentials"
                ),
                Arguments.of(
                        "dec7fcec-5b86-43ae-902f-83180dd37cf1",
                        testEmails.get(0),
                        testPasswords.get(0),
                        HttpStatus.NOT_FOUND.value(),
                        "Recipe not found! [Id: dec7fcec-5b86-43ae-902f-83180dd37cf1]"
                ),
                Arguments.of(
                        "4b437406-abf0-459a-a22f-5c65f1cf102a",
                        testEmails.get(1),
                        testPasswords.get(1),
                        HttpStatus.FORBIDDEN.value(),
                        "User test2@test.com is not the author of the recipe [Id: 4b437406-abf0-459a-a22f-5c65f1cf102a]"
                )
        );
    }

    static Stream<Arguments> provideRecipeSearchesByCategory() {
        return Stream.of(
                Arguments.of(
                        "soup",
                        List.of(
                            new RecipeRead(
                                    "onion soup",
                                    "onion soup with anchovies",
                                    List.of("onion", "anchovies"),
                                    List.of("make the soup", "add the anchovies"),
                                    "soup",
                                    null
                            ),
                            new RecipeRead(
                                    "tomato soup",
                                    "tomato soup",
                                    List.of("tomato"),
                                    List.of("make the soup"),
                                    "soup",
                                    null
                            )
                        )
                ),
                Arguments.of(
                        "beef",
                        List.of()
                )
        );
    }

    static Stream<Arguments> provideRecipeSearchesByName() {
        return Stream.of(
                Arguments.of(
                        "onion",
                        List.of(
                                new RecipeRead(
                                        "onion soup",
                                        "onion soup with anchovies",
                                        List.of("onion", "anchovies"),
                                        List.of("make the soup", "add the anchovies"),
                                        "soup",
                                        null
                                )
                        )
                ),
                Arguments.of(
                        "beef",
                        List.of()
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
                .post("/api/recipes/new")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(RecipeRead.class);
        assertThat(actualRecipe).isEqualToIgnoringGivenFields(expectedRecipe, "date");
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
                .post("/api/recipes/new")
                .then()
                .statusCode(expectedStatusCode)
                .body("message", equalTo(expectedExceptionMessage));
    }

    @Test
    @Order(3)
    public void testRecipeRead() {
        final RecipeRead expectedRecipe = new RecipeRead(
                "apple soup",
                "onion soup",
                List.of("onion"),
                List.of("make the soup"),
                "soup",
                Instant.parse("2024-08-17T22:06:43.237200Z")
        );
        final RecipeRead actualRecipe = given()
                .auth()
                .basic(testEmails.get(0), testPasswords.get(0))
                .contentType(ContentType.JSON)
                .pathParam("id", "4b437406-abf0-459a-a22f-5c65f1cf102a")
                .when()
                .get("/api/recipes/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RecipeRead.class);
        assertThat(actualRecipe).isEqualTo(expectedRecipe);
    }

    @ParameterizedTest
    @Order(4)
    @MethodSource("provideRecipeReadsWithError")
    public void testRecipeReadWithError(final UUID id,
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
                .get("/api/recipes/{id}")
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
                .pathParam("id", "4b437406-abf0-459a-a22f-5c65f1cf102a")
                .body(recipeUpdate)
                .when()
                .put("/api/recipes/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RecipeRead.class);
        assertThat(actualRecipe).isEqualToIgnoringGivenFields(expectedRecipe, "date");
    }

    @ParameterizedTest
    @Order(6)
    @MethodSource("provideRecipeUpdatesWithError")
    public void testRecipeUpdateWithError(final UUID id,
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
                .put("/api/recipes/{id}")
                .then()
                .statusCode(expectedStatusCode)
                .body("message",equalTo(expectedExceptionMessage));
    }

    @Test
    @Order(12)
    public void testRecipeDelete() {
        given()
                .auth()
                .basic(testEmails.get(0), testPasswords.get(0))
                .pathParam("id", "4b437406-abf0-459a-a22f-5c65f1cf102a")
                .when()
                .delete("/api/recipes/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @ParameterizedTest
    @Order(11)
    @MethodSource("provideRecipeDeletesWithError")
    public void testRecipeDeleteWithError(final UUID id,
                                          final String userEmail,
                                          final String userPassword,
                                          final int expectedStatusCode,
                                          final String expectedExceptionMessage) {
        given()
                .auth()
                .basic(userEmail, userPassword)
                .pathParam("id", id)
                .when()
                .delete("/api/recipes/{id}")
                .then()
                .statusCode(expectedStatusCode)
                .body("message",equalTo(expectedExceptionMessage));
    }

    @ParameterizedTest
    @Order(7)
    @MethodSource("provideRecipeSearchesByCategory")
    public void testRecipeSearchByCategory(final String category,
                                           final List<RecipeRead> expectedRecipes) {
        final List<RecipeRead> actualRecipes = given()
                .auth()
                .basic(testEmails.get(0), testPasswords.get(0))
                .contentType(ContentType.JSON)
                .pathParam("category", category)
                .when()
                .get("/api/recipes/categories/{category}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<List<RecipeRead>>() {});
        assertThat(actualRecipes)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("date")
                .isEqualTo(expectedRecipes);
    }

    @Test
    @Order(8)
    public void testRecipeSearchByCategoryWithError() {
        given()
                .auth()
                .basic(testEmailWrong, testPasswords.get(0))
                .contentType(ContentType.JSON)
                .pathParam("category", "soup")
                .when()
                .get("/api/recipes/categories/{category}")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message",equalTo("Bad credentials"));
    }

    @ParameterizedTest
    @Order(9)
    @MethodSource("provideRecipeSearchesByName")
    public void testRecipeSearchByName(final String name,
                                       final List<RecipeRead> expectedRecipes) {
        final List<RecipeRead> actualRecipes = given()
                .auth()
                .basic(testEmails.get(0), testPasswords.get(0))
                .contentType(ContentType.JSON)
                .pathParam("name", name)
                .when()
                .get("/api/recipes/names/{name}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<List<RecipeRead>>() {});
        assertThat(actualRecipes)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("date")
                .isEqualTo(expectedRecipes);
    }

    @Test
    @Order(10)
    public void testRecipeSearchByNameWithError() {
        given()
                .auth()
                .basic(testEmailWrong, testPasswords.get(0))
                .contentType(ContentType.JSON)
                .pathParam("name", "onion")
                .when()
                .get("/api/recipes/names/{name}")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message",equalTo("Bad credentials"));
    }
}
