package com.example.phasmatic.data.ai;


import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

public class PineconeIndexer {

    private final OpenAIChatClient openAI;
    private final PineconeClient pinecone;
    private DatabaseReference masterRef;

    public PineconeIndexer(Context context) {
        openAI = new OpenAIChatClient(context);
        pinecone = new PineconeClient();
    }

    public void indexPrograms() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app");
        masterRef = db.getReference("master");

        masterRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot item : snapshot.getChildren()) {

                            String id = item.child("id").getValue(String.class);
                            String name = item.child("name").getValue(String.class);
                            String description = item.child("description").getValue(String.class);
                            String language = item.child("language").getValue(String.class);
                            String ranking = item.child("ranking").getValue(String.class);
                            String universityId = item.child("university_id").getValue(String.class);
                            String websiteUrl = item.child("website_url").getValue(String.class);

                            //ftiaxnoume text gia to embedding
                            String text = name + ". " + description + ". Language: " + language + ". Ranking: " + ranking;

                            openAI.getEmbedding(text, new EmbeddingCallback() {

                                @Override
                                public void onSuccess(float[] embedding) {
                                    Log.d("FLOW", "Embedding received, length: " + embedding.length);

                                    try {
                                        JSONObject metadata = new JSONObject();
                                        metadata.put("name", name);
                                        metadata.put("description", description);
                                        metadata.put("language", language);
                                        metadata.put("ranking", ranking);
                                        metadata.put("university_id", universityId);
                                        metadata.put("website_url", websiteUrl);

                                        Log.d("FLOW", "Sending to Pinecone id=" + id);

                                        pinecone.upsertNamespace(embedding, id, metadata, "__default__");

                                    } catch (Exception e) {
                                        Log.e("FLOW", "Metadata error", e);
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e("FLOW", "Embedding ERROR: " + error);
                                }

                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        error.toException().printStackTrace();
                    }

                });
    }

    public void indexUniversities() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference uniRef = db.getReference("universities");

        uniRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    String id = item.child("id").getValue(String.class);
                    String name = item.child("name").getValue(String.class);
                    String city = item.child("city").getValue(String.class);
                    String country = item.child("country").getValue(String.class);
                    String ranking = item.child("ranking").getValue(String.class);
                    String websiteUrl = item.child("website_url").getValue(String.class);

                    // Δημιουργούμε ένα string για το embedding
                    String text = name + ", " + city + ", " + country + ". Ranking: " + ranking;

                    openAI.getEmbedding(text, new EmbeddingCallback() {
                        @Override
                        public void onSuccess(float[] embedding) {
                            try {
                                JSONObject metadata = new JSONObject();
                                metadata.put("name", name);
                                metadata.put("city", city);
                                metadata.put("country", country);
                                metadata.put("ranking", ranking);
                                metadata.put("website_url", websiteUrl);

                                //stelnoume sto pinecone namespace "universities"
                                pinecone.upsertNamespace(embedding, id, metadata, "universities");

                            } catch (Exception e) {
                                Log.e("FLOW", "Metadata error", e);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("FLOW", "Embedding ERROR: " + error);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
    }


}