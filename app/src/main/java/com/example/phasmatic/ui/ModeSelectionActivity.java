package com.example.phasmatic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.service.controls.actions.ModeAction;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import android.widget.VideoView;
import android.widget.MediaController;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.processing.SurfaceProcessorNode;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.extras.InternetConnection;
import com.example.phasmatic.extras.StayLoggedHelper;
import com.example.phasmatic.ui.Forum.ForumActivity;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import android.graphics.Bitmap;
import com.example.phasmatic.extras.ProfileImageManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ModeSelectionActivity extends AppCompatActivity {

    Button btnErasmus, btnMaster, btnForum, btnCarrer;
    TextView txtTitle, txtSubtitle;
    ImageView imgProfile;
    private String userId, userFullName, userEmail, userPhone;
    private ProfileMenuHelper profileMenuHelper;
    private VideoView videoView;
    private DatabaseReference usersRef;
    private InternetConnection inter = new InternetConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mode_selection);

        if(!inter.isConnected(this)){
            inter.showCustomDialog(this);
        }

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");

        imgProfile = findViewById(R.id.imgProfile);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

        profileMenuHelper = new ProfileMenuHelper(
                this,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));
        loadProfilePhoto();

        txtTitle = findViewById(R.id.txtTitleMode);
        txtSubtitle = findViewById(R.id.txtSubtitleMode);
        btnErasmus = findViewById(R.id.btnErasmusMode);
        btnMaster = findViewById(R.id.btnMasterMode);
        btnCarrer = findViewById(R.id.btnCareerMode);
        videoView = findViewById(R.id.videoView);
        btnForum = findViewById(R.id.btnForum);


        btnErasmus.setOnClickListener(v -> {
            Intent i = new Intent(ModeSelectionActivity.this, QuestionnaireActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("userFullName", userFullName);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            i.putExtra("modeType", "erasmus");
            startActivity(i);
        });

        btnMaster.setOnClickListener(v -> {
            Intent i = new Intent(ModeSelectionActivity.this, QuestionnaireActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("userFullName", userFullName);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            i.putExtra("modeType", "master");
            startActivity(i);
        });

        btnCarrer.setOnClickListener(v -> {
            Intent i = new Intent(ModeSelectionActivity.this, QuestionnaireActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("userFullName", userFullName);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            i.putExtra("modeType", "career");
            startActivity(i);
        });

        btnForum.setOnClickListener(v -> {
            Intent i = new Intent(ModeSelectionActivity.this, ForumActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("userFullName", userFullName);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            i.putExtra("modeType", "master");
            startActivity(i);
        });

        String path = "android.resource://" + getPackageName() + "/" + R.raw.indian_video;
        Uri uri = Uri.parse(path);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnCompletionListener(mp -> videoView.start());
    }

    private void loadProfilePhoto() {
        if (userId == null || userId.isEmpty()) {
            imgProfile.setImageResource(R.drawable.baseline_face_24);
            return;
        }

        usersRef.child(userId).get().addOnSuccessListener(snapshot -> {
            String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                String displayUrl = profileImageUrl + "?t=" + System.currentTimeMillis();

                Glide.with(this)
                        .load(displayUrl)
                        .placeholder(R.drawable.baseline_face_24)
                        .error(R.drawable.baseline_face_24)
                        .into(imgProfile);
            } else {
                // fallback se local cache an uparxei
                Bitmap bitmap = ProfileImageManager.loadBitmap(this, userId);
                if (bitmap != null) {
                    imgProfile.setImageBitmap(bitmap);
                } else {
                    imgProfile.setImageResource(R.drawable.baseline_face_24);
                }
            }
        });
    }
}



