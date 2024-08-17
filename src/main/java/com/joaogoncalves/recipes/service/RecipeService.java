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
import java.util.stream.Collectors;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    private Recipe find(final Long id) {
        return recipeRepository
                .findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(
                        String.format("Recipe not found! [Id: %s]", id)
                ));
    }

    private boolean userIsAuthorOfRecipe(final String email,
                                         final Long recipeOwnerId,
                                         final Long recipeId) {
        final User user = (User) userService.loadUserByUsername(email);
        if (Objects.equals(user.getId(), recipeOwnerId)) {
            return true;
        } else {
            throw new UserNotAuthorOfRecipeException(
                    String.format("User %s is not the author of the recipe %s", user.getId(), recipeId)
            );
        }
    }

    public void create(final String email, final RecipeCreate recipeCreate) {
        final User author = (User) userService.loadUserByUsername(email);
        Recipe recipe = modelMapper.map(recipeCreate, Recipe.class);
        recipe.setAuthor(author);
        recipeRepository.save(recipe);
    }

    public RecipeRead read(final Long id) {
        final Recipe recipe = find(id);
        return modelMapper.map(recipe, RecipeRead.class);
    }

    public void update(final String email, final Long id, final RecipeUpdate recipeUpdate) {
        final Recipe recipe = find(id);
        if (userIsAuthorOfRecipe(email, recipe.getAuthor().getId(), recipe.getId())) {
            modelMapper.map(recipeUpdate, recipe);
            recipeRepository.save(recipe);
        }
    }

    public void delete(final String email, final Long id) {
        final Recipe recipe = find(id);
        if (userIsAuthorOfRecipe(email, recipe.getAuthor().getId(), recipe.getId())) {
            recipeRepository.deleteById(id);
        }
    }

    public List<RecipeRead> searchRecipes(final String category, final String name) {
        List<RecipeRead> recipes;
        if (category != null) {
            recipes = recipeRepository
                    .findByCategoryIgnoreCaseOrderByDateDesc(category)
                    .stream()
                    .map(value -> modelMapper.map(value, RecipeRead.class))
                    .collect(Collectors.toList());
        } else {
            recipes = recipeRepository.findByNameContainingIgnoreCaseOrderByDateDesc(name)
                    .stream()
                    .map(value -> modelMapper.map(value, RecipeRead.class))
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}

