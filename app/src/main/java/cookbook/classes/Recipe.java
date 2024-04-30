package cookbook.classes;

import java.util.ArrayList;
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
    private int recipeCreatorId;
    private List<Comment> comments;
    private List<Ingredient> ingredients;
    private List<Tag> tags;
    private List<User> favorites;

    /**
     * Constructor for Recipe class.*/
    
    public Recipe(int recipeId, String name, String shortDescription, String detailedDescription, int servings, int recipeCreatorId, String ingredientString, String tagString) {
        this.recipeId = recipeId;
        this.name = name;
        this.shortDescription = shortDescription;
        this.detailedDescription = detailedDescription;
        this.servings = servings;
        this.recipeCreatorId = recipeCreatorId;
        this.comments = new ArrayList<>();
        ingredients = new ArrayList<>();
        tags = new ArrayList<>();
        parseList(ingredientString, false);
        parseList(tagString, true);
        this.favorites = new ArrayList<>();
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

    /**
     * Parses the string containing ingredient or tag names and creates a list of Ingredient or Tag objects accordingly.
     * @param listString The string containing ingredient or tag names.
     * @param isTags Boolean flag indicating whether to parse as tags or ingredients.
     * @return The list of Ingredient or Tag objects.
     */
    private void parseList(String listString, boolean isTags) {
        if (listString == (null)) {
            listString = "";
        }
        String[] names = listString.split(", ");
        for (String name : names) {
            if (name.trim().length() > 0) {
                if (isTags) {
                    tags.add(new Tag(name));
                } else {
                    ingredients.add(new Ingredient(name));
                }
            }
        }
    }

    public int getRecipeId() {
        return recipeId;
    }

    public int getRecipeCreatorId() {
        return recipeCreatorId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<User> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<User> favorites) {
        this.favorites = favorites;
    }

        /**
     * Converts a list of RecipeItem objects to a comma-separated string.
     * @param recipeItems The list of RecipeItem objects.
     * @return Comma-separated string of items in the list.
     */
    public static String asString(List<? extends RecipeItem> recipeItems) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < recipeItems.size(); i++) {
            RecipeItem item = recipeItems.get(i);
            stringBuilder.append(item.getName());
            if (i < recipeItems.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
}
