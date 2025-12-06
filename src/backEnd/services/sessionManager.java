package backEnd.services;
import backEnd.models.*;

public class sessionManager {
    private static sessionManager instance;
    private User currentUser;

    public static sessionManager getInstance() {
        if(instance == null) {
            instance = new sessionManager();
        }
        return instance;
    }

    public User getCurrentUser() { return currentUser;}
    public void setCurrentUser(User user) {this.currentUser = user;}

    public void clearSession() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
