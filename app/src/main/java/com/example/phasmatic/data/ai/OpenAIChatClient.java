package com.example.phasmatic.data.ai;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.phasmatic.data.model.MessageLLM;
import com.google.firebase.database.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class OpenAIChatClient {

    private static final String OPENAI_URL =
            "https://api.openai.com/v1/chat/completions";

    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final FirebaseDatabase firebaseDb;
    private final PineconeClient pineconeClient = new PineconeClient();

    public interface ChatCallback {
        void onSuccess(String reply);
        void onError(String error);
    }

    public interface MessagesCallback {
        void onSuccess(java.util.ArrayList<MessageLLM> messages);
        void onError(String error);
    }

    public OpenAIChatClient(Context context) {
        firebaseDb = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
    }

    public void getEmbedding(String text, EmbeddingCallback callback) {

        firebaseDb.getReference("api_keys/0/api_key")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String apiKey = snapshot.getValue(String.class);

                        try {
                            JSONObject body = new JSONObject();
                            body.put("model", "text-embedding-3-small");
                            body.put("input", text);

                            Request request = new Request.Builder()
                                    .url("https://api.openai.com/v1/embeddings")
                                    .addHeader("Authorization", "Bearer " + apiKey)
                                    .post(RequestBody.create(body.toString(), JSON))
                                    .build();

                            client.newCall(request).enqueue(new Callback() {

                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    callback.onError(e.getMessage());
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) {

                                    try {
                                        JSONObject json = new JSONObject(response.body().string());
                                        JSONArray emb = json.getJSONArray("data")
                                                .getJSONObject(0)
                                                .getJSONArray("embedding");

                                        float[] vector = new float[emb.length()];

                                        for (int i = 0; i < emb.length(); i++) {
                                            vector[i] = (float) emb.getDouble(i);
                                        }

                                        callback.onSuccess(vector);

                                    } catch (Exception e) {
                                        callback.onError(e.getMessage());
                                    }
                                }
                            });

                        } catch (Exception e) {
                            callback.onError(e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }
    private MessageLLM saveMessage(String conversationId, String role, String content) {

        DatabaseReference ref = firebaseDb.getReference("messages_llm").push();

        MessageLLM msg = new MessageLLM(
                ref.getKey(),
                conversationId,
                role,
                content,
                System.currentTimeMillis()
        );

        ref.setValue(msg);

        return msg;
    }

    private void getLastMessages(
            String conversationId,
            int limit,
            MessagesCallback callback
    ) {

        firebaseDb.getReference("messages_llm")
                .orderByChild("conversationId")
                .equalTo(conversationId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        java.util.ArrayList<MessageLLM> list = new java.util.ArrayList<>();

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            MessageLLM m = ds.getValue(MessageLLM.class);
                            if (m != null) list.add(m);
                        }

                        list.sort((a, b) ->
                                Long.compare(a.getTimestamp(), b.getTimestamp())
                        );

                        if (list.size() > limit) {
                            list = new java.util.ArrayList<>(
                                    list.subList(list.size() - limit, list.size())
                            );
                        }

                        callback.onSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public void sendMessage(
            int num,
            String conversationId,
            String userMessage,
            String userFullName,
            ChatCallback callback
    ) {
        MessageLLM userMsg = saveMessage(conversationId, "user", userMessage);

        getEmbedding(userMessage, new EmbeddingCallback() {
            @Override
            public void onSuccess(float[] embedding) {

                //pinecone history
                pineconeClient.upsertChatHistory(
                        embedding,
                        userMsg.getId(),
                        userFullName,
                        "user",
                        userMessage,
                        conversationId,
                        userMsg.getTimestamp()
                );

                runRag(num, embedding, conversationId, userMessage, callback, userFullName);
            }

            @Override
            public void onError(String error) {
                runRag(num, null, conversationId, userMessage, callback, userFullName);
            }
        });
    }

    private void runRag(
            int num,
            float[] embedding,
            String conversationId,
            String userMessage,
            ChatCallback callback,
            String userFullName
    ) {

        firebaseDb.getReference("api_keys/0/api_key")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String apiKey = snapshot.getValue(String.class);

                        if (embedding == null) {
                            buildPrompt(apiKey, conversationId, userMessage, "", callback, userFullName);
                            return;
                        }

                        String[] namespaces;
                        String[] indexes;

                        if (num == 0) {
                            namespaces = new String[]{"erasmus-AUEB"};
                            indexes = new String[]{"Education"};
                        } else if (num == 1) {
                            namespaces = new String[]{"europe-master"};
                            indexes = new String[]{"master"};
                        } else if(num == 2){
                            namespaces = new String[]{"main-career"};
                            indexes = new String[]{"career"};
                        }else if(num==4){
                            namespaces = new String[]{"erasmus-THESSALY"};
                            indexes = new String[]{"Education"};
                        }else if(num==5){
                            namespaces = new String[]{"erasmus-ARISTOTLE"};
                            indexes = new String[]{"Education"};
                        }
                        else if(num==6){
                            namespaces = new String[]{"erasmus-EKPA"};
                            indexes = new String[]{"Education"};
                        }
                        else if(num==7){
                            namespaces = new String[]{"erasmus-CRETE"};
                            indexes = new String[]{"Education"};
                        }
                        else if(num==8){
                            namespaces = new String[]{"erasmus-PAPEI"};
                            indexes = new String[]{"Education"};
                        }
                        else if(num==9){
                            namespaces = new String[]{"erasmus-PELLOPONESE"};
                            indexes = new String[]{"Education"};
                        }
                        else if(num==10){
                            namespaces = new String[]{"erasmus-HAROKOPIO"};
                            indexes = new String[]{"Education"};
                        }
                        else{
                            namespaces = new String[]{"erasmus-IONIAN"};
                            indexes = new String[]{"Education"};
                        }

                        queryNext(0, namespaces, indexes, embedding, new StringBuilder(),
                                apiKey, conversationId, userMessage, callback);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }
    private void queryNext(
            int index,
            String[] namespaces,
            String[] indexes,
            float[] embedding,
            StringBuilder context,
            String apiKey,
            String conversationId,
            String userMessage,
            ChatCallback callback
    ) {

        if (index == namespaces.length) {
            buildPrompt(apiKey, conversationId, userMessage, context.toString(), callback, userMessage);
            return;
        }

        pineconeClient.queryIndex(
                embedding,
                namespaces[index],
                indexes[index],
                new PineconeClient.PineconeCallback() {

                    @Override
                    public void onSuccess(String ctx) {
                        context.append(ctx).append("\n");
                        queryNext(index + 1, namespaces, indexes, embedding,
                                context, apiKey, conversationId, userMessage, callback);
                    }

                    @Override
                    public void onError(String error) {
                        queryNext(index + 1, namespaces, indexes, embedding,
                                context, apiKey, conversationId, userMessage, callback);
                    }
                }
        );
    }
    private void buildPrompt(
            String apiKey,
            String conversationId,
            String userMessage,
            String ragContext,
            ChatCallback callback,
            String userFullName
    ) {

        getLastMessages(conversationId, 2, new MessagesCallback() {

            @Override
            public void onSuccess(java.util.ArrayList<MessageLLM> history) {

                try {

                    JSONArray messages = new JSONArray();

                    messages.put(new JSONObject()
                            .put("role", "system")
                            .put("content",
                                    "ROLE: Academic recommender system (fit-score ranking engine).\n\n" +

                                            "GOAL:\n" +
                                            "Select and explain the best matching options based ONLY on Fit Score.\n\n" +

                                            "CRITICAL RULES:\n" +
                                            "- NEVER reference retrieval order, Pinecone order, or index position.\n" +
                                            "- Treat all options as unordered candidates.\n" +
                                            "- Ranking must be derived ONLY from Fit Score evaluation.\n" +
                                            "- Do NOT mention 'Option 1/2/3' or any retrieval numbering.\n\n" +

                                            "SCORING MODEL (MANDATORY):\n" +
                                            "Compute Fit Score (0–10) using:\n" +
                                            "- Language match\n" +
                                            "- Location / region preference\n" +
                                            "- Cost / budget compatibility\n" +
                                            "- Field / academic alignment\n" +
                                            "- Goal alignment\n\n" +

                                            "WEIGHTING RULE:\n" +
                                            "- Prioritize fields according to user profile importance.\n" +
                                            "- Strong mismatch in HIGH priority field → major score penalty.\n\n" +

                                            "OUTPUT RULE:\n" +
                                            "- Return TOP 5 candidates sorted by Fit Score (best → worst)\n" +
                                            "- DO NOT show any retrieval index or source ordering\n\n" +

                                            "OUTPUT FORMAT (STRICT):\n" +
                                            "University - Program - Country\n" +
                                            "Fit Score: X/10\n" +
                                            "Why: 1 concise sentence\n\n" +

                                            "STYLE:\n" +
                                            "- structured, deterministic\n" +
                                            "- no meta references (no 'context', no 'database', no 'Pinecone')\n"
                            )
                    );

                    messages.put(new JSONObject()
                            .put("role", "system")
                            .put("content", "RAG CONTEXT:\n" + ragContext));

                    for (MessageLLM m : history) {
                        messages.put(new JSONObject()
                                .put("role", m.getRole())
                                .put("content", m.getContent()));
                    }

                    messages.put(new JSONObject()
                            .put("role", "user")
                            .put("content", userMessage));

                    callOpenAI(apiKey, messages, conversationId, callback,userFullName);
                } catch (Exception e) {
                    callback.onError(e.getMessage());
                }
            }
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    private void callOpenAI(
            String apiKey,
            JSONArray messages,
            String conversationId,
            ChatCallback callback,
            String userFullName
    ) {

        try {

            JSONObject body = new JSONObject();
            body.put("model", "gpt-4o-mini");
            body.put("messages", messages);

            Request request = new Request.Builder()
                    .url(OPENAI_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {

                    try {

                        String content = new JSONObject(response.body().string())
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        MessageLLM assistant = saveMessage(conversationId, "assistant", content);

                        getEmbedding(content, new EmbeddingCallback() {
                            @Override
                            public void onSuccess(float[] embeddingVector) {

                                pineconeClient.upsertChatHistory(
                                        embeddingVector,
                                        assistant.getId(),
                                        userFullName+"-SystemResp",
                                        "assistant",
                                        content,
                                        conversationId,
                                        Long.valueOf(String.valueOf(assistant.getTimestamp()))
                                );
                            }

                            @Override
                            public void onError(String error) {}
                        });

                        callback.onSuccess(content);

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