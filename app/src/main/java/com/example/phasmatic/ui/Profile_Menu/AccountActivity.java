package com.example.phasmatic.ui.Profile_Menu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.extras.InternetConnection;
import com.example.phasmatic.extras.ProfileImageManager;
import com.example.phasmatic.ui.BackButtonHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountActivity extends AppCompatActivity {

    // ---------- Supabase ----------
    private static final String SUPABASE_URL = "https://sbzxqcwvbbgbpykyvmfa.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNienhxY3d2YmJnYnB5a3l2bWZhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQ0MjcwNDEsImV4cCI6MjA5MDAwMzA0MX0.oUc-uXUKPE6HJS7peW3ytfW1H5uSTFP6vUa_8Zn7iuo";
    private static final String SUPABASE_BUCKET = "avatars";

    ImageButton btnBack;
    private Button btnEditProfile, btnEditAcademic;
    private TextView txtFullName, txtEmail, txtPhone;
    private TextView txtUniversity, txtAcademicLevel, txtField,
            txtLanguages, txtGpa, txtBudget, txtYearOfStudies, txtAdvisorType;

    private String userId;
    private String userFullName, userEmail, userPhone;

    private ImageView imgProfilePhoto;
    private Uri imageUri;

    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;

    private DatabaseReference usersRef;
    private DatabaseReference userInfoRef;

    private OkHttpClient httpClient = new OkHttpClient();
    private InternetConnection inter = new InternetConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        if(!inter.isConnected(this)){
            inter.showCustomDialog(this);
        }


        btnBack = findViewById(R.id.btnBack);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditAcademic = findViewById(R.id.btnEditAcademic);

        txtFullName = findViewById(R.id.txtFullName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);

        txtUniversity = findViewById(R.id.txtUniversity);
        txtAcademicLevel = findViewById(R.id.txtAcademicLevel);
        txtField = findViewById(R.id.txtField);
        txtLanguages = findViewById(R.id.txtLanguages);
        txtGpa = findViewById(R.id.txtGpa);
        txtBudget = findViewById(R.id.txtBudget);
        txtYearOfStudies = findViewById(R.id.txtYearOfStudies);
        txtAdvisorType = findViewById(R.id.txtAdvisorType);

        imgProfilePhoto = findViewById(R.id.imgProfilePhoto);

        userId = getIntent().getStringExtra("userId");

        BackButtonHelper.attachToGoModeSelection(
                this,
                R.id.btnBack,
                userId,
                userFullName,
                null,
                null
        );

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );

        usersRef = firebaseDb.getReference("users");
        userInfoRef = firebaseDb.getReference("user_info");

        loadUser();
        loadUserInfo();

        btnEditProfile.setOnClickListener(v -> {
            Intent i = new Intent(AccountActivity.this, EditProfileActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("userFullName", userFullName);
            i.putExtra("userEmail", userEmail);
            i.putExtra("userPhone", userPhone);
            startActivity(i);
        });

        btnEditAcademic.setOnClickListener(v -> {
            Intent i = new Intent(AccountActivity.this, EditUserInfoActivity.class);
            i.putExtra("userId", userId);
            startActivity(i);
        });

        imgProfilePhoto.setOnClickListener(v -> showImageOptions());
    }

    private void showImageOptions() {
        String[] options = {"Take photo", "Choose from gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Photo");

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, TAKE_PHOTO);
            }

            if (which == 1) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == PICK_IMAGE) {
                try {
                    imageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media
                            .getBitmap(getContentResolver(), imageUri);

                    imgProfilePhoto.setImageBitmap(bitmap);
                    //optional local cache
                    ProfileImageManager.saveBitmap(this, userId, bitmap);

                    uploadImageToSupabase(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == TAKE_PHOTO) {
                try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    imgProfilePhoto.setImageBitmap(photo);
                    // optional local cache
                    ProfileImageManager.saveBitmap(this, userId, photo);

                    uploadImageToSupabase(photo);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void uploadImageToSupabase(Bitmap bitmap) {
        if (userId == null || userId.isEmpty()) return;

        new Thread(() -> {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                byte[] imageBytes = bos.toByteArray();

                String path = userId + ".jpg";
                String url = SUPABASE_URL + "/storage/v1/object/" +
                        SUPABASE_BUCKET + "/" + path;

                RequestBody body = RequestBody.create(
                        imageBytes,
                        MediaType.parse("image/jpeg")
                );

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("apikey", SUPABASE_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                        .addHeader("Content-Type", "image/jpeg")
                        .addHeader("x-upsert", "true")   //epitrepei overwrite
                        .build();

                Response response = httpClient.newCall(request).execute();

                if (!response.isSuccessful()) {
                    String msg = "Upload failed: " + response.code();
                    runOnUiThread(() ->
                            Toast.makeText(AccountActivity.this, msg,
                                    Toast.LENGTH_LONG).show());
                    return;
                }

                // public bucket -> public URL
                String publicUrl = SUPABASE_URL + "/storage/v1/object/public/" +
                        SUPABASE_BUCKET + "/" + path;

                saveProfileImageUrlToFirebase(publicUrl);

                runOnUiThread(() ->
                        Toast.makeText(AccountActivity.this,
                                "Profile photo updated", Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(AccountActivity.this,
                                "Upload error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void saveProfileImageUrlToFirebase(String url) {
        if (userId == null || userId.isEmpty()) return;
        usersRef.child(userId).child("profileImageUrl").setValue(url);
    }


    private void loadUser() {
        if (userId == null || userId.isEmpty()) return;

        usersRef.child(userId).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;

            userFullName = snapshot.child("fullName").getValue(String.class);
            userEmail = snapshot.child("email").getValue(String.class);
            userPhone = snapshot.child("phoneNumber").getValue(String.class);
            String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

            txtFullName.setText(userFullName != null ? userFullName : "-");
            txtEmail.setText(userEmail != null ? userEmail : "-");
            txtPhone.setText(userPhone != null ? userPhone : "-");

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                String displayUrl = profileImageUrl + "?v=" + System.currentTimeMillis();

                Glide.with(this)
                        .load(displayUrl)
                        .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.baseline_face_24)
                        .error(R.drawable.baseline_face_24)
                        .into(imgProfilePhoto);
            } else {
                Bitmap cached = ProfileImageManager.loadBitmap(this, userId);
                if (cached != null) {
                    imgProfilePhoto.setImageBitmap(cached);
                } else {
                    imgProfilePhoto.setImageResource(R.drawable.baseline_face_24);
                }
            }
        });
    }

    private void loadUserInfo() {
        if (userId == null || userId.isEmpty()) return;

        userInfoRef.child(userId).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;

            String university = snapshot.child("university").getValue(String.class);
            String academicLevel = snapshot.child("academicLevel").getValue(String.class);
            String field = snapshot.child("field").getValue(String.class);
            String languages = snapshot.child("languages").getValue(String.class);

            Object gpaObj = snapshot.child("gpa").getValue();
            String gpa = gpaObj != null ? String.valueOf(gpaObj) : "-";

            Object budgetObj = snapshot.child("budgetPerYear").getValue();
            String budgetPerYear = budgetObj != null ? String.valueOf(budgetObj) : "-";

            Object yearObj = snapshot.child("yearOfStudies").getValue();
            String yearOfStudies = yearObj != null ? String.valueOf(yearObj) : "-";

            String advisorType = snapshot.child("advisorType").getValue(String.class);

            txtUniversity.setText("University: " + (university != null ? university : "-"));
            txtAcademicLevel.setText("Level: " + (academicLevel != null ? academicLevel : "-"));
            txtField.setText("Field: " + (field != null ? field : "-"));
            txtLanguages.setText("Languages: " + (languages != null ? languages : "-"));
            txtGpa.setText("GPA: " + gpa);
            txtBudget.setText("Budget per year: " + budgetPerYear);
            txtYearOfStudies.setText("Year of studies: " + yearOfStudies);
            txtAdvisorType.setText("Advisor type: " + (advisorType != null ? advisorType : "-"));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
        loadUserInfo();
    }
}