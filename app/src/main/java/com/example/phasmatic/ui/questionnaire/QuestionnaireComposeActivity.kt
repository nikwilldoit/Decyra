package com.example.phasmatic.ui.questionnaire

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import com.example.phasmatic.data.model.UserExpectation
import com.example.phasmatic.extras.InternetConnection
import com.example.phasmatic.extras.ProfileImageManager
import com.example.phasmatic.ui.Chat.users_to_chat.UsersActivity
import com.example.phasmatic.ui.Profile_Menu.account_settings.AccountActivity
import com.example.phasmatic.ui.career.CareerChatComposeActivity
import com.example.phasmatic.ui.conference.general_conference.GeneralConferenceActivity
import com.example.phasmatic.ui.erasmus.ErasmusChatComposeActivity
import com.example.phasmatic.ui.login.LoginActivity
import com.example.phasmatic.ui.master.MasterChatComposeActivity
import com.example.phasmatic.ui.notes.Notes.NotesActivity
import com.google.firebase.database.*
import java.util.Locale

class QuestionnaireComposeActivity : AppCompatActivity() {

    private val inter = InternetConnection()
    private lateinit var db: FirebaseDatabase
    private lateinit var questionsRef: DatabaseReference
    private lateinit var answersRef: DatabaseReference
    private lateinit var expectationsRef: DatabaseReference
    private lateinit var usersRef: DatabaseReference
    private lateinit var usersInfoRef: DatabaseReference
    private lateinit var itFieldsRef: DatabaseReference
    private lateinit var careerRef: DatabaseReference

    // State Variables
    private var userId by mutableStateOf<String?>(null)
    private var userFullName by mutableStateOf<String?>(null)
    private var userEmail by mutableStateOf<String?>(null)
    private var userPhone by mutableStateOf<String?>(null)
    private var modeType by mutableStateOf("")
    private var currentIndex by mutableStateOf(0)
    private val questionsList = mutableStateListOf<QuestionItem>()
    private val userAnswers = mutableStateListOf<String>()
    private val currentAnswerOptions = mutableStateListOf<String>()
    private val itFieldNames = mutableStateListOf<String>()
    private val itFieldIds = mutableStateListOf<Int>()

    private var selectedFieldId by mutableStateOf<Int?>(null)
    private var isLoading by mutableStateOf(true)
    private var textAnswer by mutableStateOf("")
    private var selectedAnswerIndex by mutableStateOf<Int?>(null)
    private var careerQuestion2Template: String? = null
    private var userLanguages = "Unknown"
    private var profileImageUrl by mutableStateOf<String?>(null)
    private var profileBitmap by mutableStateOf<Bitmap?>(null)
    private var menuExpanded by mutableStateOf(false)

    data class QuestionItem(val questionId: Long, var text: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = intent.getStringExtra("userId")
        modeType = intent.getStringExtra("modeType") ?: "career"
        userFullName = intent.getStringExtra("userFullName")
        userEmail = intent.getStringExtra("userEmail")
        userPhone = intent.getStringExtra("userPhone")
        db = FirebaseDatabase.getInstance("https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app")
        questionsRef = db.getReference("questionnaire_questions")
        answersRef = db.getReference("questionnaire_answers")
        expectationsRef = db.getReference("user_expectations")
        usersRef = db.getReference("users")
        usersInfoRef = db.getReference("user_info")
        itFieldsRef = db.getReference("it_fields")
        careerRef = db.getReference("career")

        loadInitialData()

        setContent {
            QuestionnaireScreen(
                modeType = modeType,
                currentIndex = currentIndex,
                questions = questionsList,
                answers = userAnswers,
                currentAnswerOptions = currentAnswerOptions,
                itFieldNames = itFieldNames,
                selectedFieldId = selectedFieldId,
                selectedAnswerIndex = selectedAnswerIndex,
                textAnswer = textAnswer,
                isLoading = isLoading,
                profileImageUrl = profileImageUrl,
                profileBitmap = profileBitmap,
                menuExpanded = menuExpanded,
                onBackClick = { finish() },
                onPrevClick = { handlePrev() },
                onNextClick = { handleNext() },
                onVoiceClick = { startVoiceInput() },
                onStepClick = { index -> if (index < questionsList.size) { saveCurrentAnswer(); currentIndex = index; updateUI() } },
                onItFieldSelected = { index ->
                    selectedFieldId = itFieldIds[index]
                    selectedAnswerIndex = index
                },
                onAnswerSelected = { index -> selectedAnswerIndex = index },
                onTextAnswerChange = { textAnswer = it },
                onProfileClick = { menuExpanded = true },
                onMenuDismiss = { menuExpanded = false },
                onAccountClick = { startActivity(Intent(this, AccountActivity::class.java)) },
                onChatClick = { startActivity(Intent(this, UsersActivity::class.java)) },
                onConferenceClick = { startActivity(Intent(this, GeneralConferenceActivity::class.java)) },
                onNotesClick = { startActivity(Intent(this, NotesActivity::class.java)) },
                onLogoutClick = { handleLogout() }
            )
        }
    }

    private fun loadInitialData() {
        isLoading = true
        userId?.let { id ->
            usersInfoRef.child(id).child("languages").get().addOnSuccessListener {
                userLanguages = it.getValue(String::class.java) ?: "Unknown"
            }
            usersRef.child(id).get().addOnSuccessListener {
                profileImageUrl = it.child("profileImageUrl").getValue(String::class.java)
                if (profileImageUrl.isNullOrEmpty()) profileBitmap = ProfileImageManager.loadBitmap(this, id)
            }
        }

        if (modeType == "career") {
            itFieldsRef.get().addOnSuccessListener { s ->
                itFieldNames.clear(); itFieldIds.clear()
                for (c in s.children) {
                    val name = c.child("name").getValue(String::class.java)
                    val id = c.child("id").getValue(Int::class.java)
                    if (name != null && id != null) { itFieldNames.add(name); itFieldIds.add(id) }
                }
            }
        }
        loadQuestionsFromDb(modeType)
    }

    private fun loadQuestionsFromDb(mode: String) {
        questionsRef.orderByChild("mode_type").equalTo(mode).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tmp = mutableListOf<QuestionItem>()
                for (child in snapshot.children) {
                    val active = child.child("is_active").getValue()
                    if (active == false || active == 0L) continue
                    val qId = child.child("question_id").getValue(Long::class.java)
                    val qText = child.child("question").getValue(String::class.java)
                    if (qId != null && qText != null) tmp.add(QuestionItem(qId, qText))
                }
                tmp.sortBy { it.questionId }
                questionsList.clear(); questionsList.addAll(tmp)
                userAnswers.clear(); userAnswers.addAll(List(tmp.size) { "" })
                if (modeType == "career" && questionsList.size > 1) careerQuestion2Template = questionsList[1].text
                isLoading = false
                if (questionsList.isNotEmpty()) updateUI()
            }
            override fun onCancelled(error: DatabaseError) { isLoading = false }
        })
    }

    private fun updateUI() {
        if (questionsList.isEmpty()) return
        textAnswer = userAnswers[currentIndex]
        selectedAnswerIndex = null
        val qId = questionsList[currentIndex].questionId
        if (!shouldUseTextInput(modeType, qId) && !(modeType == "career" && currentIndex == 0)) {
            loadAnswersForQuestion(qId)
        }
        if (modeType == "career" && currentIndex == 1) updateCareerSecondQuestion()
    }

    private fun loadAnswersForQuestion(qId: Long) {
        currentAnswerOptions.clear()
        answersRef.orderByChild("question_id").equalTo(qId.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    if (child.child("mode_type").getValue(String::class.java) != modeType) continue
                    listOf("answer1", "answer2", "answer3", "answer4").forEach { key ->
                        child.child(key).getValue(String::class.java)?.let { if (it.isNotBlank()) currentAnswerOptions.add(it) }
                    }
                }
                val saved = userAnswers[currentIndex]
                if (saved.isNotEmpty()) selectedAnswerIndex = currentAnswerOptions.indexOf(saved).takeIf { it >= 0 }
            }
            override fun onCancelled(e: DatabaseError) {}
        })
    }

    private fun updateCareerSecondQuestion() {
        val fId = selectedFieldId ?: return
        val template = careerQuestion2Template ?: return
        careerRef.orderByChild("field_id").equalTo(fId.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                var salary: Long? = null
                for (c in s.children) {
                    salary = c.child("avg_salary_no_master").getValue(Long::class.java)
                    if (salary != null) break
                }
                salary?.let {
                    val replaced = template.replace("...", it.toString()).replace("…", it.toString())
                    questionsList[1] = questionsList[1].copy(text = replaced)
                }
            }
            override fun onCancelled(e: DatabaseError) {}
        })
    }

    private fun saveCurrentAnswer() {
        val ans = when {
            modeType == "career" && currentIndex == 0 -> itFieldNames.getOrNull(selectedAnswerIndex ?: -1) ?: ""
            currentAnswerOptions.isNotEmpty() && !shouldUseTextInput(modeType, questionsList[currentIndex].questionId) ->
                currentAnswerOptions.getOrNull(selectedAnswerIndex ?: -1) ?: ""
            else -> textAnswer
        }
        if (currentIndex < userAnswers.size) userAnswers[currentIndex] = ans
    }

    private fun handleNext() {
        saveCurrentAnswer()
        if (currentIndex < questionsList.size - 1) { currentIndex++; updateUI() }
        else saveExpectationsAndGoChat()
    }

    private fun handlePrev() {
        saveCurrentAnswer()
        if (currentIndex > 0) { currentIndex--; updateUI() }
    }

    private fun saveExpectationsAndGoChat() {
        isLoading = true
        val resultText = userAnswers.joinToString("\n")
        val id = expectationsRef.push().key ?: return
        val exp = UserExpectation(id, userId, modeType, resultText)
        expectationsRef.child(id).setValue(exp).addOnSuccessListener {
            val intent = when (modeType) {
                "erasmus" -> Intent(this, ErasmusChatComposeActivity::class.java).apply{
                    putUserExtras(userId, userFullName, userEmail, userPhone)
                }
                "master" -> Intent(this, MasterChatComposeActivity::class.java).apply{
                    putUserExtras(userId, userFullName, userEmail, userPhone)
                }
                else -> Intent(this, CareerChatComposeActivity::class.java).apply{
                    putUserExtras(userId, userFullName, userEmail, userPhone)
                }
            }
            intent.putExtra("userExpectations", resultText)
            startActivity(intent); finish()
        }
    }

    private fun Intent.putUserExtras(
        userId: String?,
        userFullName: String?,
        userEmail: String?,
        userPhone: String?
    ): Intent{
        putExtra("userId", userId)
        putExtra("userFullName", userFullName)
        putExtra("userEmail", userEmail)
        putExtra("userPhone", userPhone)
        return this
    }



    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "el-GR")
        }
        try { startActivityForResult(intent, 3000) } catch (e: Exception) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3000 && resultCode == Activity.RESULT_OK) {
            textAnswer = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
        }
    }

    private fun handleLogout() { startActivity(Intent(this, LoginActivity::class.java)); finishAffinity() }
    private fun shouldUseTextInput(mode: String, qId: Long) = (mode == "career" && qId == 4L) || (mode == "master" && qId == 6L)
}