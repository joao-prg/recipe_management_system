package com.joaogoncalves.recipes.controller;

import com.joaogoncalves.recipes.model.UserCreate;
import com.joaogoncalves.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestContainers
public class UserControllerTests {

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    public void testUserCreateOk() {
        final UserCreate userCreate = new UserCreate("test@test.com", "testtest");
        given()
                .contentType(ContentType.JSON)
                .body(userCreate)
                .when()
                .post("/api/register")
                .then()
                .statusCode(200);
    }
}
