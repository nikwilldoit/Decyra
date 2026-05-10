package com.example.phasmatic.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.phasmatic.ui.forget.ForgetActivity
import com.example.phasmatic.ui.register.RegisterActivity
import com.example.phasmatic.data.model.User
import com.example.phasmatic.ui.userinfo.UserInfoActivity
import com.example.phasmatic.ui.modeSelection.ModeSelectionActivity
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import org.tensorflow.lite.Interpreter
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Locale

class LoginComposeActivity : ComponentActivity() {

    private lateinit var usersRef: DatabaseReference
    private lateinit var usersFaceRef: DatabaseReference
    private lateinit var userInfoRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    private var imageCapture: ImageCapture? = null
    private var tflite: Interpreter? = null

    private val CAMERA_PERMISSION_CODE = 100

    private var authenticatedUserId: String? = null
    private var authenticatedUser: User? = null

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var infoMessage by mutableStateOf<String?>(null)
    var faceLoginEnabled by mutableStateOf(false)
    var showCameraPreview by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    private lateinit var viewFinder: PreviewView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseDb = FirebaseDatabase.getInstance(
            "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        )

        usersRef = firebaseDb.getReference("users")
        usersFaceRef = firebaseDb.getReference("users_face_embedding")
        userInfoRef = firebaseDb.getReference("user_info")
        mAuth = FirebaseAuth.getInstance()

        requestNotificationPermissionIfNeeded()
        loadFaceModel()

        setContent {
            LoginScreen(
                email = email,
                password = password,
                infoMessage = infoMessage,
                faceLoginEnabled = faceLoginEnabled,
                isLoading = isLoading,
                showCameraPreview = showCameraPreview,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onLoginClick = { attemptLogin() },
                onRegisterClick = {
                    startActivity(Intent(this, RegisterActivity::class.java))
                },
                onForgotPasswordClick = {
                    startActivity(Intent(this, ForgetActivity::class.java))
                },
                onFaceLoginClick = {
                    checkCameraPermission()
                },
                cameraPreview = {
                    androidx.compose.ui.viewinterop.AndroidView(
                        factory = { context ->
                            PreviewView(context).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                implementationMode = PreviewView.ImplementationMode.COMPATIBLE

                                viewFinder = this
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                },
                onCaptureClick = {
                    takePhoto()
                },
                onBackFromCamera = {
                    showCameraPreview = false
                }
            )
        }
    }

    private fun attemptLogin() {
        if (email.isBlank() || password.isBlank()) {
            infoMessage = "Enter email and password"
            return
        }
        loginWithFirebase(email.trim(), password.trim())
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }

    private fun loadFaceModel() {
        try {
            val isr: InputStream = assets.open("facenet.tflite")
            val model = ByteArray(isr.available())
            isr.read(model)
            isr.close()

            val buffer = ByteBuffer.allocateDirect(model.size)
                .order(ByteOrder.nativeOrder())
            buffer.put(model)
            buffer.rewind()

            tflite = Interpreter(buffer)
        } catch (e: Exception) {
            Toast.makeText(this, "Model load failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginWithFirebase(email: String, password: String) {
        isLoading = true
        infoMessage = null

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    isLoading = false
                    infoMessage = "Incorrect credentials"
                    return@addOnCompleteListener
                }

                val uid = mAuth.currentUser?.uid ?: run {
                    isLoading = false
                    infoMessage = "User not found"
                    return@addOnCompleteListener
                }

                usersRef.child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)

                            if (user == null) {
                                isLoading = false
                                infoMessage = "User profile not found"
                                return
                            }

                            usersRef.child(uid).child("password").setValue(password)

                            authenticatedUserId = uid
                            authenticatedUser = user

                            FirebaseMessaging.getInstance().token
                                .addOnSuccessListener { token ->
                                    FirebaseDatabase.getInstance(
                                        "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
                                    ).getReference("users")
                                        .child(uid)
                                        .child("fcmToken")
                                        .setValue(token)
                                }

                            if (email == "admin@admin.com" && password == "admin1") {
                                isLoading = false
                                openNextActivity()
                                return
                            }

                            isLoading = false
                            infoMessage = "Password OK. Scan your face."
                            faceLoginEnabled = true
                        }

                        override fun onCancelled(error: DatabaseError) {
                            isLoading = false
                            infoMessage = "Database error"
                        }
                    })
            }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun startCamera() {
        showCameraPreview = true

        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(viewFinder.surfaceProvider)

                imageCapture = ImageCapture.Builder().build()

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val capture = imageCapture ?: return

        val timeStamp = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val options = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ).build()

        capture.takePicture(
            options,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@LoginComposeActivity, "Capture failed", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    try {
                        val uri: Uri = output.savedUri ?: return
                        val stream = contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(stream)
                        stream?.close()

                        val resized = Bitmap.createScaledBitmap(bitmap, 160, 160, true)
                        val embedding = normalize(runModel(resized))

                        loginWithFace(authenticatedUserId, embedding)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    showCameraPreview = false
                }
            }
        )
    }

    private fun loginWithFace(userId: String?, inputEmbedding: FloatArray) {
        if (userId == null) return

        usersFaceRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var bestScore = -1f

                    for (child in snapshot.children) {
                        val embeddings = child.child("faceEmbeddings").value as? List<*> ?: continue

                        for (obj in embeddings) {
                            val vector = obj as? List<*> ?: continue
                            val stored = FloatArray(128)

                            for (i in 0 until 128) {
                                stored[i] = (vector[i] as Number).toFloat()
                            }

                            val sim = cosineSimilarity(inputEmbedding, stored)
                            if (sim > bestScore) bestScore = sim
                        }
                    }

                    if (bestScore > 0.5f) {
                        Toast.makeText(this@LoginComposeActivity, "Face verified", Toast.LENGTH_LONG).show()
                        openNextActivity()
                    } else {
                        Toast.makeText(this@LoginComposeActivity, "Face mismatch", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun openNextActivity() {
        val uid = authenticatedUserId ?: return
        val user = authenticatedUser ?: return

        userInfoRef.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    showRememberDialog(uid) {
                        val i = if (snap.exists()) {
                            Intent(this@LoginComposeActivity, ModeSelectionActivity::class.java)
                        } else {
                            Intent(this@LoginComposeActivity, UserInfoActivity::class.java)
                        }

                        i.putExtra("userId", uid)
                        i.putExtra("userFullName", user.fullName)
                        i.putExtra("userEmail", user.email)
                        i.putExtra("userPhone", user.phoneNumber)

                        startActivity(i)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun showRememberDialog(userid: String, onDone: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Remember login?")
            .setMessage("Do you want to stay logged in on this device?")
            .setPositiveButton("Yes") { _, _ ->
                usersRef.child(userid).child("remember").setValue(1)
                onDone()
            }
            .setNegativeButton("No") { _, _ ->
                usersRef.child(userid).child("remember").setValue(0)
                onDone()
            }
            .setCancelable(false)
            .show()
    }

    private fun runModel(bitmap: Bitmap): FloatArray {
        val buffer = ByteBuffer.allocateDirect(1 * 160 * 160 * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(160 * 160)
        bitmap.getPixels(pixels, 0, 160, 0, 0, 160, 160)

        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF) / 255f
            val g = ((pixel shr 8) and 0xFF) / 255f
            val b = (pixel and 0xFF) / 255f
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }

        val output = Array(1) { FloatArray(128) }
        tflite?.run(buffer, output)
        return output[0]
    }

    private fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        var dot = 0f
        var n1 = 0f
        var n2 = 0f

        for (i in v1.indices) {
            dot += v1[i] * v2[i]
            n1 += v1[i] * v1[i]
            n2 += v2[i] * v2[i]
        }

        return (dot / (Math.sqrt(n1.toDouble()) * Math.sqrt(n2.toDouble()))).toFloat()
    }

    private fun normalize(emb: FloatArray): FloatArray {
        var sum = 0f
        for (v in emb) sum += v * v
        val norm = Math.sqrt(sum.toDouble()).toFloat()
        for (i in emb.indices) emb[i] /= norm
        return emb
    }
}