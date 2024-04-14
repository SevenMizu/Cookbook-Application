package cookbook.classes;
/**
 * Represents an Ingredient entity for a Recipe.
 */
public class Ingredient extends RecipeItem {
    /**
     * Constructs a new Ingredient object.
     * @param name The name of the ingredient.
     */
    public Ingredient(int ingredientId, String name) {
        super(ingredientId, name);
    }
}