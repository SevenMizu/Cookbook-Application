package cookbook.classes;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String username;
    private String password;
    private List<Integer> favouriteRecipeIds; // List of favorite recipe IDs

    // Constructor with favorite recipes
    public User(int userId, String username, String password, String favouriteRecipeIdsString) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.favouriteRecipeIds = parseFavouriteRecipeIds(favouriteRecipeIdsString);
    }

    // Helper method to parse favorite recipe IDs from a string
    private List<Integer> parseFavouriteRecipeIds(String favouriteRecipeIdsString) {
        List<Integer> ids = new ArrayList<>();
        if (favouriteRecipeIdsString != null && !favouriteRecipeIdsString.isEmpty()) {
            String[] parts = favouriteRecipeIdsString.split(",");
            for (String part : parts) {
                try {
                    ids.add(Integer.parseInt(part.trim())); // trim to remove any spaces before or after the number
                } catch (NumberFormatException e) {
                    // Handle the case where the string is not a valid integer
                    System.out.println("Invalid number format in recipe IDs: " + part);
                }
            }
        }
        return ids;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and setter for favouriteRecipeIds
    public List<Integer> getFavouriteRecipeIds() {
        return favouriteRecipeIds;
    }

    // New method to get favouriteRecipeIds as a comma-separated string
    public String getFavouriteRecipeIdsAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < favouriteRecipeIds.size(); i++) {
            sb.append(favouriteRecipeIds.get(i));
            if (i < favouriteRecipeIds.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    // Method to add a favorite recipe ID
    public void addFavouriteRecipeId(int recipeId) {
        if (!favouriteRecipeIds.contains(recipeId)) {
            favouriteRecipeIds.add(recipeId);
        }
    }

    // Method to remove a favorite recipe ID
    public void removeFavouriteRecipeId(int recipeId) {
        favouriteRecipeIds.remove(Integer.valueOf(recipeId));
    }

    public void setFavouriteRecipeIds(List<Integer> favouriteRecipeIds) {
        this.favouriteRecipeIds = favouriteRecipeIds;
    }

    // Method to check password validity
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

}
