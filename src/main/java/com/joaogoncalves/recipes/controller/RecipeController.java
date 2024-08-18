package com.joaogoncalves.recipes.controller;

import com.joaogoncalves.recipes.model.RecipeCreate;
import com.joaogoncalves.recipes.model.RecipeRead;
import com.joaogoncalves.recipes.model.RecipeUpdate;
import com.joaogoncalves.recipes.service.RecipeService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/recipes")
@Validated
public class RecipeController {

    @Autowired
    private RecipeService recipeService;


    @PostMapping(
            path="/new",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RecipeRead> create(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestBody @Valid final RecipeCreate recipeCreate
    ) {
        return new ResponseEntity<>(
                recipeService.create(userDetails.getUsername(), recipeCreate),
                HttpStatus.CREATED
        );
    }

    @GetMapping(
            path="/{id}",
            produces = APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RecipeRead> read(@PathVariable @Min(1) final Long id) {
        return ResponseEntity.ok(recipeService.read(id));
    }

    @PutMapping(
            path = "/{id}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RecipeRead> update(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable @Min(1) final Long id,
            @Valid @RequestBody final RecipeUpdate recipeUpdate
    ) {
        return ResponseEntity.ok(
                recipeService.update(
                        userDetails.getUsername(),
                        id,
                        recipeUpdate
                )
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable @Min(1) final Long id
    ) {
        recipeService.delete(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories/{category}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> searchByCategory(@PathVariable @NotBlank final String category) {
        return ResponseEntity.ok(recipeService.searchByCategory(category));
    }

    @GetMapping("/names/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> searchByName(@PathVariable @NotBlank final String name) {
        return ResponseEntity.ok(recipeService.searchByName(name));
    }
}

