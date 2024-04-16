package cookbook.classes;

/**
 * Represents a common entity for Ingredients and Tags in a Recipe.
 */
public class RecipeItem {
    private String name; // The name of the item

    /**
     * Constructs a new RecipeItem object.
     * @param name The name of the item.
     */
    public RecipeItem(String name) {
        this.name = name;

    }

    /**
     * Retrieves the name of the item.
     * @return The name of the item.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the item.
     * @param name The name of the item to set.
     */
    public void setName(String name) {
        this.name = name;
    }
}