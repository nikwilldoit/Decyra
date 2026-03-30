package com.example.phasmatic.ui.Chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.phasmatic.R;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment;

public class ConferenceActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    private String userId;
    private String conferenceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        conferenceID = intent.getStringExtra("conferenceID");

        // fallback για testing
        if (userId == null) userId = "user123";
        if (conferenceID == null) conferenceID = "test_room_1";

        if (checkPermissions()) {
            addFragment();
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (checkPermissions()) {
                addFragment();
            }
        }
    }

    private void addFragment() {

        long appID = 581663696;
        String appSign = "b8262be56354041edcd4128c02f73d0858f0d143f55036a2b79b45b16f61011c";

        // Zego θέλει clean userId
        String safeUserId = userId.replaceAll("[^a-zA-Z0-9_]", "");
        if (safeUserId.isEmpty()) safeUserId = "user123";

        String userName = "user_" + safeUserId;

        // 👇 ΟΠΩΣ tutorial (χωρίς defaultConfig)
        ZegoUIKitPrebuiltVideoConferenceConfig config =
                new ZegoUIKitPrebuiltVideoConferenceConfig();

        ZegoUIKitPrebuiltVideoConferenceFragment fragment =
                ZegoUIKitPrebuiltVideoConferenceFragment.newInstance(
                        appID,
                        appSign,
                        safeUserId,
                        userName,
                        conferenceID,
                        config
                );

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}