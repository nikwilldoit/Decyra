package com.example.phasmatic.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.data.ai.OpenAIChatClient;
import com.example.phasmatic.extras.InternetConnection;
import com.example.phasmatic.extras.ProfileImageManager;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import android.graphics.Bitmap;
import com.example.phasmatic.extras.ProfileImageManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ErasmusChatActivity extends AppCompatActivity {

    TextView txtChatTitle, txtChatLog;
    EditText edtUserInput;
    Button btnSend, btnVoice;
    ImageButton btnBack;
    OpenAIChatClient chatClient;
    ImageView imgProfile;
    private String userId, userFullName, userEmail, userPhone;
    private ProfileMenuHelper profileMenuHelper;
    private DatabaseReference usersRef;

    private String userExpectations;

    private InternetConnection inter = new InternetConnection();

    @SuppressLint("SetTextI18n") //AFAIREI WARNINGS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_erasmus_chat);


        if(!inter.isConnected(this)){
            inter.showCustomDialog(this);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");
        userExpectations = intent.getStringExtra("userExpectations");

        imgProfile = findViewById(R.id.imgProfile);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");
        DatabaseReference userInfoRef = firebaseDb.getReference("user_info");
        profileMenuHelper = new ProfileMenuHelper(
                this,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));
        loadProfilePhoto();

        txtChatTitle = findViewById(R.id.txtChatTitle);
        txtChatLog = findViewById(R.id.txtChatLog);
        edtUserInput = findViewById(R.id.edtUserInput);
        btnSend = findViewById(R.id.btnSend);
        btnVoice = findViewById(R.id.btnVoice);

        txtChatTitle.setText("DECYRA Erasmus Assistant");

        chatClient = new OpenAIChatClient(this);

        BackButtonHelper.attach(this, R.id.btnBack);

        if (userExpectations != null && !userExpectations.isEmpty()) {
            edtUserInput.setText(userExpectations);
            edtUserInput.setSelection(userExpectations.length());
        }

        btnSend.setOnClickListener(v -> {
            String userMsg = edtUserInput.getText().toString().trim();
            if (userMsg.isEmpty()) return;

            appendToChat("You: " + userMsg);
            edtUserInput.setText("");
            btnSend.setEnabled(false);

            String ConvoId = userFullName + "-ERASMUS";

            userInfoRef.child(userId).get().addOnSuccessListener(snapshot -> {

                int uniId = 0;

                if (snapshot.exists()) {
                    String university = snapshot.child("university").getValue(String.class);
                    uniId = mapUniversityToId(university);
                }

                chatClient.sendMessage(
                        uniId,
                        ConvoId,
                        userMsg,
                        userFullName,
                        new OpenAIChatClient.ChatCallback() {
                            @Override
                            public void onSuccess(String reply) {
                                runOnUiThread(() -> {
                                    appendToChat("Assistant: \n" + reply);
                                    btnSend.setEnabled(true);
                                });
                            }

                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> {
                                    appendToChat("Error: " + error);
                                    btnSend.setEnabled(true);
                                });
                            }
                        }
                );

            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Firebase error", Toast.LENGTH_SHORT).show();
                btnSend.setEnabled(true);
            });
        });

        btnVoice.setOnClickListener(v -> startSpeechRecognizer());
    }

    private int mapUniversityToId(String university) {
        if (university == null) return 0;

        if (university.contains("Οικονομικό Πανεπιστήμιο Αθηνών")){
            return 0;
        }
        if (university.contains("Πανεπιστήμιο Θεσσαλίας")) {
            return 4;
        }
        if (university.contains("Αριστοτέλειο Πανεπιστήμιο Θεσσαλονίκης")) return 5;
        if (university.contains("Εθνικό και Καποδιστριακό Πανεπιστήμιο Αθηνών")) return 6;
        if (university.contains("Πανεπιστήμιο Κρήτης")) return 7;
        if (university.contains("Πανεπιστήμιο Πειραιώς")) return 8;
        if (university.contains("Πανεπιστήμιο Πελοποννήσου")) return 9;
        if (university.contains("Χαροκόπειο Πανεπιστήμιο")) return 10;
        if (university.contains("Ιόνιο Πανεπιστήμιο")) return 11;
        return 0;
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

    private void startSpeechRecognizer() {
        int REQUEST_SPEECH_RECOGNIZER = 3000;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "el-GR");

        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "el-GR");


        try {
            startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Η αναγνώριση φωνής δεν υποστηρίζεται στη συσκευή σας", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int REQUEST_SPEECH_RECOGNIZER = 3000;
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("DEMO-REQUESTCODE", Integer.toString(requestCode));
        Log.i("DEMO-RESULTCODE", Integer.toString(resultCode));

        if (requestCode == REQUEST_SPEECH_RECOGNIZER && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            edtUserInput.setText(text.get(0));

            Log.i("DEMO-ANSWER", text.get(0));

        } else {
            System.out.println("Recognizer API error");
        }
    }

    private void appendToChat(String text) {
        if (txtChatLog.getText().length() == 0) {
            txtChatLog.setText(text);
        } else {
            txtChatLog.append("\n\n" + text);
        }
    }
}
