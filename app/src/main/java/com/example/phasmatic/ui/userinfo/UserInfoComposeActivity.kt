package com.example.phasmatic.ui.userinfo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.phasmatic.data.model.UserInfo
import com.example.phasmatic.extras.InternetConnection
import com.example.phasmatic.ui.login.LoginActivity
import com.example.phasmatic.ui.modeSelection.ModeSelectionActivity
import com.google.firebase.database.*

class UserInfoComposeActivity : AppCompatActivity() {
    private lateinit var userInfoRef: DatabaseReference
    private lateinit var universitiesRef: DatabaseReference

    private var userId: String? = null
    private var userFullName: String? = null
    private var userEmail: String? = null
    private var userPhone: String? = null
    private var hasUserInfo: Boolean = false

    private val inter = InternetConnection()

    var selectedUniversity by mutableStateOf("")
    var academicLevel by mutableStateOf("")
    var languages by mutableStateOf("")
    var gpa by mutableStateOf("")
    var field by mutableStateOf("")
    var budget by mutableStateOf("")
    var selectedYear by mutableStateOf("1")
    var advisorType by mutableStateOf<String?>("male")

    var universities by mutableStateOf(listOf<String>())
    var isLoading by mutableStateOf(true)
    var isSaving by mutableStateOf(false)
    var infoMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!inter.isConnected(this)) {
            inter.showCustomDialog(this)
        }

        val intent = intent
        userId = intent.getStringExtra("userId")
        userFullName = intent.getStringExtra("userFullName")
        userEmail = intent.getStringExtra("userEmail")
        userPhone = intent.getStringExtra("userPhone")
        hasUserInfo = intent.getBooleanExtra("hasUserInfo", false)

        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "Missing user, redirecting to login", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val db = FirebaseDatabase.getInstance(
            "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        )
        userInfoRef = db.getReference("user_info")
        universitiesRef = db.getReference("universities")

        loadUniversities()
        prefillUserInfo()

        setContent {
            UserInfoScreen(
                userFullName = userFullName,
                universities = universities,
                selectedUniversity = selectedUniversity,
                academicLevel = academicLevel,
                languages = languages,
                gpa = gpa,
                field = field,
                budget = budget,
                selectedYear = selectedYear,
                //advisorType = advisorType,
                isLoading = isLoading,
                isSaving = isSaving,
                infoMessage = infoMessage,
                onUniversityChange = { selectedUniversity = it },
                onAcademicLevelChange = { academicLevel = it },
                onLanguagesChange = { languages = it },
                onGpaChange = { gpa = it },
                onFieldChange = { field = it },
                onBudgetChange = { budget = it },
                onYearChange = { selectedYear = it },
                onAdvisorSelected = { advisorType = it },
                onSaveClick = { saveUserInfo() }
            )
        }
    }

    private fun loadUniversities() {
        universitiesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                for (child in snapshot.children) {
                    val name = child.child("name").getValue(String::class.java)
                    val country = child.child("country").getValue(String::class.java)
                    if (name != null && country == "Ελλάδα") {
                        list.add(name)
                    }
                }
                universities = list
                if (selectedUniversity.isBlank() && list.isNotEmpty()) {
                    selectedUniversity = list.first()
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                Toast.makeText(
                    this@UserInfoComposeActivity,
                    "Failed to load universities",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun prefillUserInfo() {
        val uid = userId ?: return

        userInfoRef.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) return

                    val info = snapshot.getValue(UserInfo::class.java) ?: return

                    selectedUniversity = info.university ?: selectedUniversity
                    academicLevel = info.academicLevel ?: ""
                    languages = info.languages ?: ""
                    gpa = info.gpa ?: ""
                    field = info.field ?: ""
                    budget = info.budgetPerYear?.toString() ?: ""
                    selectedYear = info.yearOfStudies?.toString() ?: "1"

                    advisorType = info.advisorType ?: "male"
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun saveUserInfo() {
        val uid = userId
        if (uid.isNullOrEmpty()) {
            Toast.makeText(this, "User id missing", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedUniversity.isBlank() ||
            academicLevel.isBlank() ||
            languages.isBlank() ||
            gpa.isBlank() ||
            field.isBlank() ||
            budget.isBlank() ||
            selectedYear.isBlank()
        ) {
            infoMessage = "Please fill all fields"
            return
        }

        val budgetValue = budget.toDoubleOrNull()
        val yearValue = selectedYear.toIntOrNull()

        if (budgetValue == null || yearValue == null) {
            infoMessage = "Invalid numeric values"
            return
        }

        val advisor = advisorType ?: "male"

        val advisorImage = when (advisor) {
            "male" -> "male.png"
            "female" -> "female.png"
            else -> "robot.png"
        }

        isSaving = true
        infoMessage = null

        val info = UserInfo(
            uid,
            selectedUniversity,
            academicLevel,
            languages,
            gpa,
            field,
            budgetValue,
            yearValue,
            advisor,
            advisorImage
        )

        userInfoRef.child(uid)
            .setValue(info)
            .addOnSuccessListener {
                isSaving = false
                Toast.makeText(this, "Info saved", Toast.LENGTH_SHORT).show()

                val i = Intent(this, ModeSelectionActivity::class.java)
                i.putExtra("userId", uid)
                i.putExtra("userFullName", userFullName)
                i.putExtra("userEmail", userEmail)
                i.putExtra("userPhone", userPhone)
                startActivity(i)
                finish()
            }
            .addOnFailureListener { e ->
                isSaving = false
                infoMessage = "Save failed: ${e.message}"
            }
    }
}