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

    private final OpenAIChatClient openAIClient;
    private final PineconeClient pineconeClient;

    public PineconeIndexer(Context context) {
        openAIClient = new OpenAIChatClient(context);
        pineconeClient = new PineconeClient();
    }

    public void indexMasterPrograms() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("master");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    String id = item.child("id").getValue(String.class);
                    String name = item.child("name").getValue(String.class);
                    String description = item.child("description").getValue(String.class);
                    String universityId = item.child("university_id").getValue(String.class);
                    String language = item.child("language").getValue(String.class);
                    

                    String text = "Master's name: " + name + ", Description:  "+ description +
                            ". University id: " + universityId + ", language of the program: " + language;

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {

                            try {

                                JSONObject metadataObject = new JSONObject();
                                metadataObject.put("name", name);
                                metadataObject.put("description", description);
                                metadataObject.put("university_id", universityId);
                                metadataObject.put("language",language);


                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadataObject,
                                        "master",
                                        "Education"
                                );

                            } catch (Exception exception) {
                                Log.e("INDEX", "Error indexing master", exception);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", "Embedding error (master): " + errorMessage);
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

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("universities");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    Long idLong = item.child("id").getValue(Long.class);
                    Long rankingLong = item.child("ranking").getValue(Long.class);

                    String id = String.valueOf(idLong);
                    String name = item.child("name").getValue(String.class);
                    String country = item.child("country").getValue(String.class);
                    String ranking = String.valueOf(rankingLong);


                    String text = "University name: " + name + ", from the country: " + country +
                            "with ranking: " + ranking;

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {

                            try {

                                JSONObject metadataObject = new JSONObject();
                                metadataObject.put("name", name);
                                metadataObject.put("country", country);
                                metadataObject.put("ranking",ranking);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadataObject,
                                        "universities",
                                        "Education"
                                );

                            } catch (Exception exception) {
                                Log.e("INDEX", "Error indexing universities", exception);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", "Embedding error (universities): " + errorMessage);
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

    public void indexCareer() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("career");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    Long idLong = item.child("id").getValue(Long.class);
                    Long salaryLong = item.child("avg_salary_with_master").getValue(Long.class);
                    Long salaryNoMasterLong = item.child("avg_salary_no_master").getValue(Long.class);
                    Long fieldLong = item.child("field_id").getValue(Long.class);
                    Long countryLong = item.child("country_id").getValue(Long.class);

                    String salary = String.valueOf(salaryLong);
                    String salaryNoMaster =  String.valueOf(salaryNoMasterLong);
                    String id =  String.valueOf(idLong);
                    String fieldId = String.valueOf(fieldLong);
                    String countryId = String.valueOf(countryLong);



                    String text =
                            "Average salary WITH master degree is " + salary + ". " +
                                    "Average salary WITHOUT master degree is " + salaryNoMaster + ". " +
                                    "Field id " + fieldId +
                                    "Country id" + countryId;

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {

                            try {

                                JSONObject metadataObject = new JSONObject();
                                metadataObject.put("salary_With_Master", salary);
                                metadataObject.put("salary_WithOut_Master", salaryNoMaster);
                                metadataObject.put("field_id", fieldId);
                                metadataObject.put("country_id", countryId);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadataObject,
                                        "career",
                                        "career"
                                );

                            } catch (Exception exception) {
                                Log.e("INDEX", "Error indexing career", exception);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", "Embedding error (career): " + errorMessage);
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
    public void indexITFields() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("it_fields");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    Long idLong = item.child("id").getValue(Long.class);


                    String id = String.valueOf(idLong);
                    String name = item.child("name").getValue(String.class);


                    String text = "Computer science field: " +name;

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {

                            try {

                                JSONObject metadataObject = new JSONObject();
                                metadataObject.put("name", name);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadataObject,
                                        "it_fields",
                                        "career"
                                );

                            } catch (Exception exception) {
                                Log.e("INDEX", "Error indexing IT fields", exception);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", "Embedding error (fields): " + errorMessage);
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


    public void indexCountries() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("countries");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    Long idLong = item.child("id").getValue(Long.class);

                    String id = String.valueOf(idLong);
                    String name = item.child("name").getValue(String.class);

                    String text = "Country name " + name;

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {

                            try {

                                JSONObject metadataObject = new JSONObject();
                                metadataObject.put("name", name);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadataObject,
                                        "countries",
                                        "career"
                                );

                            } catch (Exception exception) {
                                Log.e("INDEX", "Error indexing countries", exception);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", "Embedding error (countries): " + errorMessage);
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

    public void indexErasmus() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("erasmus");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    Long idLong = item.child("id").getValue(Long.class);
                    Long uniLong = item.child("university_id").getValue(Long.class);

                    String id = String.valueOf(idLong);
                    String name = item.child("name").getValue(String.class);
                    String university_id = String.valueOf(uniLong);
                    String language = item.child("language").getValue(String.class);



                    String text = "Erasmus name: " + name + ", language: " + language+
                            ", university id: " + university_id;

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {

                            try {

                                JSONObject metadataObject = new JSONObject();
                                metadataObject.put("name", name);
                                metadataObject.put("university_id",university_id);
                                metadataObject.put("language", language);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadataObject,
                                        "erasmus",
                                        "Education"
                                );

                            } catch (Exception exception) {
                                Log.e("INDEX", "Error indexing Erasmus", exception);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", "Embedding error (fields): " + errorMessage);
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