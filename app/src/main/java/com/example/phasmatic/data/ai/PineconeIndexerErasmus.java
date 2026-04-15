package com.example.phasmatic.data.ai;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.*;

import org.json.JSONObject;

import java.util.Objects;

public class PineconeIndexerErasmus {

    private final OpenAIChatClient openAIClient;
    private final PineconeClient pineconeClient;

    public PineconeIndexerErasmus(Context context) {
        openAIClient = new OpenAIChatClient(context);
        pineconeClient = new PineconeClient();
    }

    // --------------------------- AUEB ---------------------------
    public void indexErasmusAUEB() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("erasmus");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    String uniName = item.child("university_name").getValue(String.class);
                    if (!Objects.equals(uniName, "Οικονομικό Πανεπιστήμιο Αθηνών")) continue;

                    Long idlong = item.child("id").getValue(Long.class);
                    String id = String.valueOf(idlong);
                    String city = item.child("city").getValue(String.class);
                    String country = item.child("country").getValue(String.class);
                    String name = item.child("name").getValue(String.class);
                    String language = item.child("language").getValue(String.class);
                    String fund = item.child("fund").getValue(String.class);

                    String text =
                            "Το Οικονομικό Πανεπιστήμιο Αθηνών προσφέρει πρόγραμμα Erasmus στο "
                                    + name + " στην πόλη " + city + " της χώρας " + country + ". "
                                    + "Η απαίτηση γλώσσας είναι: " + language + " και η χρηματοδότηση είναι "
                                    + fund + " ευρώ.";

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {
                            try {
                                JSONObject metadata = new JSONObject();
                                metadata.put("Όνομα Πανεπιστημίου", "Οικονομικό Πανεπιστήμιο Αθηνών");
                                metadata.put("Όνομα Προγράμματος", name);
                                metadata.put("Χώρα", country);
                                metadata.put("Πόλη", city);
                                metadata.put("Γλώσσα", language);
                                metadata.put("Χρηματοδότηση", fund);
                                metadata.put("Περιγραφή", text);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadata,
                                        "erasmus-AUEB",
                                        "Education"
                                );

                            } catch (Exception e) {
                                Log.e("INDEX", "AUEB error", e);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", errorMessage);
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

    // --------------------------- EKPA ---------------------------
    public void indexErasmusEKPA() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("erasmus");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    String uniName = item.child("university_name").getValue(String.class);
                    if (!Objects.equals(uniName, "University of Athens")) continue;

                    String id = item.child("id").getValue(String.class);
                    String city = item.child("city").getValue(String.class);
                    String country = item.child("country").getValue(String.class);
                    String name = item.child("name").getValue(String.class);
                    String language = item.child("language").getValue(String.class);
                    String fund = item.child("fund").getValue(String.class);

                    String text =
                            "Το Πανεπιστήμιο Αθηνών προσφέρει πρόγραμμα Erasmus στο "
                                    + name + " στην πόλη " + city + " της χώρας " + country + ". "
                                    + "Η απαίτηση γλώσσας είναι: " + language + " και η χρηματοδότηση είναι "
                                    + fund + " ευρώ.";

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {
                            try {
                                JSONObject metadata = new JSONObject();
                                metadata.put("Όνομα Πανεπιστημίου", "Πανεπιστήμιο Αθηνών");
                                metadata.put("Όνομα Προγράμματος", name);
                                metadata.put("Χώρα", country);
                                metadata.put("Πόλη", city);
                                metadata.put("Γλώσσα", language);
                                metadata.put("Χρηματοδότηση", fund);
                                metadata.put("Περιγραφή", text);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadata,
                                        "erasmus-EKPA",
                                        "Education"
                                );

                            } catch (Exception e) {
                                Log.e("INDEX", "EKPA error", e);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", errorMessage);
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

    // --------------------------- PAPEI ---------------------------
    public void indexErasmusPAPEI() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("erasmus");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    String uniName = item.child("university_name").getValue(String.class);
                    if (!Objects.equals(uniName, "University of Piraeus")) continue;

                    String id = item.child("id").getValue(String.class);
                    String city = item.child("city").getValue(String.class);
                    String country = item.child("country").getValue(String.class);
                    String name = item.child("name").getValue(String.class);
                    String language = item.child("language").getValue(String.class);
                    String fund = item.child("fund").getValue(String.class);

                    String text =
                            "Το Πανεπιστήμιο Πειραιά προσφέρει πρόγραμμα Erasmus στο "
                                    + name + " στην πόλη " + city + " της χώρας " + country + ". "
                                    + "Η απαίτηση γλώσσας είναι: " + language + " και η χρηματοδότηση είναι "
                                    + fund + " ευρώ.";

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {
                            try {
                                JSONObject metadata = new JSONObject();
                                metadata.put("Όνομα Πανεπιστημίου", "Πανεπιστήμιο Πειραιά");
                                metadata.put("Όνομα Προγράμματος", name);
                                metadata.put("Χώρα", country);
                                metadata.put("Πόλη", city);
                                metadata.put("Γλώσσα", language);
                                metadata.put("Χρηματοδότηση", fund);
                                metadata.put("Περιγραφή", text);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadata,
                                        "erasmus-PAPEI",
                                        "Education"
                                );

                            } catch (Exception e) {
                                Log.e("INDEX", "PAPEI error", e);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", errorMessage);
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

    // --------------------------- CRETE ---------------------------
    public void indexErasmusCRETE() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("erasmus");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    String uniName = item.child("university_name").getValue(String.class);
                    if (!Objects.equals(uniName, "University of Crete")) continue;

                    String id = item.child("id").getValue(String.class);
                    String city = item.child("city").getValue(String.class);
                    String country = item.child("country").getValue(String.class);
                    String name = item.child("name").getValue(String.class);
                    String language = item.child("language").getValue(String.class);
                    String fund = item.child("fund").getValue(String.class);

                    String text =
                            "Το Πανεπιστήμιο Κρήτης προσφέρει πρόγραμμα Erasmus στο "
                                    + name + " στην πόλη " + city + " της χώρας " + country + ". "
                                    + "Η απαίτηση γλώσσας είναι: " + language + " και η χρηματοδότηση είναι "
                                    + fund + " ευρώ.";

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {
                            try {
                                JSONObject metadata = new JSONObject();
                                metadata.put("Όνομα Πανεπιστημίου", "Πανεπιστήμιο Κρήτης");
                                metadata.put("Όνομα Προγράμματος", name);
                                metadata.put("Χώρα", country);
                                metadata.put("Πόλη", city);
                                metadata.put("Γλώσσα", language);
                                metadata.put("Χρηματοδότηση", fund);
                                metadata.put("Περιγραφή", text);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadata,
                                        "erasmus-CRETE",
                                        "Education"
                                );

                            } catch (Exception e) {
                                Log.e("INDEX", "CRETE error", e);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", errorMessage);
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

    // --------------------------- ARISTOTLE ---------------------------
    public void indexErasmusARISTOTLE() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("erasmus");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    String uniName = item.child("university_name").getValue(String.class);
                    if (!Objects.equals(uniName, "Aristotle University")) continue;

                    String id = item.child("id").getValue(String.class);
                    String city = item.child("city").getValue(String.class);
                    String country = item.child("country").getValue(String.class);
                    String name = item.child("name").getValue(String.class);
                    String language = item.child("language").getValue(String.class);
                    String fund = item.child("fund").getValue(String.class);

                    String text =
                            "Το Αριστοτέλειο Πανεπιστήμιο Θεσσαλονίκης προσφέρει πρόγραμμα Erasmus στο "
                                    + name + " στην πόλη " + city + " της χώρας " + country + ". "
                                    + "Η απαίτηση γλώσσας είναι: " + language + " και η χρηματοδότηση είναι "
                                    + fund + " ευρώ.";

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {
                            try {
                                JSONObject metadata = new JSONObject();
                                metadata.put("Όνομα Πανεπιστημίου", "Αριστοτέλειο Πανεπιστήμιο Θεσσαλονίκης");
                                metadata.put("Όνομα Προγράμματος", name);
                                metadata.put("Χώρα", country);
                                metadata.put("Πόλη", city);
                                metadata.put("Γλώσσα", language);
                                metadata.put("Χρηματοδότηση", fund);
                                metadata.put("Περιγραφή", text);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadata,
                                        "erasmus-ARISTOTLE",
                                        "Education"
                                );

                            } catch (Exception e) {
                                Log.e("INDEX", "ARISTOTLE error", e);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", errorMessage);
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

    // --------------------------- HAROKOPIO ---------------------------
    public void indexErasmusHAROKOPIO() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("erasmus");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    String uniName = item.child("university_name").getValue(String.class);
                    if (!Objects.equals(uniName, "Harokopio University")) continue;

                    String id = item.child("id").getValue(String.class);
                    String city = item.child("city").getValue(String.class);
                    String country = item.child("country").getValue(String.class);
                    String name = item.child("name").getValue(String.class);
                    String language = item.child("language").getValue(String.class);
                    String fund = item.child("fund").getValue(String.class);

                    String text =
                            "Το Χαροκόπειο Πανεπιστήμιο προσφέρει πρόγραμμα Erasmus στο "
                                    + name + " στην πόλη " + city + " της χώρας " + country + ". "
                                    + "Η απαίτηση γλώσσας είναι: " + language + " και η χρηματοδότηση είναι "
                                    + fund + " ευρώ.";

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {
                            try {
                                JSONObject metadata = new JSONObject();
                                metadata.put("Όνομα Πανεπιστημίου", "Χαροκόπειο Πανεπιστήμιο");
                                metadata.put("Όνομα Προγράμματος", name);
                                metadata.put("Χώρα", country);
                                metadata.put("Πόλη", city);
                                metadata.put("Γλώσσα", language);
                                metadata.put("Χρηματοδότηση", fund);
                                metadata.put("Περιγραφή", text);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadata,
                                        "erasmus-HAROKOPIO",
                                        "Education"
                                );

                            } catch (Exception e) {
                                Log.e("INDEX", "HAROKOPIO error", e);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", errorMessage);
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

    // --------------------------- IONIAN ---------------------------
    public void indexErasmusIONIAN() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("erasmus");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    String uniName = item.child("university_name").getValue(String.class);
                    if (!Objects.equals(uniName, "Ionian University")) continue;

                    String id = item.child("id").getValue(String.class);
                    String city = item.child("city").getValue(String.class);
                    String country = item.child("country").getValue(String.class);
                    String name = item.child("name").getValue(String.class);
                    String language = item.child("language").getValue(String.class);
                    String fund = item.child("fund").getValue(String.class);

                    String text =
                            "Το Ιόνιο Πανεπιστήμιο προσφέρει πρόγραμμα Erasmus στο "
                                    + name + " στην πόλη " + city + " της χώρας " + country + ". "
                                    + "Η απαίτηση γλώσσας είναι: " + language + " και η χρηματοδότηση είναι "
                                    + fund + " ευρώ.";

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {
                            try {
                                JSONObject metadata = new JSONObject();
                                metadata.put("Όνομα Πανεπιστημίου", "Ιόνιο Πανεπιστήμιο");
                                metadata.put("Όνομα Προγράμματος", name);
                                metadata.put("Χώρα", country);
                                metadata.put("Πόλη", city);
                                metadata.put("Γλώσσα", language);
                                metadata.put("Χρηματοδότηση", fund);
                                metadata.put("Περιγραφή", text);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadata,
                                        "erasmus-IONIAN",
                                        "Education"
                                );

                            } catch (Exception e) {
                                Log.e("INDEX", "IONIAN error", e);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", errorMessage);
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

    // --------------------------- THESSALY ---------------------------
    public void indexErasmusTHESSALY() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("erasmus");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    String uniName = item.child("university_name").getValue(String.class);
                    if (!Objects.equals(uniName, "University of Thessaly")) continue;

                    String id = item.child("id").getValue(String.class);
                    String city = item.child("city").getValue(String.class);
                    String country = item.child("country").getValue(String.class);
                    String name = item.child("name").getValue(String.class);
                    String language = item.child("language").getValue(String.class);
                    String fund = item.child("fund").getValue(String.class);

                    String text =
                            "Το Πανεπιστήμιο Θεσσαλίας προσφέρει πρόγραμμα Erasmus στο "
                                    + name + " στην πόλη " + city + " της χώρας " + country + ". "
                                    + "Η απαίτηση γλώσσας είναι: " + language + " και η χρηματοδότηση είναι "
                                    + fund + " ευρώ.";

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {
                            try {
                                JSONObject metadata = new JSONObject();
                                metadata.put("Όνομα Πανεπιστημίου", "Πανεπιστήμιο Θεσσαλίας");
                                metadata.put("Όνομα Προγράμματος", name);
                                metadata.put("Χώρα", country);
                                metadata.put("Πόλη", city);
                                metadata.put("Γλώσσα", language);
                                metadata.put("Χρηματοδότηση", fund);
                                metadata.put("Περιγραφή", text);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadata,
                                        "erasmus-THESSALY",
                                        "Education"
                                );

                            } catch (Exception e) {
                                Log.e("INDEX", "THESSALY error", e);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", errorMessage);
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

    // --------------------------- PELLOPONESE ---------------------------
    public void indexErasmusPELLOPONESE() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference("erasmus");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {

                    String uniName = item.child("university_name").getValue(String.class);
                    if (!Objects.equals(uniName, "University of Peloponnese")) continue;

                    String id = item.child("id").getValue(String.class);
                    String city = item.child("city").getValue(String.class);
                    String country = item.child("country").getValue(String.class);
                    String name = item.child("name").getValue(String.class);
                    String language = item.child("language").getValue(String.class);
                    String fund = item.child("fund").getValue(String.class);

                    String text =
                            "Το Πανεπιστήμιο Πελοποννήσου προσφέρει πρόγραμμα Erasmus στο "
                                    + name + " στην πόλη " + city + " της χώρας " + country + ". "
                                    + "Η απαίτηση γλώσσας είναι: " + language + " και η χρηματοδότηση είναι "
                                    + fund + " ευρώ.";

                    openAIClient.getEmbedding(text, new EmbeddingCallback() {

                        @Override
                        public void onSuccess(float[] embeddingVector) {
                            try {
                                JSONObject metadata = new JSONObject();
                                metadata.put("Όνομα Πανεπιστημίου", "Πανεπιστήμιο Πελοποννήσου");
                                metadata.put("Όνομα Προγράμματος", name);
                                metadata.put("Χώρα", country);
                                metadata.put("Πόλη", city);
                                metadata.put("Γλώσσα", language);
                                metadata.put("Χρηματοδότηση", fund);
                                metadata.put("Περιγραφή", text);

                                pineconeClient.upsertVector(
                                        embeddingVector,
                                        id,
                                        metadata,
                                        "erasmus-PELLOPONESE",
                                        "Education"
                                );

                            } catch (Exception e) {
                                Log.e("INDEX", "PELLOPONESE error", e);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("INDEX", errorMessage);
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