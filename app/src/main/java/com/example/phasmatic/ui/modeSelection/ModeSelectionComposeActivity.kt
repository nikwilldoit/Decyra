package com.example.phasmatic.ui.modeSelection

import android.content.Intent
import android.os.Bundle
import android.graphics.Bitmap
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.phasmatic.extras.ProfileImageManager
import com.example.phasmatic.ui.Chat.users_to_chat.UsersActivity
import com.example.phasmatic.ui.Forum.forum.ForumActivity
import com.example.phasmatic.ui.Profile_Menu.account_settings.AccountActivity
import com.example.phasmatic.ui.questionnaire.QuestionnaireActivity
import com.example.phasmatic.ui.conference.general_conference.GeneralConferenceActivity
import com.example.phasmatic.ui.login.LoginActivity
import com.example.phasmatic.ui.notes.Notes.NotesActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ModeSelectionComposeActivity : AppCompatActivity() {

    private val firebaseDb: FirebaseDatabase = FirebaseDatabase.getInstance(
        "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
    )

    private val usersRef: DatabaseReference = firebaseDb.getReference("users")

    private var profileUrl by mutableStateOf<String?>(null)
    private var profileBitmap by mutableStateOf<Bitmap?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("userId")
        val userFullName = intent.getStringExtra("userFullName")
        val userEmail = intent.getStringExtra("userEmail")
        val userPhone = intent.getStringExtra("userPhone")

        loadProfileImage(userId) { url, bitmap ->
            profileUrl = url
            profileBitmap = bitmap
        }

        setContent {
            ModeSelectionScreen(
                userId = userId,
                userFullName = userFullName,
                userEmail = userEmail,
                userPhone = userPhone,
                profileImageUrl = profileUrl,
                profileBitmap = profileBitmap,

                onModeSelected = { mode ->
                    startActivity(
                        Intent(this, QuestionnaireActivity::class.java).apply {
                            putUserExtras(userId, userFullName, userEmail, userPhone)
                            putExtra("modeType", mode)
                        }
                    )
                },

                onForumClick = {
                    startActivity(
                        Intent(this, ForumActivity::class.java).apply {
                            putUserExtras(userId, userFullName, userEmail, userPhone)
                        }
                    )
                    finish()
                },

                onProfileClick = {
                    startActivity(
                        Intent(this, AccountActivity::class.java).apply {
                            putUserExtras(userId, userFullName, userEmail, userPhone)
                        }
                    )
                },

                onChatClick = {
                    startActivity(
                        Intent(this, UsersActivity::class.java).apply {
                            putUserExtras(userId, userFullName, userEmail, userPhone)
                        }
                    )
                    finish()
                },

                onConferenceClick = {
                    startActivity(
                        Intent(this, GeneralConferenceActivity::class.java).apply {
                            putUserExtras(userId, userFullName, userEmail, userPhone)
                        }
                    )
                    finish()
                },

                onNotesClick = {
                    startActivity(
                        Intent(this, NotesActivity::class.java).apply {
                            putUserExtras(userId, userFullName, userEmail, userPhone)
                        }
                    )
                    finish()
                },

                onLogoutClick = {
                    logout(userId)
                }
            )
        }
    }

    private fun Intent.putUserExtras(
        userId: String?,
        userFullName: String?,
        userEmail: String?,
        userPhone: String?
    ): Intent {
        putExtra("userId", userId)
        putExtra("userFullName", userFullName)
        putExtra("userEmail", userEmail)
        putExtra("userPhone", userPhone)
        return this
    }

    private fun logout(userId: String?) {
        if (userId.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        usersRef.child(userId).child("remember").setValue(0)
            .addOnCompleteListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }

    private fun loadProfileImage(
        userId: String?,
        onResult: (String?, Bitmap?) -> Unit
    ) {
        if (userId.isNullOrEmpty()) {
            onResult(null, null)
            return
        }

        usersRef.child(userId).get()
            .addOnSuccessListener { snapshot ->
                val profileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)

                if (!profileImageUrl.isNullOrEmpty()) {
                    val displayUrl = "$profileImageUrl?t=${System.currentTimeMillis()}"
                    onResult(displayUrl, null)
                } else {
                    val bitmap = ProfileImageManager.loadBitmap(this, userId)
                    onResult(null, bitmap)
                }
            }
            .addOnFailureListener {
                val bitmap = ProfileImageManager.loadBitmap(this, userId)
                onResult(null, bitmap)
            }
    }
}