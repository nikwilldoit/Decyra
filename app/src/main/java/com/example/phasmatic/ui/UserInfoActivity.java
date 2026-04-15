package com.example.phasmatic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.processing.SurfaceProcessorNode;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.UserInfo;
import com.example.phasmatic.extras.InternetConnection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserInfoActivity extends AppCompatActivity {

    private Spinner spnUniversity, spnYear;
    private EditText edtAcademicLevel, edtLanguages, edtGpa, edtField, edtBudget;
    private Button btnSave;

    private String userId, userFullName, userEmail, userPhone;

    private DatabaseReference userInfoRef;
    private DatabaseReference universitiesRef;

    private ArrayAdapter<String> universityAdapter;
    private final List<String> universityList = new ArrayList<>();
    private boolean hasUserInfo = false;
    private ImageView imgAdvisorMale, imgAdvisorFemale, imgAdvisorRobot;
    private String advisorType; //male,female,robot

    private InternetConnection inter = new InternetConnection();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        if(!inter.isConnected(this)){
            inter.showCustomDialog(this);
        }

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");
        hasUserInfo = intent.getBooleanExtra("hasUserInfo", false);

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Missing user, redirecting to login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        spnUniversity = findViewById(R.id.spnUniversity);
        spnYear = findViewById(R.id.spnYear);
        edtAcademicLevel = findViewById(R.id.edtAcademicLevel);
        edtLanguages = findViewById(R.id.edtLanguages);
        edtGpa = findViewById(R.id.edtGpa);
        edtField = findViewById(R.id.edtField);
        edtBudget = findViewById(R.id.edtBudget);
        btnSave = findViewById(R.id.btnSaveUserInfo);
        imgAdvisorMale = findViewById(R.id.imgAdvisorMale);
        imgAdvisorFemale = findViewById(R.id.imgAdvisorFemale);
        imgAdvisorRobot = findViewById(R.id.imgAdvisorRobot);

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        userInfoRef = db.getReference("user_info");
        universitiesRef = db.getReference("universities");

        setupYearSpinner();
        loadUniversitiesIntoSpinner();

        imgAdvisorMale.setOnClickListener(v -> selectAdvisor("male"));
        imgAdvisorFemale.setOnClickListener(v -> selectAdvisor("female"));
        imgAdvisorRobot.setOnClickListener(v -> selectAdvisor("robot"));

        btnSave.setOnClickListener(v -> saveUserInfo());
    }

    private void selectAdvisor(String type) {
        advisorType = type;

        imgAdvisorMale.setBackgroundResource(
                "male".equals(type) ? R.drawable.advisor_selected_bg : R.drawable.advisor_unselected_bg
        );
        imgAdvisorFemale.setBackgroundResource(
                "female".equals(type) ? R.drawable.advisor_selected_bg : R.drawable.advisor_unselected_bg
        );
        imgAdvisorRobot.setBackgroundResource(
                "robot".equals(type) ? R.drawable.advisor_selected_bg : R.drawable.advisor_unselected_bg
        );
    }


    @Override
    protected void onStart() {
        super.onStart();
        prefillUserInfo();
    }

    private void setupYearSpinner() {
        List<String> years = new ArrayList<>();
        years.add("1");
        years.add("2");
        years.add("3");
        years.add("4");

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                years
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnYear.setAdapter(yearAdapter);
    }

    private void loadUniversitiesIntoSpinner() {
        universityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                universityList
        );
        universityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUniversity.setAdapter(universityAdapter);

        universitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                universityList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = child.child("name").getValue(String.class);
                    String country = child.child("country").getValue(String.class);
                    if (name != null && Objects.equals(country, "Ελλάδα")) {
                        universityList.add(name);
                    }
                }
                universityAdapter.notifyDataSetChanged();

                if (hasUserInfo) {
                    prefillUserInfo();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(UserInfoActivity.this,
                        "Failed to load universities", Toast.LENGTH_SHORT).show();

                if (hasUserInfo) {
                    prefillUserInfo();
                }
            }
        });
    }

    private void prefillUserInfo() {
        if (userId == null || userId.isEmpty()) return;

        userInfoRef.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        UserInfo info = snapshot.getValue(UserInfo.class);
                        if (info == null) return;

                        String uni = info.getUniversity();
                        if (uni != null) {
                            int index = universityList.indexOf(uni);
                            if (index >= 0) {
                                spnUniversity.setSelection(index);
                            }
                        }

                        if (info.getAcademicLevel() != null)
                            edtAcademicLevel.setText(info.getAcademicLevel());

                        if (info.getLanguages() != null)
                            edtLanguages.setText(info.getLanguages());

                        if (info.getGpa() != null)
                            edtGpa.setText(info.getGpa());

                        if (info.getField() != null)
                            edtField.setText(info.getField());

                        if (info.getBudgetPerYear() != null)
                            edtBudget.setText(String.valueOf(info.getBudgetPerYear()));

                        if (info.getYearOfStudies() != null) {
                            int yearIndex = info.getYearOfStudies() - 1;
                            if (yearIndex >= 0 && yearIndex < spnYear.getCount()) {
                                spnYear.setSelection(yearIndex);
                            }
                        }
                        if (info.getAdvisorType() != null) {
                            selectAdvisor(info.getAdvisorType());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }



    private void saveUserInfo() {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User id missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String university = (String) spnUniversity.getSelectedItem();
        String academicLevel = edtAcademicLevel.getText().toString().trim();
        String languages = edtLanguages.getText().toString().trim();
        String gpaStr = edtGpa.getText().toString().trim();
        String field = edtField.getText().toString().trim();
        String budgetStr = edtBudget.getText().toString().trim();
        String yearStr = (String) spnYear.getSelectedItem();

        if (university == null || academicLevel.isEmpty() || languages.isEmpty()
                || gpaStr.isEmpty() || field.isEmpty() || budgetStr.isEmpty() || yearStr == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Double budget;
        Integer year;
        try {
            budget = Double.parseDouble(budgetStr);
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid numeric values", Toast.LENGTH_SHORT).show();
            return;
        }

        if (advisorType == null) {
            Toast.makeText(this, "Please choose an advisor", Toast.LENGTH_SHORT).show();
            return;
        }

        String advisorImage;
        switch (advisorType) {
            case "male":
                advisorImage = "male.png";
                break;
            case "female":
                advisorImage = "female.png";
                break;
            case "robot":
            default:
                advisorImage = "robot.png";
                break;
        }

        UserInfo info = new UserInfo(
                userId,
                university,
                academicLevel,
                languages,
                gpaStr,
                field,
                budget,
                year,
                advisorType,
                advisorImage
        );

        userInfoRef.child(userId)
                .setValue(info)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Info saved", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(UserInfoActivity.this, ModeSelectionActivity.class);
                    i.putExtra("userId", userId);
                    i.putExtra("userFullName", userFullName);
                    i.putExtra("userEmail", userEmail);
                    i.putExtra("userPhone", userPhone);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
