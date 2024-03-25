package com.joaogoncalves.recipes.repository;

import com.joaogoncalves.recipes.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByCategoryIgnoreCaseOrderByDateDesc(final String category);
    List<Recipe> findByNameContainingIgnoreCaseOrderByDateDesc(final String name);
}
