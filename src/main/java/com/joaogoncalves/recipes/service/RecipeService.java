package com.joaogoncalves.recipes.service;


import com.joaogoncalves.recipes.entity.Recipe;
import com.joaogoncalves.recipes.entity.User;
import com.joaogoncalves.recipes.exception.RecipeNotFoundException;
import com.joaogoncalves.recipes.exception.UserNotAuthorOfRecipeException;
import com.joaogoncalves.recipes.model.RecipeCreate;
import com.joaogoncalves.recipes.model.RecipeRead;
import com.joaogoncalves.recipes.model.RecipeUpdate;
import com.joaogoncalves.recipes.repository.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    private Recipe find(final UUID id) {
        final Recipe recipe = recipeRepository
                .findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(
                        String.format("Recipe not found! [Id: %s]", id)
                ));
        log.debug(String.format("Recipe retrieved successfully [ID: %s]", id));
        return recipe;
    }

    private boolean userIsAuthorOfRecipe(final String email,
                                         final UUID recipeOwnerId,
                                         final UUID recipeId) {
        final User user = (User) userService.loadUserByUsername(email);
        if (Objects.equals(user.getId(), recipeOwnerId)) {
            log.debug(
                    String.format(
                            "User [e-mail: %s] is the author of recipe [ID: %s]",
                            email,
                            recipeId
                    )
            );
            return true;
        } else {
            throw new UserNotAuthorOfRecipeException(
                    String.format(
                            "User %s is not the author of the recipe [Id: %s]",
                            email,
                            recipeId
                    )
            );
        }
    }

    public RecipeRead create(final String email, final RecipeCreate recipeCreate) {
        final User author = (User) userService.loadUserByUsername(email);
        Recipe recipeToCreate = modelMapper.map(recipeCreate, Recipe.class);
        recipeToCreate.setAuthor(author);
        final Recipe savedRecipe = recipeRepository.save(recipeToCreate);
        log.debug(String.format("Recipe saved successfully [name: %s]", recipeCreate.getName()));
        return modelMapper.map(savedRecipe, RecipeRead.class);
    }

    public RecipeRead read(final UUID id) {
        final Recipe recipe = find(id);
        return modelMapper.map(recipe, RecipeRead.class);
    }

    public RecipeRead update(
            final String email,
            final UUID id,
            final RecipeUpdate recipeUpdate
    ) {
        final Recipe recipe = find(id);
        if (userIsAuthorOfRecipe(email,
                recipe.getAuthor().getId(),
                recipe.getId())
        ) {
            modelMapper.map(recipeUpdate, recipe);
            final Recipe updatedRecipe = recipeRepository.save(recipe);
            log.debug(String.format("Recipe saved successfully [ID: %s]", id));
            return modelMapper.map(updatedRecipe, RecipeRead.class);
        }
        return null;
    }

    public void delete(final String email, final UUID id) {
        final Recipe recipe = find(id);
        if (userIsAuthorOfRecipe(
                email,
                recipe.getAuthor().getId(),
                recipe.getId())
        ) {
            recipeRepository.deleteById(id);
            log.debug(String.format("Recipe deleted successfully [ID: %s]", id));
        }
    }

    public List<RecipeRead> searchByCategory(final String category) {
        final List<RecipeRead> recipes =  recipeRepository
                .findByCategoryIgnoreCaseOrderByDateDesc(category)
                .stream()
                .map(recipe -> modelMapper.map(recipe, RecipeRead.class))
                .collect(Collectors.toList());
        log.debug(String.format("Found %d recipes for category %s", recipes.size(), category));
        return recipes;
    }

    public List<RecipeRead> searchByName(final String name) {
        final List<RecipeRead> recipes = recipeRepository
                .findByNameContainingIgnoreCaseOrderByDateDesc(name)
                .stream()
                .map(recipe -> modelMapper.map(recipe, RecipeRead.class))
                .collect(Collectors.toList());
        log.debug(String.format("Found %d recipes for name %s", recipes.size(), name));
        return recipes;
    }
}

