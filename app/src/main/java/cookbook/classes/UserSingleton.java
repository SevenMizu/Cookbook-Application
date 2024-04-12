package cookbook.classes;

public class UserSingleton {
    private static UserSingleton instance; // Static variable to hold the single instance of UserSingleton
    private User user; // Instance variable to hold the logged-in user

    // Private constructor to prevent instantiation from outside the class
    private UserSingleton() {
        // Constructor logic can be empty or customized as needed
    }

    // Method to get the single instance of UserSingleton
    public static UserSingleton getInstance(User user) {
        if (instance == null) {
            instance = new UserSingleton();
        }
        // Set the logged-in user
        instance.setUser(user);
        return instance;
    }

    // Method to retrieve the logged-in user
    public User getUser() {
        return user;
    }

    // Method to set the logged-in user
    private void setUser(User user) {
        this.user = user;
    }
}