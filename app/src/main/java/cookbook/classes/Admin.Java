package cookbook.classes;

public class Admin extends User {
    public Admin(int userId, String username, String password, String favouriteRecipeIdsString) {
        super(userId, username, password, favouriteRecipeIdsString);
    }
}
