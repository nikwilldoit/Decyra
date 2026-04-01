package com.example.phasmatic.data.ai;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PineconeClient {

    private static final String BASE_URL = "https://decyra-better-index-trb4i0f.svc.aped-4627-b74a.pinecone.io/query";
    private static final String API_KEY = "pcsk_3sKnDQ_U2HLqcoc4Dstfk3RPndnDyKL36ggcwhiaQJrCe6R9qZ2AUzufab9tZGDM5SXSTX";

    private final OkHttpClient client = new OkHttpClient();

    public interface PineconeCallback {
        void onSuccess(String context);
        void onError(String error);
    }

    public void upsertNamespace(float[] vector, String id, JSONObject metadata, String namespace) {
        try {
             //master index
            String url = "https://decyra-better-index-trb4i0f.svc.aped-4627-b74a.pinecone.io/vectors/upsert";

            //erasmus index
            //String url = "https://decyra-erasmus-index-trb4i0f.svc.aped-4627-b74a.pinecone.io/vectors/upsert";

            //work index
            //String url = "https://decyra-work-index-trb4i0f.svc.aped-4627-b74a.pinecone.io/vectors/upsert";


            JSONObject body = new JSONObject();
            JSONArray vectors = new JSONArray();

            JSONObject vec = new JSONObject();
            vec.put("id", id);
            vec.put("values", new JSONArray(vector));
            vec.put("metadata", metadata);

            vectors.put(vec);
            body.put("vectors", vectors);
            body.put("namespace", namespace); //orizoyme neo namespace gia univeristies

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Api-Key", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(body.toString(), MediaType.get("application/json")))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("PINECONE", "REQUEST FAILED: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d("PINECONE", "UPSERT SUCCESS!");
                    } else {
                        Log.e("PINECONE", "ERROR RESPONSE: " + response.code());
                    }
                }
            });

        } catch (Exception e) {
            Log.e("PINECONE", "EXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }
    }


//    public void upsertMaster(float[] vector, String id, JSONObject metadata) {
//
//        try {
//            String url = "https://decyra-better-index-trb4i0f.svc.aped-4627-b74a.pinecone.io/vectors/upsert";
//
//            Log.d("PINECONE", "===== UPSERT START =====");
//            Log.d("PINECONE", "URL: " + url);
//            Log.d("PINECONE", "ID: " + id);
//            Log.d("PINECONE", "Vector length: " + vector.length);
//            Log.d("PINECONE", "Metadata: " + metadata.toString());
//
//            JSONObject body = new JSONObject();
//            JSONArray vectors = new JSONArray();
//
//            JSONObject vec = new JSONObject();
//            vec.put("id", id);
//            vec.put("values", new JSONArray(vector));
//            vec.put("metadata", metadata);
//
//            vectors.put(vec);
//            body.put("vectors", vectors);
//
//            Log.d("PINECONE", "Request body: " + body.toString());
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .addHeader("Api-Key", API_KEY)
//                    .addHeader("Content-Type", "application/json")
//                    .post(RequestBody.create(body.toString(),
//                            MediaType.get("application/json")))
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    Log.e("PINECONE", "REQUEST FAILED: " + e.getMessage());
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//
//                    String resBody = response.body() != null
//                            ? response.body().string()
//                            : "EMPTY";
//
//                    Log.d("PINECONE", "RESPONSE CODE: " + response.code());
//                    Log.d("PINECONE", "RESPONSE BODY: " + resBody);
//
//                    if (!response.isSuccessful()) {
//                        Log.e("PINECONE", "ERROR RESPONSE!");
//                    } else {
//                        Log.d("PINECONE", "UPSERT SUCCESS!");
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            Log.e("PINECONE", "EXCEPTION: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    public void query(float[] vector, PineconeCallback callback) {

        try {
            JSONObject body = new JSONObject();
            body.put("vector", new JSONArray(vector));
            body.put("topK", 5);
            body.put("includeMetadata", true);

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .addHeader("Api-Key", API_KEY)
                    .post(RequestBody.create(body.toString(),
                            MediaType.get("application/json")))
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        String res = response.body().string();

                        JSONObject json = new JSONObject(res);
                        JSONArray matches = json.getJSONArray("matches");

                        StringBuilder context = new StringBuilder();

                        for (int i = 0; i < matches.length(); i++) {
                            JSONObject meta = matches.getJSONObject(i)
                                    .getJSONObject("metadata");

                            context.append("Program: ").append(meta.optString("title")).append("\n").append("Country: ").append(meta.optString("country")).append("\n").append("Field: ").append(meta.optString("field")).append("\n").append("Description: ").append(meta.optString("description")).append("\n\n");
                        }

                        callback.onSuccess(context.toString());

                    } catch (Exception e) {
                        callback.onError(e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
}