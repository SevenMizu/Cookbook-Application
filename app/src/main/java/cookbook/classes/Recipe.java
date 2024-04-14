package cookbook.classes;

import java.util.List;

/**
 * Represents a Recipe entity.
 */
public class Recipe {
    private int recipeId;
    private String name;
    private String shortDescription;
    private String detailedDescription;
    private int servings;
    private List<Comment> comments;
    private List<Ingredient> ingredients;
    private List<Tag> tags;
    private List<User> favorites;

    /**
     * Constructor for Recipe class.
     */
    public Recipe(int recipeId, String name, String shortDescription, String detailedDescription, int servings) {
        this.recipeId = recipeId;
        this.name = name;
        this.shortDescription = shortDescription;
        this.detailedDescription = detailedDescription;
        this.servings = servings;
    }

    // Getters and setters for all attributes

    /**
     * Adds a comment to the recipe.
     * @param comment The comment to add.
     */
    public void addComment(Comment comment) {
        comments.add(comment);
    }

    /**
     * Adds an ingredient to the recipe.
     * @param ingredient The ingredient to add.
     */
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    /**
     * Adds a tag to the recipe.
     * @param tag The tag to add.
     */
    public void addTag(Tag tag) {
        tags.add(tag);
    }

    /**
     * Adds a user to the list of favorites for this recipe.
     * @param user The user to add to favorites.
     */
    public void addFavorite(User user) {
        favorites.add(user);
    }
}