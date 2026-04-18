package com.example.phasmatic.ui.conference;

import static com.google.gson.internal.bind.util.ISO8601Utils.format;
import static java.util.UUID.randomUUID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.data.model.User;
import com.example.phasmatic.extras.InternetConnection;
import com.example.phasmatic.extras.NotificationSender;
import com.example.phasmatic.extras.ProfileImageManager;
import com.example.phasmatic.ui.ModeSelectionActivity;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.phasmatic.data.model.Note;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class GeneralConferenceActivity extends AppCompatActivity {

    private Button btnJoin, btnConfirm;
    private String userId;
    private ImageButton btnBack;
    private ImageView btnProfile;
    private DatabaseReference usersRef;
    private String userFullName, userEmail, userPhone;
    private ProfileMenuHelper profileMenuHelper;
    DatabaseReference conferenceRef;
    ConferenceAdapter adapter;
    public List<User> userList = new ArrayList<>();
    List<User> fullUserList = new ArrayList<>();

    private InternetConnection inter = new InternetConnection();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_conference);


        if(!inter.isConnected(this)){
            inter.showCustomDialog(this);
        }


        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        btnProfile = findViewById(R.id.imgProfile);
        btnConfirm = findViewById(R.id.btnConfirmConference);
        btnJoin = findViewById(R.id.btnJoinRoom);
        btnBack = findViewById(R.id.btnBackConference);

        RecyclerView recyclerView = findViewById(R.id.recyclerUsers);
        EditText edtSearch = findViewById(R.id.edtSearchUser);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

        usersRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                fullUserList.clear();
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user != null && user.getId() != null) {
                        fullUserList.add(user);
                        userList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                android.widget.Toast.makeText(GeneralConferenceActivity.this, "DB Error", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new ConferenceAdapter(userList, user -> {});
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                userList.clear();

                if (query.isEmpty()) {
                    userList.addAll(fullUserList);
                } else {
                    for (User user : fullUserList) {
                        String name = user.getFullName() != null ? user.getFullName().toLowerCase() : "";
                        if (name.contains(query)) {
                            userList.add(user);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");

        profileMenuHelper = new ProfileMenuHelper(this, userId, userFullName, userEmail, userPhone);

        conferenceRef = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        ).getReference("conferences");

        btnConfirm.setOnClickListener(v -> {
            List<String> selected = adapter.getSelectedUserIds();
            if (selected.isEmpty()) {
                Toast.makeText(this, "Choose at least one other user", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar now = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        TimePickerDialog timePicker = new TimePickerDialog(
                                this,
                                (timeView, hourOfDay, minute) -> {
                                    Calendar selectedDateTime = java.util.Calendar.getInstance();
                                    selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                                    long startMillis = selectedDateTime.getTimeInMillis();

                                    String eventDate = year + "-" + (month + 1) + "-" + dayOfMonth;

                                    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

                                    String timeText = format(selectedDateTime.getTime());

                                    String code = randomUUID().toString().substring(0, 6);
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance(
                                            "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
                                    ).getReference();

                                    DatabaseReference roomRef = rootRef.child("conferences").child(code);
                                    DatabaseReference notesRef = rootRef.child("notes");

                                    List<String> participants = new ArrayList<>(selected);
                                    if (!participants.contains(userId)) participants.add(userId);

                                    Map<String, Object> updates = new HashMap<>();

                                    for (String uid : participants) {
                                        updates.put("/conferences/" + code + "/participants/" + uid, true);
                                    }

                                    updates.put("/conferences/" + code + "/code", code);
                                    updates.put("/conferences/" + code + "/event_date", eventDate);
                                    updates.put("/conferences/" + code + "/time_start", startMillis);
                                    updates.put("/conferences/" + code + "/time_text", timeText);
                                    updates.put("/conferences/" + code + "/note", "Conference meeting");
                                    updates.put("/conferences/" + code + "/created_by", userId);
                                    updates.put("/conferences/" + code + "/created_by_name", userFullName);

                                    for (String participantId : participants) {
                                        String noteId = notesRef.push().getKey();
                                        if (noteId == null) continue;

                                        String noteTitle = "You have a new Video Conference";
                                        String noteDescription =
                                                "Code: " + code +
                                                        "\nCreated by: " + userFullName +
                                                        "\nDate: " + eventDate +
                                                        "\nTime: " + timeText;

                                        Note note = new Note(
                                                noteId,
                                                noteTitle,
                                                noteDescription,
                                                System.currentTimeMillis(),
                                                participantId,
                                                false
                                        );
                                        updates.put("/notes/" + noteId, note);
                                        String senderName = userFullName != null ? userFullName : "User";
                                        firebaseDb.getReference("users").child(participantId).child("fcmToken")
                                                .get().addOnSuccessListener(snapshot -> {
                                                    String token = snapshot.getValue(String.class);
                                                    if (token != null && !token.isEmpty()) {
                                                        NotificationSender.send(token, senderName, "Invited to a new Video Conference");
                                                    }
                                                });
                                    }

                                    rootRef.updateChildren(updates)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(
                                                        this,
                                                        "Scheduled at " + eventDate + " " + timeText,
                                                        Toast.LENGTH_LONG
                                                ).show();
                                                adapter.markConfirmedUsers(selected);
                                                adapter.notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                            );

                                    Toast.makeText(
                                            this,
                                            "Scheduled at " + eventDate + " " + timeText,
                                            Toast.LENGTH_LONG
                                    ).show();
                                    adapter.markConfirmedUsers(selected);
                                    adapter.notifyDataSetChanged();

                                },
                                now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE),
                                true
                        );
                        timePicker.show();
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });

        btnJoin.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Room Code");

            final android.widget.EditText input = new android.widget.EditText(this);
            input.setHint("Code...");
            builder.setView(input);

            builder.setPositiveButton("Join", (dialog, which) -> {
                String code = input.getText().toString().trim();
                if (code.isEmpty()) {
                    android.widget.Toast.makeText(this, "Enter a code", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                conferenceRef.child(code).get().addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        android.widget.Toast.makeText(this, "Invalid code", android.widget.Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Long startMillis = snapshot.child("time_start").getValue(Long.class);
                    if (startMillis == null) {
                        android.widget.Toast.makeText(this, "Invalid event time", android.widget.Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long now = System.currentTimeMillis();
                    long fifteenMinutes = 15 * 60 * 1000;
                    long allowedJoinTime = startMillis - fifteenMinutes;

                    if (now >= allowedJoinTime) {
                        Intent i = new Intent(GeneralConferenceActivity.this, ConferenceActivity.class);
                        i.putExtra("code", code);
                        i.putExtra("userId", userId);
                        i.putExtra("userName", userFullName);
                        startActivity(i);
                    } else {
                        long diffMillis = allowedJoinTime - now;
                        long minutes = diffMillis / 60000;
                        long seconds = (diffMillis % 60000) / 1000;
                        android.widget.Toast.makeText(
                                GeneralConferenceActivity.this,
                                "Join opens in " + minutes + "m " + seconds + "s",
                                android.widget.Toast.LENGTH_LONG
                        ).show();
                    }

                }).addOnFailureListener(e -> {
                    android.widget.Toast.makeText(this, "Error connecting to server", android.widget.Toast.LENGTH_SHORT).show();
                });
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        btnBack.setOnClickListener(v -> {
            Intent i = new Intent(this, ModeSelectionActivity.class);
            i.putExtra("userId", userId);
            startActivity(i);
        });

        btnProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));
        loadProfilePhoto();
    }

    private void loadProfilePhoto() {
        if (userId == null || userId.isEmpty()) {
            btnProfile.setImageResource(R.drawable.baseline_face_24);
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
                        .into(btnProfile);
            } else {
                Bitmap bitmap = ProfileImageManager.loadBitmap(this, userId);
                if (bitmap != null) btnProfile.setImageBitmap(bitmap);
                else btnProfile.setImageResource(R.drawable.baseline_face_24);
            }
        });
    }
}