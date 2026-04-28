package com.example.phasmatic.ui.register

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.phasmatic.data.model.User
import com.example.phasmatic.data.model.User_Face_Embedding
import com.example.phasmatic.ui.login.LoginActivity
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.*

enum class FaceAction {
    CENTER, LOOK_LEFT, LOOK_RIGHT, LOOK_UP, LOOK_DOWN, BLINK, DONE
}

class RegisterComposeActivity : ComponentActivity() {

    private lateinit var usersRef: DatabaseReference
    private lateinit var usersFaceEmbeddingRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var textToSpeech: TextToSpeech
    private var tflite: Interpreter? = null

    private val CAMERA_PERMISSION_CODE = 100

    // Form state
    var fullName by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var phone by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    // Camera state
    var showCameraPreview by mutableStateOf(false)
    var currentAction by mutableStateOf(FaceAction.CENTER)
    var actionProgress by mutableStateOf(0) // Frame count for current action

    private var imageCapture: ImageCapture? = null
    private lateinit var viewFinder: PreviewView

    private var lastCaptureTime = 0L
    private var framesCapturedForAction = 0
    private val FRAMES_PER_ACTION = 3
    private val CENTER_EMBEDDINGS = 3

    private val centerEmbeddings = mutableListOf<List<Double>>()
    private val actionEmbeddings = mutableListOf<List<Double>>()

    private var ttsInitialized = false
    private var centerPromptGiven = false
    private var donePromptGiven = false

    @OptIn(ExperimentalGetImage::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseDb = FirebaseDatabase.getInstance(
            "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        )
        usersRef = firebaseDb.getReference("users")
        usersFaceEmbeddingRef = firebaseDb.getReference("users_face_embedding")
        mAuth = FirebaseAuth.getInstance()

        textToSpeech = TextToSpeech(applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.ENGLISH
                ttsInitialized = true
            }
        }

        loadFaceModel()

        setContent {
            RegisterScreen(
                fullName = fullName,
                email = email,
                password = password,
                phone = phone,
                isLoading = isLoading,
                showCameraPreview = showCameraPreview,
                currentAction = currentAction,
                actionProgress = actionProgress,
                onFullNameChange = { fullName = it },
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onPhoneChange = { phone = it },
                onRegisterClick = { attemptRegister() },
                onLoginClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                onCaptureFaceClick = {
                    showCameraPreview = true
                    checkCameraPermission()
                },
                cameraPreview = {
                    androidx.compose.ui.viewinterop.AndroidView(factory = { context ->
                        PreviewView(context).also { preview ->
                            viewFinder = preview

                        }
                    })
                },
                onBackFromCamera = {
                    showCameraPreview = false
                    currentAction = FaceAction.CENTER
                    centerPromptGiven = false
                    donePromptGiven = false
                }
            )
        }
    }

    private fun loadFaceModel() {
        try {
            val fileDescriptor: AssetFileDescriptor = assets.openFd("facenet.tflite")
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            tflite = Interpreter(
                fileChannel.map(
                    FileChannel.MapMode.READ_ONLY,
                    startOffset,
                    declaredLength
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun attemptRegister() {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Full name, email and password are required", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val finalEmbeddings = buildFinalEmbeddings()
        if (finalEmbeddings.isEmpty()) {
            Toast.makeText(this, "Please capture your face first", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true

        mAuth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser?.uid ?: run {
                        isLoading = false
                        Toast.makeText(this, "User creation failed", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    val faceEmbeddingId = usersFaceEmbeddingRef.push().key ?: run {
                        isLoading = false
                        return@addOnCompleteListener
                    }

                    val user = User(uid, fullName.trim(), email.trim(), password.trim(), phone.trim())
                    val userFaceEmbedding =
                        User_Face_Embedding(faceEmbeddingId, uid, finalEmbeddings, user)

                    usersRef.child(uid).setValue(user)
                        .addOnSuccessListener {
                            usersFaceEmbeddingRef.child(faceEmbeddingId).setValue(userFaceEmbedding)
                                .addOnSuccessListener {
                                    isLoading = false
                                    Toast.makeText(
                                        this,
                                        "Registration successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            Toast.makeText(
                                this,
                                "Error saving user data: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                } else {
                    isLoading = false
                    var msg = "Registration failed"
                    task.exception?.message?.let { raw ->
                        msg = when {
                            raw.contains("Password should be at least 6 characters") ->
                                "Password must be at least 6 characters"
                            else -> raw
                        }
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        }
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(viewFinder.surfaceProvider)

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                    if (imageProxy.image == null) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    val image = InputImage.fromMediaImage(
                        imageProxy.image!!,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    val options = FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build()

                    val detector = FaceDetection.getClient(options)

                    detector.process(image)
                        .addOnSuccessListener { faces ->
                            if (faces.isNotEmpty()) {
                                val face = faces[0]
                                val actionCompleted = updateFaceAction(face)

                                if (actionCompleted) {
                                    val bitmap = viewFinder.bitmap
                                    if (bitmap != null) {
                                        processFrame(bitmap, face)
                                    }
                                }

                                if (currentAction == FaceAction.DONE) {
                                    showCameraPreview = false
                                }
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processFrame(bitmap: Bitmap, face: Face) {
        val now = System.currentTimeMillis()
        if (now - lastCaptureTime < 500) return
        lastCaptureTime = now

        val bounds: Rect = face.boundingBox
        val left = maxOf(bounds.left, 0)
        val top = maxOf(bounds.top, 0)
        val width = minOf(bounds.width(), bitmap.width - left)
        val height = minOf(bounds.height(), bitmap.height - top)

        if (width <= 0 || height <= 0) return

        val faceBitmap = Bitmap.createBitmap(bitmap, left, top, width, height)
        processWithAugmentation(faceBitmap)

        framesCapturedForAction++
        actionProgress = framesCapturedForAction

        if (framesCapturedForAction >= FRAMES_PER_ACTION) {
            framesCapturedForAction = 0
            actionProgress = 0
            currentAction = getNextAction(currentAction)
        }
    }

    private fun updateFaceAction(face: Face): Boolean {
        val yaw = face.headEulerAngleY
        val pitch = face.headEulerAngleX

        return when (currentAction) {
            FaceAction.CENTER -> {
                if (!centerPromptGiven && ttsInitialized) {
                    speak("Look straight")
                    centerPromptGiven = true
                }
                Math.abs(yaw) < 10 && Math.abs(pitch) < 10
            }
            FaceAction.LOOK_LEFT -> yaw > 20
            FaceAction.LOOK_RIGHT -> yaw < -20
            FaceAction.LOOK_UP -> pitch < -15
            FaceAction.LOOK_DOWN -> pitch > 15
            FaceAction.BLINK -> {
                face.leftEyeOpenProbability?.let { it < 0.3 } == true
            }
            FaceAction.DONE -> {
                if (!donePromptGiven && ttsInitialized) {
                    speak("Operation Completed")
                    donePromptGiven = true
                }
                false
            }
        }
    }

    private fun getNextAction(action: FaceAction): FaceAction {
        val handler = Handler(Looper.getMainLooper())

        return when (action) {
            FaceAction.CENTER -> {
                speak("After 5 seconds turn your head left")
                scheduleCountdown(handler)
                FaceAction.LOOK_LEFT
            }
            FaceAction.LOOK_LEFT -> {
                speak("After 5 seconds turn your head right")
                scheduleCountdown(handler)
                FaceAction.LOOK_RIGHT
            }
            FaceAction.LOOK_RIGHT -> {
                speak("After 5 seconds turn your head down")
                scheduleCountdown(handler)
                FaceAction.LOOK_UP
            }
            FaceAction.LOOK_UP -> {
                speak("After 5 seconds turn your head up")
                scheduleCountdown(handler)
                FaceAction.LOOK_DOWN
            }
            FaceAction.LOOK_DOWN -> {
                speak("After 5 seconds blink your eyes")
                scheduleCountdown(handler)
                FaceAction.BLINK
            }
            else -> FaceAction.DONE
        }
    }

    private fun scheduleCountdown(handler: Handler) {
        handler.postDelayed({ speak("4") }, 3000)
        handler.postDelayed({ speak("3") }, 4000)
        handler.postDelayed({ speak("2") }, 5000)
        handler.postDelayed({ speak("1") }, 6000)
    }

    private fun speak(text: String) {
        if (ttsInitialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    private fun processWithAugmentation(faceBitmap: Bitmap) {
        val resized = Bitmap.createScaledBitmap(faceBitmap, 160, 160, true)
        var emb = runModel(resized)
        emb = normalize(emb)

        val embeddingList = emb.map { it.toDouble() }

        if (currentAction == FaceAction.CENTER) {
            if (centerEmbeddings.size < CENTER_EMBEDDINGS) {
                centerEmbeddings.add(embeddingList)
            }
        } else {
            if (actionEmbeddings.size < 5) {
                actionEmbeddings.add(embeddingList)
            }
        }
    }

    private fun runModel(bitmap: Bitmap): FloatArray {
        val inputBuffer = ByteBuffer.allocateDirect(1 * 160 * 160 * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(160 * 160)
        bitmap.getPixels(pixels, 0, 160, 0, 0, 160, 160)

        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            inputBuffer.putFloat(r)
            inputBuffer.putFloat(g)
            inputBuffer.putFloat(b)
        }

        val output = Array(1) { FloatArray(128) }
        tflite?.run(inputBuffer, output)
        return output[0]
    }

    private fun normalize(emb: FloatArray): FloatArray {
        var sum = 0f
        for (v in emb) sum += v * v
        val norm = Math.sqrt(sum.toDouble()).toFloat()
        for (i in emb.indices) emb[i] /= norm
        return emb
    }

    private fun computeAverageEmbedding(embeddings: List<List<Double>>): List<Double> {
        val dim = embeddings[0].size
        val avg = DoubleArray(dim)

        for (emb in embeddings) {
            for (i in 0 until dim) {
                avg[i] += emb[i]
            }
        }

        for (i in 0 until dim) {
            avg[i] /= embeddings.size
        }

        return avg.toList()
    }

    private fun buildFinalEmbeddings(): List<List<Double>> {
        val finalList = mutableListOf<List<Double>>()
        finalList.addAll(centerEmbeddings)
        finalList.addAll(actionEmbeddings)

        val all = mutableListOf<List<Double>>()
        all.addAll(centerEmbeddings)
        all.addAll(actionEmbeddings)

        if (all.isNotEmpty()) {
            val avg = computeAverageEmbedding(all)
            finalList.add(avg)
        }

        return finalList
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
    }
}