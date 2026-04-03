package com.example.phasmatic.ui.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.Note;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNoteActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private MaterialButton saveBtn;
    private DatabaseReference notesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleInput = findViewById(R.id.titleinput);
        descriptionInput = findViewById(R.id.descriptioninput);
        saveBtn = findViewById(R.id.savebtn);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        notesRef = firebaseDb.getReference("notes");

        saveBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                titleInput.setError("Title is required");
                return;
            }

            String noteId = notesRef.push().getKey();
            if (noteId == null) {
                Toast.makeText(this, "Failed to create note id", Toast.LENGTH_SHORT).show();
                return;
            }

            long createdTime = System.currentTimeMillis();
            Note note = new Note(noteId, title, description, createdTime);

            notesRef.child(noteId).setValue(note)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}