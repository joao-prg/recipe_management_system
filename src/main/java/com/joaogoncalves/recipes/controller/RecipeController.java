package com.joaogoncalves.recipes.controller;

import com.joaogoncalves.recipes.model.RecipeCreate;
import com.joaogoncalves.recipes.model.RecipeRead;
import com.joaogoncalves.recipes.model.RecipeUpdate;
import com.joaogoncalves.recipes.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/recipes")
@Validated
@Slf4j
public class RecipeController {

    @Autowired
    private RecipeService recipeService;


    @PostMapping(path="/new", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RecipeRead> create(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestBody @Valid final RecipeCreate recipeCreate) {
        log.info(
                String.format(
                        "User [e-mail: %s] is creating recipe [name: %s]",
                        userDetails.getUsername(),
                        recipeCreate.getName()
                )
        );
        return new ResponseEntity<>(recipeService.create(userDetails.getUsername(), recipeCreate), HttpStatus.CREATED);
    }

    @GetMapping(path="/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RecipeRead> read(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final UUID id) {
        log.info(
                String.format(
                        "User [e-mail: %s] is reading recipe [ID: %s]",
                        userDetails.getUsername(),
                        id
                )
        );
        return ResponseEntity.ok(recipeService.read(id));
    }

    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RecipeRead> update(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final UUID id,
            @Valid @RequestBody final RecipeUpdate recipeUpdate) {
        log.info(
                String.format(
                        "User [e-mail: %s] is updating recipe [ID: %s]",
                        userDetails.getUsername(),
                        id
                )
        );
        return ResponseEntity.ok(recipeService.update(userDetails.getUsername(), id, recipeUpdate));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final UUID id) {
        log.info(
                String.format(
                        "User [e-mail: %s] is deleting recipe [ID: %s]",
                        userDetails.getUsername(),
                        id
                )
        );
        recipeService.delete(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories/{category}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> searchByCategory(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable @NotBlank final String category) {
        log.info(
                String.format(
                        "User [e-mail: %s] is searching recipes [category: %s]",
                        userDetails.getUsername(),
                        category
                )
        );
        return ResponseEntity.ok(recipeService.searchByCategory(category));
    }

    @GetMapping("/names/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> searchByName(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable @NotBlank final String name) {
        log.info(
                String.format(
                        "User [e-mail: %s] is searching recipes [name: %s]",
                        userDetails.getUsername(),
                        name
                )
        );
        return ResponseEntity.ok(recipeService.searchByName(name));
    }
}

