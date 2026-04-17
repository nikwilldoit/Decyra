package com.example.phasmatic.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
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
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import android.graphics.Bitmap;
import com.example.phasmatic.extras.ProfileImageManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import com.example.phasmatic.extras.PDF;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class MasterChatActivity extends AppCompatActivity {

    TextView txtChatTitle, txtChatLog;
    EditText edtUserInput;
    Button btnSend, btnVoice, btnPdf;
    ImageButton btnBack;
    ImageView imgProfile;
    private String userId, userFullName, userEmail, userPhone;
    OpenAIChatClient chatClient;
    private ProfileMenuHelper profileMenuHelper;
    private DatabaseReference usersRef;

    private String userExpectations;

    private InternetConnection inter = new InternetConnection();

    private String LLMRep = "";
    private static final int CREATE_PDF_FILE = 2001;

    private OkHttpClient httpClient = new OkHttpClient();

    private static final String SUPABASE_FUNCTION_URL =
            "https://sbzxqcwvbbgbpykyvmfa.supabase.co/functions/v1/send-email";
    private static final String APP_SHARED_SECRET = "decyra_email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_master_chat);


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
        btnPdf = findViewById(R.id.btnpdf);

        txtChatTitle.setText("DECYRA Master Assistant");

        chatClient = new OpenAIChatClient(this);

        BackButtonHelper.attach(this, R.id.btnBack);

        if (userExpectations != null && !userExpectations.isEmpty()) {
            edtUserInput.setText(userExpectations);
            edtUserInput.setSelection(userExpectations.length());
        }

        btnPdf.setOnClickListener(v -> {
            if (LLMRep == null || LLMRep.trim().isEmpty()) {
                Toast.makeText(this, "No response to export", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent pdfIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            pdfIntent.addCategory(Intent.CATEGORY_OPENABLE);
            pdfIntent.setType("application/pdf");
            pdfIntent.putExtra(Intent.EXTRA_TITLE, "master_" + System.currentTimeMillis() + ".pdf");
            startActivityForResult(pdfIntent, CREATE_PDF_FILE);

            if (userEmail == null || userEmail.trim().isEmpty()) {
                Toast.makeText(this, "No email for this user", Toast.LENGTH_SHORT).show();
                return;
            }

            String subject = "DECYRA Master Results";
            String html = "<h2>DECYRA Master Assistant</h2>"
                    + "<p>Dear " + (userFullName != null ? userFullName : "student") + ",</p>"
                    + "<p>" + LLMRep.replace("\n", "<br>") + "</p>";

            sendEmailwithSupabase(userEmail, subject, html);
        });

        btnSend.setOnClickListener(v -> {
            String userMsg = edtUserInput.getText().toString().trim();
            if (userMsg.isEmpty()) return;

            appendToChat("You: " + userMsg);
            edtUserInput.setText("");
            btnSend.setEnabled(false);
            String ConvoId = userFullName + "-MASTER";
            chatClient.sendMessage(1, ConvoId,userMsg,userFullName, new OpenAIChatClient.ChatCallback() {
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

    private void sendEmailwithSupabase(String to, String subject, String html) {
        new Thread(() -> {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                JSONObject json = new JSONObject();
                json.put("to", to);
                json.put("subject", subject);
                json.put("html", html);

                RequestBody body = RequestBody.create(json.toString(), JSON);

                Request request = new Request.Builder()
                        .url(SUPABASE_FUNCTION_URL)
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-app-secret", APP_SHARED_SECRET)
                        .build();

                Response response = httpClient.newCall(request).execute();

                int code = response.code();
                String respBody = response.body() != null ? response.body().string() : "";

                Log.i("EMAIL_DEBUG", "code=" + code + " body=" + respBody);

                if (response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(
                                    this,
                                    "Email sent successfully",
                                    Toast.LENGTH_SHORT
                            ).show()
                    );
                } else {
                    String msg = "Email failed: " + code;
                    runOnUiThread(() ->
                            Toast.makeText(
                                    this,
                                    msg,
                                    Toast.LENGTH_LONG
                            ).show()
                    );
                }

            } catch (Exception e) {
                Log.e("EMAIL_DEBUG", "Exception: " + e.getMessage(), e);
                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "Email error: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        }).start();
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

        if (requestCode == CREATE_PDF_FILE) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                boolean success = PDF.exportToPdf(this, data.getData(), LLMRep);
                if (!success) {
                    Toast.makeText(this, "PDF export failed", Toast.LENGTH_SHORT).show();
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
