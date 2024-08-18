package com.joaogoncalves.recipes.service;


import com.joaogoncalves.recipes.entity.Recipe;
import com.joaogoncalves.recipes.entity.User;
import com.joaogoncalves.recipes.exception.RecipeNotFoundException;
import com.joaogoncalves.recipes.exception.UserNotAuthorOfRecipeException;
import com.joaogoncalves.recipes.model.RecipeCreate;
import com.joaogoncalves.recipes.model.RecipeRead;
import com.joaogoncalves.recipes.model.RecipeUpdate;
import com.joaogoncalves.recipes.repository.RecipeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    private Recipe find(final UUID id) {
        return recipeRepository
                .findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(
                        String.format("Recipe not found! [Id: %s]", id)
                ));
    }

    private boolean userIsAuthorOfRecipe(final String email,
                                         final UUID recipeOwnerId,
                                         final UUID recipeId) {
        final User user = (User) userService.loadUserByUsername(email);
        if (Objects.equals(user.getId(), recipeOwnerId)) {
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
        }
    }

    public List<RecipeRead> searchByCategory(final String category) {
        return recipeRepository
                .findByCategoryIgnoreCaseOrderByDateDesc(category)
                .stream()
                .map(recipe -> modelMapper.map(recipe, RecipeRead.class))
                .collect(Collectors.toList());
    }

    public List<RecipeRead> searchByName(final String name) {
        return recipeRepository
                .findByNameContainingIgnoreCaseOrderByDateDesc(name)
                .stream()
                .map(recipe -> modelMapper.map(recipe, RecipeRead.class))
                .collect(Collectors.toList());
    }
}

