package com.example.phasmatic.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.phasmatic.R;
import com.example.phasmatic.data.ai.PineconeIndexer;
import com.example.phasmatic.data.model.User;
import com.example.phasmatic.extras.InternetConnection;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.*;


import org.tensorflow.lite.Interpreter;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmailAddressLog, edtPasswordLog;
    Button btnLoginLog, btnRegisterLog, btnFaceLogin, btnForgotPass;
    TextView txtDisplayInfoLog;

    Button captureButton;
    Button loadPineCone;
    android.view.View cameraLayout, loginLayout;

    DatabaseReference usersRef;
    DatabaseReference usersFaceRef;
    DatabaseReference userInfoRef;


    PreviewView viewFinder;

    private ImageCapture imageCapture;
    private Interpreter tflite;

    private static final int CAMERA_PERMISSION_CODE = 100;

    private String authenticatedUserId = null;
    private User authenticatedUser = null;

    private FirebaseAuth mAuth;

    private InternetConnection inter = new InternetConnection();



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestNotificationPermissionIfNeeded();

        if(!inter.isConnected(this)){
            inter.showCustomDialog(this);
        }

        edtEmailAddressLog = findViewById(R.id.edtEmailAddressLog);
        edtPasswordLog = findViewById(R.id.edtPasswordLog);
        btnLoginLog = findViewById(R.id.btnLoginLog);
        btnRegisterLog = findViewById(R.id.btnRegisterLog);
        btnFaceLogin = findViewById(R.id.btnFaceLogin);
        txtDisplayInfoLog = findViewById(R.id.txtDisplayInfoLog);
        btnForgotPass = findViewById(R.id.btnforgotPass);

        viewFinder = findViewById(R.id.viewFinder);
        cameraLayout = findViewById(R.id.cameraLayout);
        loginLayout = findViewById(R.id.loginLayout);
        captureButton = findViewById(R.id.image_capture_button);

        btnFaceLogin.setEnabled(false);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );

        usersRef = firebaseDb.getReference("users");
        usersFaceRef = firebaseDb.getReference("users_face_embedding");
        userInfoRef = firebaseDb.getReference("user_info");

        mAuth = FirebaseAuth.getInstance();

        loadFaceModel();

        btnForgotPass.setOnClickListener(v->
                startActivity(new Intent(this, ForgetActivity.class)));


        btnRegisterLog.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        btnLoginLog.setOnClickListener(v -> {
            if(!inter.isConnected(this)){
                inter.showCustomDialog(this);
            }

            String email = edtEmailAddressLog.getText().toString().trim();
            String password = edtPasswordLog.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                txtDisplayInfoLog.setText("Enter email and password");
                return;
            }

            loginWithFirebase(email, password);

//            PineconeIndexer indexer = new PineconeIndexer(this);
//            indexer.indexMasterPrograms();
//            indexer.indexErasmus();
//            indexer.indexUniversities();
//            indexer.indexCareer();
//            indexer.indexITFields();

        });

        btnFaceLogin.setOnClickListener(v -> checkCameraPermission());
        captureButton.setOnClickListener(v -> takePhoto());



    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { //Android 13+
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void loadFaceModel() {
        try {
            InputStream is = getAssets().open("facenet.tflite");
            byte[] model = new byte[is.available()];
            is.read(model);
            is.close();

            ByteBuffer buffer = ByteBuffer.allocateDirect(model.length)
                    .order(ByteOrder.nativeOrder());
            buffer.put(model);
            buffer.rewind();

            tflite = new Interpreter(buffer);

        } catch (Exception e) {
            Toast.makeText(this,"Model load failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void loginWithFirebase(String email, String password) {

        //Login sto Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        txtDisplayInfoLog.setText("Incorrect credentials");
                        return;
                    }

                    //epituxia sto auth
                    String uid = mAuth.getCurrentUser().getUid();

                    //load the user from db
                    usersRef.child(uid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    User user = snapshot.getValue(User.class);

                                    if (user == null) {
                                        txtDisplayInfoLog.setText("User profile not found");
                                        return;
                                    }

                                    // 3)enhmerwse to password field sto DB
                                    usersRef.child(uid).child("password").setValue(password);

                                    authenticatedUserId = uid;
                                    authenticatedUser   = user;

                                    Toast.makeText(LoginActivity.this,
                                            "Password OK. Scan your face.",
                                            Toast.LENGTH_LONG).show();

                                    btnFaceLogin.setEnabled(true);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    txtDisplayInfoLog.setText("Database error");
                                }
                            });
                });
    }

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }
    }

    private void startCamera() {

        cameraLayout.setVisibility(android.view.View.VISIBLE);
        loginLayout.setVisibility(android.view.View.GONE);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {

            try {

                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                );

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {

        String timeStamp = new SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis());

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                ).build();

        imageCapture.takePicture(
                options,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(LoginActivity.this,
                                "Capture failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults result) {

                        try {

                            Uri uri = result.getSavedUri();

                            InputStream stream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(stream);
                            stream.close();

                            Bitmap resized = Bitmap.createScaledBitmap(bitmap,160,160,true);

                            float[] embedding = normalize(runModel(resized));

                            loginWithFace(authenticatedUserId, embedding);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        cameraLayout.setVisibility(android.view.View.GONE);
                        loginLayout.setVisibility(android.view.View.VISIBLE);
                    }
                });
    }

    private void loginWithFace(String userId, float[] inputEmbedding) {

        usersFaceRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        float bestScore = -1f;

                        for (DataSnapshot child : snapshot.getChildren()) {

                            List<?> embeddings =
                                    (List<?>) child.child("faceEmbeddings").getValue();

                            if (embeddings == null) continue;

                            for (Object obj : embeddings) {

                                List<?> vector = (List<?>) obj;

                                float[] stored = new float[128];

                                for (int i = 0; i < 128; i++)
                                    stored[i] = ((Number) vector.get(i)).floatValue();

                                float sim = cosineSimilarity(inputEmbedding, stored);

                                if (sim > bestScore)
                                    bestScore = sim;
                            }
                        }

                        if (bestScore > 0.5f) {

                            Toast.makeText(LoginActivity.this,
                                    "Face verified",
                                    Toast.LENGTH_LONG).show();
//                            String sh = "hey score is " + bestScore;
//                            Toast.makeText(LoginActivity.this, sh, Toast.LENGTH_LONG).show();
                            openNextActivity();

                        } else {

                            Toast.makeText(LoginActivity.this,
                                    "Face mismatch",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
    }

    private void openNextActivity() {

        userInfoRef.child(authenticatedUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snap) {

                        Intent i;

                        if (snap.exists())
                            i = new Intent(LoginActivity.this, ModeSelectionActivity.class);
                        else
                            i = new Intent(LoginActivity.this, UserInfoActivity.class);

                        i.putExtra("userId", authenticatedUserId);
                        i.putExtra("userFullName", authenticatedUser.getFullName());
                        i.putExtra("userEmail", authenticatedUser.getEmail());
                        i.putExtra("userPhone", authenticatedUser.getPhoneNumber());

                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
    }

    private float[] runModel(Bitmap bitmap) {

        ByteBuffer buffer = ByteBuffer.allocateDirect(1*160*160*3*4);
        buffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[160*160];
        bitmap.getPixels(pixels,0,160,0,0,160,160);

        for(int pixel:pixels){

            float r=((pixel>>16)&0xFF)/255f;
            float g=((pixel>>8)&0xFF)/255f;
            float b=(pixel&0xFF)/255f;

            buffer.putFloat(r);
            buffer.putFloat(g);
            buffer.putFloat(b);
        }

        float[][] output=new float[1][128];
        tflite.run(buffer,output);

        return output[0];
    }

    private float cosineSimilarity(float[] v1,float[] v2){

        float dot=0,n1=0,n2=0;

        for(int i=0;i<v1.length;i++){
            dot+=v1[i]*v2[i];
            n1+=v1[i]*v1[i];
            n2+=v2[i]*v2[i];
        }

        return (float)(dot/(Math.sqrt(n1)*Math.sqrt(n2)));
    }

    private float[] normalize(float[] emb){

        float sum=0;

        for(float v:emb) sum+=v*v;

        float norm=(float)Math.sqrt(sum);

        for(int i=0;i<emb.length;i++)
            emb[i]/=norm;

        return emb;
    }
}