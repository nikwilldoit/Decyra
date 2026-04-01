package com.example.phasmatic.data.ai;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public interface EmbeddingCallback {
    void onSuccess(float[] embedding);
    void onError(String error);

}
