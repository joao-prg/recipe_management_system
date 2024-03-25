package com.joaogoncalves.recipes.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Information of the recipe to be created.")
public class RecipeCreate {

    @ApiModelProperty(notes = "The name of the recipe")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @ApiModelProperty(notes = "The description of the recipe")
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @ApiModelProperty(notes = "The ingredients of the recipe")
    @NotEmpty(message = "Ingredients cannot be empty")
    private List<String> ingredients;

    @ApiModelProperty(notes = "The directions of the recipe")
    @NotEmpty(message = "Directions cannot be empty")
    private List<String> directions;

    @ApiModelProperty(notes = "The category of the recipe")
    @NotBlank(message = "Category cannot be blank")
    private String category;
}
