package com.example.phasmatic.ui.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CallsActivity extends AppCompatActivity {

    private static final String TAG = "CALLS";

    private Spinner spnUsers;
    private EditText edtMeetingCode;
    private Button btnJoinCall;

    private String currentUserId;
    private String currentUserFullName;
    private String currentUserEmail;
    private String currentUserPhone;

    private DatabaseReference usersRef;

    private final List<User> userList = new ArrayList<>();
    private final List<String> userNames = new ArrayList<>();
    private ArrayAdapter<String> usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calls);

        currentUserId = getIntent().getStringExtra("userId");
        currentUserFullName = getIntent().getStringExtra("userFullName");
        currentUserEmail = getIntent().getStringExtra("userEmail");
        currentUserPhone = getIntent().getStringExtra("userPhone");

        spnUsers       = findViewById(R.id.spnUsers);
        edtMeetingCode = findViewById(R.id.edtMeetingCode);
        btnJoinCall    = findViewById(R.id.btnJoinCall);

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = db.getReference("users");

        usersAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                userNames
        );
        usersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUsers.setAdapter(usersAdapter);

        loadUsers();

        btnJoinCall.setOnClickListener(v -> joinCall());
    }

    private void loadUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                userNames.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    User u = child.getValue(User.class);
                    if (u == null) continue;

                    if (child.getKey() != null && child.getKey().equals(currentUserId)) {
                        continue;
                    }

                    u.setId(child.getKey());
                    userList.add(u);
                    userNames.add(u.getFullName() != null ? u.getFullName() : child.getKey());
                }

                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void joinCall() {
        String code = edtMeetingCode.getText().toString().trim();
        if (code.isEmpty()) {
            edtMeetingCode.setError("Meeting code required");
            return;
        }

        int pos = spnUsers.getSelectedItemPosition();
        String otherUid = null;
        String otherName = null;
        if (pos >= 0 && pos < userList.size()) {
            User other = userList.get(pos);
            otherUid = other.getId();
            otherName = other.getFullName();
        }

        Log.d(TAG, "joinCall: channel=" + code +
                " otherUid=" + otherUid + " otherName=" + otherName);

        Intent i = new Intent(this, VideoCallActivity.class);
        i.putExtra("channelName", code);
        startActivity(i);
    }
}