package com.example.phasmatic.ui.Profile_Menu;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;

import com.example.phasmatic.R;
import com.example.phasmatic.ui.Chat.CallsActivity;
import com.example.phasmatic.ui.Chat.UsersActivity;
import com.example.phasmatic.ui.LoginActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileMenuHelper {

    private final Activity activity;
    private final DatabaseReference usersRef;

    private String userId;
    private String userFullName;
    private String userEmail;
    private String userPhone;

    public ProfileMenuHelper(Activity activity,
                             String userId,
                             String userFullName,
                             String userEmail,
                             String userPhone) {

        this.activity = activity;
        this.userId = userId;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");
    }

    public void showProfileMenu(View anchor) {
        PopupMenu popup = new PopupMenu(activity, anchor);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_account) {
                openAccountActivity();
                return true;
            }
            else if (id == R.id.menu_logout) {
                logout();
                return true;
            }
            else if (id == R.id.menu_chat) {
                go_to_chats();
                return true;
            }
            else if (id == R.id.menu_calls) {
                go_to_calls();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void openAccountActivity() {
        Intent i = new Intent(activity, AccountActivity.class);
        i.putExtra("userId", userId);
        i.putExtra("userFullName", userFullName);
        i.putExtra("userEmail", userEmail);
        i.putExtra("userPhone", userPhone);
        activity.startActivity(i);
    }

    private void go_to_calls() {
        Intent i = new Intent(activity, CallsActivity.class);
        i.putExtra("userId", userId);
        i.putExtra("userFullName", userFullName);
        i.putExtra("userEmail", userEmail);
        i.putExtra("userPhone", userPhone);
        activity.startActivity(i);
        activity.finish();
    }

    private void logout() {
        Intent i = new Intent(activity, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();
    }

    private void go_to_chats() {
        Intent i = new Intent(activity, UsersActivity.class);
        i.putExtra("userId", userId);
        i.putExtra("userFullName", userFullName);
        i.putExtra("userEmail", userEmail);
        i.putExtra("userPhone", userPhone);
        activity.startActivity(i);
        activity.finish();
    }
}
