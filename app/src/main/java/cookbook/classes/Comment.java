package cookbook.classes;

/**
 * Represents a Comment entity for a Recipe.
 */
public class Comment {
    private int commentId;
    private String text; // The text content of the comment
    private int authorID; // The author of the comment

    /**
     * Constructs a new Comment object.
     * @param text The text content of the comment.
     * @param author The author of the comment.
     */
    public Comment(int commentId, String text, int authorID) {
        this.commentId = commentId;
        this.text = text;
        this.authorID = authorID;
    }

    /**
     * Retrieves the text content of the comment.
     * @return The text content of the comment.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text content of the comment.
     * @param text The text content to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Retrieves the author of the comment.
     * @return The author of the comment.
     */
    public int getAuthorID() {
        return authorID;
    }

    /**
     * Sets the author of the comment.
     * @param author The author to set.
     */
    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }
}