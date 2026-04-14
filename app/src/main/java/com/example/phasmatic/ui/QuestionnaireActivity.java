package com.example.phasmatic.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.data.model.UserExpectation;
import com.example.phasmatic.extras.InternetConnection;
import com.example.phasmatic.ui.Profile_Menu.ProfileMenuHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.graphics.Bitmap;
import com.example.phasmatic.extras.ProfileImageManager;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireActivity extends AppCompatActivity {

    private TextView txtProgress, txtQuestion;
    private Button btnPrev, btnNext, btnVoice;
    private ProgressBar progressQuestions;
    private TextView txtModeTitle;
    private ImageView[] stepDots;

    private ImageButton btnBack;
    private ImageView imgProfile;
    private ProfileMenuHelper profileMenuHelper;
    private BackButtonHelper backButtonHelper;

    private List<String> itFieldNames = new ArrayList<>();
    private List<Integer> itFieldIds = new ArrayList<>();
    private Integer selectedFieldId = null;
    private DatabaseReference itFieldsRef;
    private DatabaseReference careerRef;


    private String userId, userFullName, userEmail, userPhone, modeType;

    private EditText edtAnswer;
    private Spinner spnItField, spnAnswers;

    private List<QuestionItem> questions = new ArrayList<>();
    private List<String> answers = new ArrayList<>();
    private List<String> currentAnswerOptions = new ArrayList<>();

    private DatabaseReference answersRef;


    private int currentIndex = 0;

    private DatabaseReference expectationsRef;
    private DatabaseReference questionsRef;
    private DatabaseReference usersRef;
    private DatabaseReference usersInfoRef;

    private String careerQuestion2Template;
    private InternetConnection inter = new InternetConnection();


    private static class QuestionItem {
        long questionId;
        String text;

        QuestionItem(long questionId, String text) {
            this.questionId = questionId;
            this.text = text;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);


        if(!inter.isConnected(this)){
            inter.showCustomDialog(this);
        }


        txtModeTitle = findViewById(R.id.txtModeTitle);
        progressQuestions = findViewById(R.id.progressQuestions);

        spnAnswers = findViewById(R.id.spnAnswers);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");
        modeType = intent.getStringExtra("modeType");

        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);

        btnVoice = findViewById(R.id.btnVoice);

        spnItField = findViewById(R.id.spnItField);

        BackButtonHelper.attachToGoModeSelection(
                this,
                R.id.btnBack,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        usersRef = firebaseDb.getReference("users");

        usersInfoRef = firebaseDb.getReference("user_info");

        itFieldsRef = firebaseDb.getReference("it_fields");
        careerRef = firebaseDb.getReference("career");

        profileMenuHelper = new ProfileMenuHelper(
                this,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));
        loadProfilePhoto();


        ImageView step1 = findViewById(R.id.step1);
        ImageView step2 = findViewById(R.id.step2);
        ImageView step3 = findViewById(R.id.step3);
        ImageView step4 = findViewById(R.id.step4);
        ImageView step5 = findViewById(R.id.step5);

        stepDots = new ImageView[]{step1, step2, step3, step4, step5};

        if ("erasmus".equals(modeType)) {
            txtModeTitle.setText("Erasmus questionnaire");
        } else if ("master".equals(modeType)) {
            txtModeTitle.setText("Master questionnaire");
        } else if ("career".equals(modeType)) {
            txtModeTitle.setText("Study - Work advisor questionnaire");
            loadItFields();
        }

        for (int i = 0; i < stepDots.length; i++) {
            final int index = i;
            stepDots[i].setOnClickListener(v -> {
                if (index < questions.size()) {
                    saveCurrentAnswer();
                    currentIndex = index;
                    updateUI();
                }
            });
        }

        txtProgress = findViewById(R.id.txtProgress);
        txtQuestion = findViewById(R.id.txtQuestion);
        edtAnswer = findViewById(R.id.edtAnswer);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        expectationsRef = db.getReference("user_expectations");
        questionsRef = db.getReference("questionnaire_questions");
        answersRef = db.getReference("questionnaire_answers");


        loadQuestionsFromDb(modeType);

        btnPrev.setOnClickListener(v -> {
            saveCurrentAnswer();
            if (currentIndex > 0) {
                currentIndex--;
                updateUI();
            }
        });

        btnNext.setOnClickListener(v -> {
            saveCurrentAnswer();
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                updateUI();
            } else {
                saveExpectationsAndGoChat();
            }
        });

        btnVoice.setOnClickListener(v -> startSpeechRecognizer());
    }

    private void loadItFields() {
        itFieldsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                itFieldNames.clear();
                itFieldIds.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer id   = child.child("id").getValue(Integer.class);
                    String name  = child.child("name").getValue(String.class);
                    if (id != null && name != null) {
                        itFieldIds.add(id);
                        itFieldNames.add(name);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        QuestionnaireActivity.this,
                        android.R.layout.simple_spinner_item,
                        itFieldNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnItField.setAdapter(adapter);

                spnItField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedFieldId = itFieldIds.get(position);
                        if ("career".equals(modeType) && currentIndex == 1) {
                            updateCareerSecondQuestion();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuestionnaireActivity.this,
                        "Failed to load IT fields: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void updateUI() {
        if (questions.isEmpty()) return;

        QuestionItem currentQuestion = questions.get(currentIndex);

        txtQuestion.setText(currentQuestion.text);
        txtProgress.setText((currentIndex + 1) + " / " + questions.size());

        progressQuestions.setMax(questions.size());
        progressQuestions.setProgress(currentIndex + 1);

        edtAnswer.setVisibility(View.GONE);
        spnItField.setVisibility(View.GONE);
        spnAnswers.setVisibility(View.GONE);
        btnVoice.setEnabled(false);

        if ("career".equals(modeType) && currentIndex == 0) {
            spnItField.setVisibility(View.VISIBLE);
            btnVoice.setEnabled(false);
        } else {
            loadAnswersForQuestion(currentQuestion.questionId);
        }

        btnPrev.setEnabled(currentIndex > 0);
        btnNext.setText(currentIndex == questions.size() - 1 ? "Finish" : "Next");

        for (int i = 0; i < stepDots.length; i++) {
            if (i < questions.size()) {
                stepDots[i].setVisibility(View.VISIBLE);
                stepDots[i].setImageResource(
                        i == currentIndex ? R.drawable.ic_step_active : R.drawable.ic_step_inactive
                );
            } else {
                stepDots[i].setVisibility(View.GONE);
            }
        }

        if ("career".equals(modeType) && currentIndex == 1) {
            updateCareerSecondQuestion();
        }
    }

    private void loadAnswersForQuestion(long questionId) {
        answersRef.orderByChild("question_id")
                .equalTo(questionId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentAnswerOptions.clear();

                        for (DataSnapshot child : snapshot.getChildren()) {

                            //elegxos gia mode_type
                            String answerMode = child.child("mode_type").getValue(String.class);
                            if (answerMode == null || !answerMode.equals(modeType)) {
                                continue;   //skip andiseis alou mode_type
                            }

                            String a1 = child.child("answer1").getValue(String.class);
                            String a2 = child.child("answer2").getValue(String.class);
                            String a3 = child.child("answer3").getValue(String.class);
                            String a4 = child.child("answer4").getValue(String.class);

                            if (a1 != null && !a1.trim().isEmpty()) currentAnswerOptions.add(a1);
                            if (a2 != null && !a2.trim().isEmpty()) currentAnswerOptions.add(a2);
                            if (a3 != null && !a3.trim().isEmpty()) currentAnswerOptions.add(a3);
                            if (a4 != null && !a4.trim().isEmpty()) currentAnswerOptions.add(a4);
                        }

                        if (!currentAnswerOptions.isEmpty()) {
                            spnAnswers.setVisibility(View.VISIBLE);
                            edtAnswer.setVisibility(View.GONE);
                            btnVoice.setEnabled(false);

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    QuestionnaireActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    currentAnswerOptions
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnAnswers.setAdapter(adapter);

                            String savedAnswer = answers.get(currentIndex);
                            if (savedAnswer != null && !savedAnswer.isEmpty()) {
                                int pos = currentAnswerOptions.indexOf(savedAnswer);
                                if (pos >= 0) {
                                    spnAnswers.setSelection(pos);
                                }
                            }
                        } else {
                            spnAnswers.setVisibility(View.GONE);
                            edtAnswer.setVisibility(View.VISIBLE);
                            btnVoice.setEnabled(true);
                            edtAnswer.setText(answers.get(currentIndex));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(QuestionnaireActivity.this,
                                "Failed to load answers: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCareerSecondQuestion() {
        Log.d("CAREER_DEBUG", "updateCareerSecondQuestion called. mode=" + modeType
                + " currentIndex=" + currentIndex + " selectedFieldId=" + selectedFieldId);

        if (!"career".equals(modeType) || selectedFieldId == null || currentIndex != 1) {
            return;
        }

        if (careerQuestion2Template == null) {
            careerQuestion2Template = questions.get(1).text;
        }

        final String original = careerQuestion2Template;
        Log.d("CAREER_DEBUG", "Original q2 template = " + original);

        careerRef.orderByChild("field_id")
                .equalTo(selectedFieldId.longValue())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Log.d("CAREER_DEBUG", "career children: " + snapshot.getChildrenCount());
                        Long salary = null;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Long fieldId   = child.child("field_id").getValue(Long.class);
                            String country = child.child("country_name").getValue(String.class);
                            Log.d("CAREER_DEBUG", "row: fieldId=" + fieldId + " country=" + country);

                            if (country != null && !country.isEmpty()) {
                                salary = child.child("avg_salary_no_master").getValue(Long.class);
                                break;
                            }
                        }

                        if (salary != null) {
                            String replaced = original
                                    .replace("...", String.valueOf(salary))
                                    .replace("…", String.valueOf(salary));
                            Log.d("CAREER_DEBUG", "replaced q2 = " + replaced);
                            questions.get(1).text = replaced;
                            if (currentIndex == 1) {
                                txtQuestion.setText(replaced);
                            }
                        } else {
                            Log.d("CAREER_DEBUG", "NO salary found for fieldId=" + selectedFieldId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("CAREER_DEBUG", "Failed: " + error.getMessage());
                    }
                });
    }

    private void saveCurrentAnswer() {
        String ans = "";

        if ("career".equals(modeType) && currentIndex == 0 && spnItField.getVisibility() == View.VISIBLE) {
            ans = (spnItField.getSelectedItem() != null)
                    ? spnItField.getSelectedItem().toString()
                    : "";
        } else if (spnAnswers.getVisibility() == View.VISIBLE) {
            ans = (spnAnswers.getSelectedItem() != null)
                    ? spnAnswers.getSelectedItem().toString()
                    : "";
        } else if (edtAnswer.getVisibility() == View.VISIBLE) {
            ans = edtAnswer.getText().toString().trim();
        }

        answers.set(currentIndex, ans);
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
                //fallback se local cache an uparxei
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
            edtAnswer.setText(text.get(0));

            Log.i("DEMO-ANSWER", text.get(0));

        } else {
            System.out.println("Recognizer API error");
        }
    }

    private void loadQuestionsFromDb(String mode) {
        questions.clear();
        answers.clear();

        questionsRef.orderByChild("mode_type")
                .equalTo(mode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        class QItem {
                            long order;
                            String text;

                            QItem(long order, String text) {
                                this.order = order;
                                this.text = text;
                            }
                        }

                        List<QItem> tmp = new ArrayList<>();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            Object activeObj = child.child("is_active").getValue();
                            boolean active = true;

                            if (activeObj instanceof Boolean) {
                                active = (Boolean) activeObj;
                            } else if (activeObj instanceof Long) {
                                active = ((Long) activeObj) != 0;
                            }

                            if (!active) continue;

                            Long questionId = child.child("question_id").getValue(Long.class);
                            String q = child.child("question").getValue(String.class);

                            if (questionId != null && q != null && !q.trim().isEmpty()) {
                                tmp.add(new QItem(questionId, q));
                            }
                        }

                        int n = tmp.size();
                        for (int i = 0; i < n - 1; i++) {
                            for (int j = 0; j < n - 1 - i; j++) {
                                if (tmp.get(j).order > tmp.get(j + 1).order) {
                                    QItem t = tmp.get(j);
                                    tmp.set(j, tmp.get(j + 1));
                                    tmp.set(j + 1, t);
                                }
                            }
                        }

                        for (QItem qi : tmp) {
                            questions.add(new QuestionItem(qi.order, qi.text));
                            answers.add("");
                        }

                        currentIndex = 0;

                        if ("career".equals(modeType) && questions.size() > 1) {
                            careerQuestion2Template = questions.get(1).text;
                        }

                        if (questions.isEmpty()) {
                            Toast.makeText(QuestionnaireActivity.this,
                                    "No questions configured for " + mode,
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            updateUI();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(QuestionnaireActivity.this,
                                "Failed to load questions: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


    private void saveExpectationsAndGoChat() {

        StringBuilder sb = new StringBuilder();

        if ("erasmus".equals(modeType)) {
            sb.append("Ψάχνω για πρόγραμμα Erasmus που να ταιριάζει σε μένα. ");
        } else if ("master".equals(modeType)) {
            sb.append("Ψάχνω για πρόγραμμα μεταπτυχιακού (Master) που να ταιριάζει σε μένα. ");
        } else if ("career".equals(modeType)) {
            sb.append("Ψάχνω για συμβουλές που να μου ταιριάζουν. ");
        }

        sb.append("Με βάση τα παρακάτω στοιχεία θέλω να μου προτείνεις τις πιο κατάλληλες επιλογές:\n\n");

        usersInfoRef.child("gpa").get().addOnCompleteListener(task -> {
            String gpa = "Unknown";
            if (task.isSuccessful() && task.getResult() != null) {
                gpa = String.valueOf(task.getResult().getValue());
            }

            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < questions.size(); i++) {
                if (!answers.get(i).isEmpty()) {
                    String f = formatAnswer(modeType, i, answers.get(i));
                    if (!f.isEmpty()) {
                        formatted.append(f).append('\n');
                    }
                }
            }

            formatted.append("GPA: ").append(gpa).append("\n");
            sb.append("- ").append(formatted).append("\n");

            if ("erasmus".equals(modeType)) {
                sb.append("\nΔώσε 10 προγράμματα Erasmus που ταιριάζουν στο προφίλ. ");
                sb.append("Μόνο όνομα πανεπιστημίου, χώρα και 1 σύντομη φράση. Χωρίς ανάλυση.");
            } else if ("master".equals(modeType)) {
                sb.append("\nΔώσε 10 προγράμματα μεταπτυχιακών που ταιριάζουν στο προφίλ. ");
                sb.append("Μόνο όνομα προγράμματος, πανεπιστήμιο, χώρα και 1 σύντομη φράση. Χωρίς ανάλυση.");
            } else if ("career".equals(modeType)) {
                sb.append("\nΔώσε 5 σύντομες προτάσεις καριέρας που ταιριάζουν στο προφίλ. ");
                sb.append("Μόνο τίτλο και 1 γραμμή εξήγησης.");
            }

            String expectationsText = sb.toString().trim();

            String id = expectationsRef.push().getKey();
            if (id == null) {
                Toast.makeText(this, "Failed to create expectation id", Toast.LENGTH_SHORT).show();
                return;
            }

            UserExpectation expectation = new UserExpectation(
                    id,
                    userId,
                    modeType,
                    expectationsText
            );

            expectationsRef.child(id)
                    .setValue(expectation)
                    .addOnSuccessListener(unused -> {
                        Intent i;
                        if ("erasmus".equals(modeType)) {
                            i = new Intent(QuestionnaireActivity.this, ErasmusChatActivity.class);
                        }
                        else if ("master".equals(modeType)) {
                            i = new Intent(QuestionnaireActivity.this, MasterChatActivity.class);
                        }
                        else {
                            i = new Intent(QuestionnaireActivity.this, CareerChatActivity.class);
                        }
                        i.putExtra("userId", userId);
                        i.putExtra("userFullName", userFullName);
                        i.putExtra("userEmail", userEmail);
                        i.putExtra("userPhone", userPhone);
                        i.putExtra("userExpectations", expectationsText);
                        startActivity(i);
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }

    private String formatAnswer(String mode, int index, String answer) {

        if ("erasmus".equals(mode)) {
            switch (index) {
                case 0:
                    return "Preferred Location: " + answer;
                case 1:
                    return "Cost Sensitivity: " + answer;
                case 2:
                    return "City Type: " + answer;
                case 3:
                    return "Experience Preference: " + answer;
                case 4:
                    return "Language Preference: " + answer;
                default:
                    return "";
            }
        }

        else if ("master".equals(mode)) {
            switch (index) {
                case 0:
                    return "Field & Career Goal: " + answer;
                case 1:
                    return "Preferred Location: " + answer;
                case 2:
                    return "Priority: " + answer;
                case 3:
                    return "Budget: " + answer;
                case 4:
                    return "Program Type: " + answer;
                default:
                    return "";
            }
        }

        return "";
    }
}