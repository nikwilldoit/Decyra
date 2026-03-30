package com.example.phasmatic.ui.Chat;

import static com.example.phasmatic.ui.BackButtonHelper.attachToGoChats;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.data.model.Message;
import com.example.phasmatic.extras.ProfileImageManager;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btnBack, btnSend, btnVoice;
    private TextView txtChatWith;
    private EditText etMessage;
    private RecyclerView rvMessages;
    private ImageView imgProfile;

    private final ArrayList<Message> messages = new ArrayList<>();
    private MessagesAdapter adapter;

    private DatabaseReference conversationsRef, messagesRef;

    private String currentUid;
    private String otherUid;
    private String otherName;


    private ProfileMenuHelper profileMenuHelper;

    private String conversationKey = null;
    private DatabaseReference usersRef;

    private final FirebaseDatabase db = FirebaseDatabase.getInstance(
            "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_someone);

        btnBack     = findViewById(R.id.btnBack);
        btnSend     = findViewById(R.id.btnSend);
        btnVoice = findViewById(R.id.btnVoice);
        txtChatWith = findViewById(R.id.txtChatWith);
        etMessage   = findViewById(R.id.etMessage);
        rvMessages  = findViewById(R.id.rvMessages);
        imgProfile  = findViewById(R.id.imgProfile);

        currentUid = getIntent().getStringExtra("userId");
        otherUid   = getIntent().getStringExtra("otherUid");
        otherName  = getIntent().getStringExtra("otherName");
        String userEmail = getIntent().getStringExtra("userEmail");
        String userPhone = getIntent().getStringExtra("userPhone");


        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");


        profileMenuHelper = new ProfileMenuHelper(
                this,
                currentUid,
                otherName,
                userEmail,
                userPhone
        );

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));
        loadProfilePhoto();

        txtChatWith.setText(otherName != null ? otherName : "Chat");

        conversationsRef = db.getReference("conversations");
        messagesRef      = db.getReference("messages");

        adapter = new MessagesAdapter(messages, currentUid);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        rvMessages.setLayoutManager(lm);
        rvMessages.setAdapter(adapter);

        attachToGoChats(this, R.id.btnBack, currentUid, otherUid, otherName);

        btnSend.setOnClickListener(v -> sendMessage());

        findOrCreateConversation();


        btnVoice.setOnClickListener(v -> startSpeechRecognizer());
    }
    @Override
    protected void onStart() {
        super.onStart();
        //String currentUid = "βαλε_εδω_το_uid_του_user"; // πχ απο intent ή auth

    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    private void listenCallStatus(String callId) {

        DatabaseReference callRef = db.getReference("calls").child(callId);

        callRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String status = snapshot.child("status").getValue(String.class);
                String channelName = snapshot.child("channelName").getValue(String.class);

                if ("accepted".equals(status)) {

                    Intent intent = new Intent(ChatActivity.this, VideoCallActivity.class);
                    intent.putExtra("channelName", channelName);
                    intent.putExtra("callId", callId);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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
            etMessage.setText(text.get(0));

            Log.i("DEMO-ANSWER", text.get(0));

        } else {
            System.out.println("Recognizer API error");
        }
    }

    private void loadProfilePhoto() {
        if (currentUid == null || currentUid.isEmpty()) {
            imgProfile.setImageResource(R.drawable.baseline_face_24);
            return;
        }

        usersRef.child(currentUid).get().addOnSuccessListener(snapshot -> {
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
                Bitmap bitmap = ProfileImageManager.loadBitmap(this, currentUid);
                if (bitmap != null) {
                    imgProfile.setImageBitmap(bitmap);
                } else {
                    imgProfile.setImageResource(R.drawable.baseline_face_24);
                }
            }
        });
    }

    //den ftiaxnei neo an uparxei hdh
    private void findOrCreateConversation() {
        conversationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String left  = ds.child("leftUser_id").getValue(String.class);
                    String right = ds.child("rightUser_id").getValue(String.class);

                    boolean match = left != null && right != null &&
                            ((left.equals(currentUid) && right.equals(otherUid)) ||
                                    (left.equals(otherUid) && right.equals(currentUid)));

                    if (match) {
                        conversationKey = ds.getKey();
                        loadMessages();
                        return;
                    }
                }
                createConversation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void createConversation() {
        DatabaseReference newRef = conversationsRef.push();
        conversationKey = newRef.getKey();
        if (conversationKey == null) return;

        HashMap<String, Object> conv = new HashMap<>();
        conv.put("id", conversationKey);
        conv.put("lastMessage", "");
        // currentUid = logged in (right), otherUid = allos (left)
        conv.put("leftUser_id", otherUid);
        conv.put("rightUser_id", currentUid);
        conv.put("timeLastMessage", nowString());

        newRef.setValue(conv).addOnSuccessListener(unused -> loadMessages());
    }

    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String convId = ds.child("conversationId").getValue(String.class);
                    if (conversationKey == null || convId == null || !conversationKey.equals(convId)) continue;

                    Message m = new Message();
                    m.id            = ds.child("id").getValue(String.class);
                    m.conversationId= convId;
                    m.sender_id     = ds.child("sender_id").getValue(String.class);
                    m.receiver_id   = ds.child("receiver_id").getValue(String.class);
                    m.messageText   = ds.child("messageText").getValue(String.class);
                    m.timeMessage   = ds.child("timeMessage").getValue(String.class);
                    m.statusOfMessage = ds.child("statusOfMessage").getValue(String.class);

                    messages.add(m);
                }

                messages.sort((a, b) -> {
                    if (a.timeMessage == null || b.timeMessage == null) return 0;
                    return a.timeMessage.compareTo(b.timeMessage);
                });

                adapter.notifyDataSetChanged();
                if (!messages.isEmpty()) {
                    rvMessages.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text) || conversationKey == null) return;

        String now = nowString();
        DatabaseReference newMsgRef = messagesRef.push();
        String key = newMsgRef.getKey();
        if (key == null) return;

        HashMap<String, Object> msg = new HashMap<>();
        msg.put("id", key);
        msg.put("conversationId", conversationKey);
        msg.put("sender_id", currentUid);
        msg.put("receiver_id", otherUid);
        msg.put("messageText", text);
        msg.put("statusOfMessage", "1");
        msg.put("timeMessage", now);

        newMsgRef.setValue(msg);
        conversationsRef.child(conversationKey).child("lastMessage").setValue(text);
        conversationsRef.child(conversationKey).child("timeLastMessage").setValue(now);

        etMessage.setText("");
    }

    private String nowString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }
}


