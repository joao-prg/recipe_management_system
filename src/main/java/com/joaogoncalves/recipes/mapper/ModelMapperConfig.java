package com.joaogoncalves.recipes.mapper;

import com.joaogoncalves.recipes.model.RecipeUpdate;
import com.joaogoncalves.recipes.entity.Recipe;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Change behaviour - collection items are not removed if source is smaller than destination
        modelMapper
                .typeMap(RecipeUpdate.class, Recipe.class)
                .addMappings(
                        mapper -> mapper
                                .with(req -> new ArrayList<String>())
                                .map(RecipeUpdate::getDirections, Recipe::setDirections)
                );
        return modelMapper;
    }
}
