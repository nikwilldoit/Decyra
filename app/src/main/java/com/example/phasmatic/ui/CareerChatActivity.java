package com.example.phasmatic.ui;

import static com.example.phasmatic.extras.ProgramType.career;

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
import com.example.phasmatic.extras.HTMLFileExporter;
import com.example.phasmatic.extras.InternetConnection;
import com.example.phasmatic.extras.ProfileImageManager;
import com.example.phasmatic.extras.ProgramType;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class CareerChatActivity extends AppCompatActivity {

    TextView txtChatTitle, txtChatLog;
    EditText edtUserInput;
    Button btnSend, btnVoice, btnsave;
    ImageButton btnBack;
    OpenAIChatClient chatClient;
    ImageView imgProfile;
    private String userId, userFullName, userEmail, userPhone;
    private ProfileMenuHelper profileMenuHelper;
    private DatabaseReference usersRef;

    private String userExpectations;

    private InternetConnection inter = new InternetConnection();

    private String LLMRep = "";
    private static final int CREATE_HTML_FILE = 2003;


    @SuppressLint("SetTextI18n") //AFAIREI WARNINGS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_erasmus_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });


        if(!inter.isConnected(this)){
            inter.showCustomDialog(this);
        }

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
        btnsave = findViewById(R.id.btnsave);

        txtChatTitle.setText("DECYRA Career Assistant");

        chatClient = new OpenAIChatClient(this);

        BackButtonHelper.attach(this, R.id.btnBack);

        if (userExpectations != null && !userExpectations.isEmpty()) {
            edtUserInput.setText(userExpectations);
            edtUserInput.setSelection(userExpectations.length());
        }

        btnsave.setOnClickListener(v -> {
            if (LLMRep == null || LLMRep.trim().isEmpty()) {
                Toast.makeText(this, "No response to export", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent htmlIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            htmlIntent.addCategory(Intent.CATEGORY_OPENABLE);
            htmlIntent.setType("text/html");
            htmlIntent.putExtra(Intent.EXTRA_TITLE, "career_" + System.currentTimeMillis() + ".html");

            startActivityForResult(htmlIntent, CREATE_HTML_FILE);
        });


        btnSend.setOnClickListener(v -> {
            String userMsg = edtUserInput.getText().toString().trim();
            if (userMsg.isEmpty()) return;

            appendToChat("You: " + userMsg);
            edtUserInput.setText("");
            btnSend.setEnabled(false);
            String ConvoId = userFullName + "-CAREER";
            chatClient.sendMessage(2, ConvoId, userMsg, userFullName, new OpenAIChatClient.ChatCallback() {
                @Override
                public void onSuccess(String reply) {
                    runOnUiThread(() -> {
                        appendToChat("Assistant: \n" + reply);
                        btnSend.setEnabled(true);
                        LLMRep = reply;
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        appendToChat("Error: " + error);
                        btnSend.setEnabled(true);
                    });
                }
            });
        });

        btnVoice.setOnClickListener(v -> startSpeechRecognizer());
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

        if (requestCode == CREATE_HTML_FILE) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                boolean success = HTMLFileExporter.exportToHtml(this, data.getData(), LLMRep);

                if (!success) {
                    Toast.makeText(this, "HTML export failed", Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }

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