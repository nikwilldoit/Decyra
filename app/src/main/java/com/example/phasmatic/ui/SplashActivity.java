package com.example.phasmatic.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class SplashActivity extends AppCompatActivity {

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );

        usersRef = firebaseDb.getReference("users");

        if (user == null) {
            goToLogin();
            return;
        }

        String uid = user.getUid();

        usersRef.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            FirebaseAuth.getInstance().signOut();
                            goToLogin();
                            return;
                        }

                        String userName = snapshot.child("fullName").getValue(String.class);
                        String userEmail = snapshot.child("email").getValue(String.class);
                        String userPhone = snapshot.child("phoneNumber").getValue(String.class);
                        try {
                            Long remember = snapshot.child("remember").getValue(Long.class);
                            if (remember == 1) {
                                Intent i = new Intent(SplashActivity.this, ModeSelectionActivity.class);

                                i.putExtra("userId", uid);
                                i.putExtra("userFullName", userName);
                                i.putExtra("userEmail", userEmail);
                                i.putExtra("userPhone", userPhone);

                                startActivity(i);
                                finish();
                            } else {
                                goToLogin();
                            }
                        }catch(Exception e){
                            System.err.println("Exception ERROR");
                            goToLogin();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        goToLogin();
                    }
                });
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}