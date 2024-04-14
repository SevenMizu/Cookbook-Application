package cookbook.classes;

/**
 * Represents a Comment entity for a Recipe.
 */
public class Comment {
    private int commentId;
    private String text; // The text content of the comment
    private User author; // The author of the comment

    /**
     * Constructs a new Comment object.
     * @param text The text content of the comment.
     * @param author The author of the comment.
     */
    public Comment(int commentId, String text, User author) {
        this.commentId = commentId;
        this.text = text;
        this.author = author;
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
    public User getAuthor() {
        return author;
    }

    /**
     * Sets the author of the comment.
     * @param author The author to set.
     */
    public void setAuthor(User author) {
        this.author = author;
    }
}