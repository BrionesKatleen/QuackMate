//package backEnd.services;
//import backEnd.models.*;
//
//public class sessionManager {
//    private static sessionManager instance;
//    private User currentUser;
//
//    public static sessionManager getInstance() {
//        if(instance == null) {
//            instance = new sessionManager();
//        }
//        return instance;
//    }
//
//    public User getCurrentUser() { return currentUser;}
//    public void setCurrentUser(User user) {this.currentUser = user;}
//
//    public void clearSession() {
//        this.currentUser = null;
//    }
//
//    public boolean isLoggedIn() {
//        return currentUser != null;
//    }
//}

package backEnd.services;

import backEnd.models.User;
import backEnd.models.Duck;
import java.util.List;

public class sessionManager {
    private static sessionManager instance;
    private User currentUser;
    private List<Duck> currentDucks;

    public static sessionManager getInstance() {
        if(instance == null) {
            instance = new sessionManager();
        }
        return instance;
    }

    public void startSession(User user, List<Duck> ducks) {
        this.currentUser = user;
        this.currentDucks = ducks;
    }

    public void endSession() {
        currentUser = null;
        currentDucks = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {this.currentUser = user;}

    public List<Duck> getCurrentDucks() {
        return currentDucks;
    }

    public boolean isSessionActive() {
        return currentUser != null;
    }
}
