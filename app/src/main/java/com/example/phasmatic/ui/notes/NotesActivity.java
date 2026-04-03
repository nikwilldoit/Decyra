package com.example.phasmatic.ui.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.Note;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    private MaterialButton addNoteBtn;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<Note> notesList = new ArrayList<>();
    private DatabaseReference notesRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNoteBtn = findViewById(R.id.addnewnotebtn);
        recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this, notesList);
        recyclerView.setAdapter(myAdapter);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        notesRef = firebaseDb.getReference("notes");

        addNoteBtn.setOnClickListener(v ->
                startActivity(new Intent(NotesActivity.this, AddNoteActivity.class))
        );

        loadNotes();
    }

    private void loadNotes() {
        notesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                notesList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Note note = child.getValue(Note.class);
                    if (note != null) {
                        notesList.add(note);
                    }
                }

                Collections.sort(notesList, (n1, n2) ->
                        Long.compare(n2.getCreatedTime(), n1.getCreatedTime()));

                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}