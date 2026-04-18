package com.example.phasmatic.ui.Profile_Menu;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;

import com.example.phasmatic.R;
import com.example.phasmatic.ui.conference.GeneralConferenceActivity;
import com.example.phasmatic.ui.Chat.UsersActivity;
import com.example.phasmatic.ui.LoginActivity;
import com.example.phasmatic.ui.notes.NotesActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileMenuHelper {

    private final Activity activity;
    private final DatabaseReference usersRef;

    private String userId;
    private String userFullName;
    private String userEmail;
    private String userPhone;
    private Long log;

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
            else if (id == R.id.menu_conference) {
                go_to_conference();
                return true;
            }
            else if (id == R.id.menu_notes) {
                go_to_notes();
                return true;
            }

            return false;
        });

        popup.show();
    }

    private void go_to_notes() {
        Intent i = new Intent(activity, NotesActivity.class);
        i.putExtra("userId", userId);
        i.putExtra("userFullName", userFullName);
        i.putExtra("userEmail", userEmail);
        i.putExtra("userPhone", userPhone);
        activity.startActivity(i);
        activity.finish();
    }

    private void openAccountActivity() {
        Intent i = new Intent(activity, AccountActivity.class);
        i.putExtra("userId", userId);
        i.putExtra("userFullName", userFullName);
        i.putExtra("userEmail", userEmail);
        i.putExtra("userPhone", userPhone);
        activity.startActivity(i);
    }

    private void logout() {

        usersRef.child(userId).child("remember").setValue(0);
        Intent i = new Intent(activity, LoginActivity.class);
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

    private void go_to_conference(){
        Intent i = new Intent(activity, GeneralConferenceActivity.class);
        i.putExtra("userId", userId);
        i.putExtra("userFullName", userFullName);
        i.putExtra("userEmail", userEmail);
        i.putExtra("userPhone", userPhone);
        activity.startActivity(i);
        activity.finish();
    }
}
