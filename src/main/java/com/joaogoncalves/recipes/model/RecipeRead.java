package com.joaogoncalves.recipes.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Information of the recipe.")
public class RecipeRead {

    @ApiModelProperty(notes = "The name of the recipe")
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 50)
    private String name;

    @ApiModelProperty(notes = "The description of the recipe")
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 200)
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

    @PastOrPresent
    private Instant date;
}
