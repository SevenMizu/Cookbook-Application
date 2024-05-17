package cookbook.classes;

public class UserSingleton {
    private static UserSingleton instance; 
    private User user; 

    private UserSingleton() {
    }

    public static UserSingleton getInstance(User user) {
        if (instance == null) {
            instance = new UserSingleton();
        }
        // Set the logged-in user
        instance.setUser(user);
        return instance;
    }

    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
    }
}