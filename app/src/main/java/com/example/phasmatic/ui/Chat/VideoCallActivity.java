package com.example.phasmatic.ui.Chat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.phasmatic.R;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.VideoCanvas;

public class VideoCallActivity extends AppCompatActivity {

    private static final String TAG = "VIDEO";

    private RtcEngine agoraEngine;

    private static final String APP_ID = "0f9cc9d655b347fb852d60aef0fcf693";
    private static final String TOKEN  = null;  // without certificate

    private String channelName;

    private FrameLayout localContainer;
    private FrameLayout remoteContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        channelName = getIntent().getStringExtra("channelName");
        Log.d(TAG, "onCreate, channelName=" + channelName);

        localContainer  = findViewById(R.id.localVideoContainer);
        remoteContainer = findViewById(R.id.remoteVideoContainer);

        findViewById(R.id.btnEndCall).setOnClickListener(v -> {
            Log.d(TAG, "EndCall clicked");
            if (agoraEngine != null) {
                agoraEngine.leaveChannel();
            }
            finish();
        });

        findViewById(R.id.btnMute).setOnClickListener(v -> {
            Log.d(TAG, "Mute clicked");
            if (agoraEngine != null) {
                agoraEngine.muteLocalAudioStream(true);
            }
        });

        findViewById(R.id.btnSwitchCamera).setOnClickListener(v -> {
            Log.d(TAG, "SwitchCamera clicked");
            if (agoraEngine != null) {
                agoraEngine.switchCamera();
            }
        });

        requestPermissions();
    }

    private void requestPermissions() {
        Log.d(TAG, "requestPermissions");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "requestPermissions: requesting");
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO },
                    1
            );
        } else {
            Log.d(TAG, "requestPermissions: already granted → initAgora");
            initAgora();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult, requestCode=" + requestCode +
                " len=" + grantResults.length +
                " cam=" + (grantResults.length > 0 ? grantResults[0] : -1) +
                " mic=" + (grantResults.length > 1 ? grantResults[1] : -1));

        if (requestCode == 1 &&
                grantResults.length >= 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "permissions GRANTED → initAgora");
            initAgora();
        } else {
            Log.d(TAG, "permissions NOT fully granted, staying here");
        }
    }

    private void initAgora() {
        Log.d(TAG, "initAgora START");

        try {
            agoraEngine = RtcEngine.create(getBaseContext(), APP_ID, rtcHandler);
            Log.d(TAG, "RtcEngine.create returned, engine = " + agoraEngine);
        } catch (Exception e) {
            Log.e(TAG, "RtcEngine.create FAILED: " + e.getMessage(), e);
            return;
        }

        if (agoraEngine == null) {
            Log.e(TAG, "agoraEngine is NULL");
            return;
        }

        Log.d(TAG, "RtcEngine created OK");

        agoraEngine.enableVideo();
        setupLocalVideo();
        joinChannel();
    }

    private void setupLocalVideo() {
        Log.d(TAG, "setupLocalVideo");
        localContainer.removeAllViews();

        // ΣΗΜΑΝΤΙΚΟ: χρησιμοποιούμε CreateRendererView
        SurfaceView localView = RtcEngine.CreateRendererView(getBaseContext());
        localView.setZOrderMediaOverlay(true);
        localContainer.addView(localView);

        agoraEngine.setupLocalVideo(
                new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0)
        );
    }

    private void joinChannel() {
        Log.d(TAG, "joinChannel, channel=" + channelName);

        ChannelMediaOptions options = new ChannelMediaOptions();
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;

        agoraEngine.startPreview();

        int res = agoraEngine.joinChannel(
                TOKEN,
                channelName,
                0,
                options
        );
        Log.d(TAG, "joinChannel result=" + res);
    }

    private final IRtcEngineEventHandler rtcHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.d(TAG, "onJoinChannelSuccess channel=" + channel + " uid=" + uid);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            Log.d(TAG, "onUserJoined uid=" + uid);
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            Log.d(TAG, "onUserOffline uid=" + uid + " reason=" + reason);
            runOnUiThread(() -> finish());
        }

        @Override
        public void onError(int err) {
            Log.e(TAG, "Agora onError = " + err);
        }
    };

    private void setupRemoteVideo(int uid) {
        Log.d(TAG, "setupRemoteVideo uid=" + uid);
        remoteContainer.removeAllViews();

        // ΚΑΙ εδώ CreateRendererView
        SurfaceView remoteView = RtcEngine.CreateRendererView(getBaseContext());
        remoteContainer.addView(remoteView);

        agoraEngine.setupRemoteVideo(
                new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop CALLED");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy CALLED");

        if (agoraEngine != null) {
            agoraEngine.leaveChannel();
        }
        RtcEngine.destroy();
    }
}
