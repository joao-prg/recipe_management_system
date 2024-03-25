package com.joaogoncalves.recipes.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotEmpty(message = "Ingredients cannot be empty")
    @ElementCollection
    private List<String> ingredients;

    @NotEmpty(message = "Directions cannot be empty")
    @ElementCollection
    private List<String> directions;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @UpdateTimestamp
    private Instant date;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
}
