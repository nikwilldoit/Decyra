package com.example.phasmatic.extras;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StayLoggedHelper {

    public interface AuthCallback {
        void onLoggedIn(FirebaseUser user);
        void onLoggedOut();
    }

    public static void checkLogin(AuthCallback callback) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            callback.onLoggedIn(user);
        } else {
            callback.onLoggedOut();
        }
    }
}
