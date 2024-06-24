package com.joaogoncalves.recipes.controller;

import com.joaogoncalves.recipes.model.RecipeCreate;
import com.joaogoncalves.recipes.model.UserCreate;
import com.joaogoncalves.recipes.repository.UserRepository;
import com.joaogoncalves.recipes.service.UserService;
import com.joaogoncalves.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestContainers
public class RecipeControllerIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        userService.create(new UserCreate("test@test.com", "testtest"));
    }

    @Test
    public void testRecipeCreate() {
        final RecipeCreate recipeCreate = new RecipeCreate();
        recipeCreate.setName("tomato soup");
        recipeCreate.setDescription("tomato soup");
        recipeCreate.setIngredients(List.of("tomato"));
        recipeCreate.setDirections(List.of("make the soup"));
        recipeCreate.setCategory("soup");
        given()
                .auth()
                .basic("test@test.com", "testtest")
                .contentType(ContentType.JSON)
                .body(recipeCreate)
                .when()
                .post("/api/recipe/new")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
