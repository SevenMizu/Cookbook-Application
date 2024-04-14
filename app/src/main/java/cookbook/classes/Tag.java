package cookbook.classes;

/**
 * Represents a Tag entity for a Recipe.
 */
public class Tag extends RecipeItem {
    /**
     * Constructs a new Tag object.
     * @param name The name of the tag.
     */
    public Tag(int tagId, String name) {
        super(tagId, name);
    }
}