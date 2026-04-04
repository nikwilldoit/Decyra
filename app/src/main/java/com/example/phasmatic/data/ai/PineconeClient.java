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

    private static final String API_KEY = "pcsk_2BxKaT_SFgCvuA3PLtbGaPxXYVA7AVXAXFGffPro5DxBdfVzzYaPo5FcUb4snuHHd338Yh";

    private static final String ERASMUS_INDEX_BASE_URL =
            "https://decyra-erasmus-index-trb4i0f.svc.aped-4627-b74a.pinecone.io";

    private static final String WORK_INDEX_BASE_URL =
            "https://decyra-work-index-trb4i0f.svc.aped-4627-b74a.pinecone.io";

    private final OkHttpClient httpClient;

    public PineconeClient() {
        httpClient = new OkHttpClient();
    }

    public interface PineconeCallback {
        void onSuccess(String context);
        void onError(String errorMessage);
    }

    private String resolveBaseUrl(String indexName) {
        if (indexName.equals("Education")) {
            return ERASMUS_INDEX_BASE_URL;
        }

        if (indexName.equals("career")) {
            return WORK_INDEX_BASE_URL;
        }

        throw new IllegalArgumentException("Invalid index name provided");
    }

    public void upsertVector(
            float[] embeddingVector,
            String vectorId,
            JSONObject metadataObject,
            String namespace,
            String indexName
    ) {

        try {

            String baseUrl = resolveBaseUrl(indexName);
            String url = baseUrl + "/vectors/upsert";

            JSONObject requestBodyJson = new JSONObject();
            JSONArray vectorsArray = new JSONArray();

            JSONObject vectorObject = new JSONObject();
            vectorObject.put("id", vectorId + "_" + namespace);
            vectorObject.put("values", new JSONArray(embeddingVector));
            vectorObject.put("metadata", metadataObject);

            vectorsArray.put(vectorObject);

            requestBodyJson.put("vectors", vectorsArray);
            requestBodyJson.put("namespace", namespace);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Api-Key", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(
                            requestBodyJson.toString(),
                            MediaType.get("application/json")
                    ))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException exception) {
                    Log.e("PINECONE", "Upsert failed: " + exception.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseBody = "";

                    if (response.body() != null) {
                        responseBody = response.body().string();
                    }

                    if (response.isSuccessful()) {
                        Log.d("PINECONE", "Upsert successful in namespace: " + namespace);
                    } else {
                        Log.e("PINECONE", "Upsert error: " + responseBody);
                    }
                }
            });

        } catch (Exception exception) {
            Log.e("PINECONE", "Exception during upsert", exception);
        }
    }

    public void queryIndex(
            float[] embeddingVector,
            String namespace,
            String indexName,
            PineconeCallback callback
    ) {

        try {

            String baseUrl = resolveBaseUrl(indexName);
            String url = baseUrl + "/query";

            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("vector", new JSONArray(embeddingVector));
            requestBodyJson.put("topK", 10);
            requestBodyJson.put("includeMetadata", true);
            requestBodyJson.put("namespace", namespace);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Api-Key", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(
                            requestBodyJson.toString(),
                            MediaType.get("application/json")
                    ))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException exception) {
                    callback.onError(exception.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {

                    try {

                        if (!response.isSuccessful()) {
                            callback.onError("HTTP error code: " + response.code());
                            return;
                        }

                        String responseBody = response.body().string();
                        JSONObject responseJson = new JSONObject(responseBody);

                        JSONArray matchesArray = responseJson.optJSONArray("matches");

                        if (matchesArray == null) {
                            callback.onError("Matches array not found");
                            return;
                        }

                        StringBuilder contextBuilder = new StringBuilder();

                        for (int i = 0; i < matchesArray.length(); i++) {

                            JSONObject matchObject = matchesArray.getJSONObject(i);
                            JSONObject metadataObject = matchObject.optJSONObject("metadata");

                            if (metadataObject != null) {
                                contextBuilder.append("Name: ").append(metadataObject.optString("name")).append("\n");

                                if (metadataObject.has("description")) {
                                    contextBuilder.append("Description: ")
                                            .append(metadataObject.optString("description"))
                                            .append("\n");
                                }

                                if (metadataObject.has("salary_with_master")) {
                                    contextBuilder.append("Salary with master: ")
                                            .append(metadataObject.optString("salary_with_master"))
                                            .append("\n");
                                }

                                if (metadataObject.has("salary_without_master")) {
                                    contextBuilder.append("Salary without master: ")
                                            .append(metadataObject.optString("salary_without_master"))
                                            .append("\n");
                                }

                                contextBuilder.append("\n");
                            }
                        }

                        callback.onSuccess(contextBuilder.toString());

                    } catch (Exception exception) {
                        callback.onError(exception.getMessage());
                    }
                }
            });

        } catch (Exception exception) {
            callback.onError(exception.getMessage());
        }
    }
}