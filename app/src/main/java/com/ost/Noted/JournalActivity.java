package com.ost.Noted;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ost.Noted.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class JournalActivity extends AppCompatActivity implements FirestoreAdapter.OnListItemClick {
    public static final String ID = "com.ost.firebaseapp.ID";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private static final String TAG = "JournalActivity";
    private RecyclerView mFirestoreList;
    private FirestoreAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirestoreList = findViewById(R.id.firestore_list);
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("journals").whereEqualTo("uid", currentUser.getUid());
//        Query query = db.collection("journals");
        FirestoreRecyclerOptions<JournalModel> options = new FirestoreRecyclerOptions.Builder<JournalModel>()
                .setQuery(query, JournalModel.class)
                .build();

        adapter = new FirestoreAdapter(options, this);

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new MaterialAlertDialogBuilder(JournalActivity.this))
                        .setTitle(getString(R.string.dialog_title))
                        .setView(input)
                        .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String journal_name = input.getText().toString();
                                                Map<String, Object> journal = new HashMap<>();
                                                journal.put("uid", currentUser.getUid());
                                                journal.put("name", journal_name);
                                                db.collection("journals")
                                                        .add(journal)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error adding document", e);
                                                            }
                                                        });
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();

                Log.d(TAG, "ok");
                Toast.makeText(getApplicationContext(), "Data Added", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onItemClick(String id, int position) {
        Log.d(TAG, "onItemClick: clicked " + position + " with id " + id);
        Intent intent = new Intent(this, NotesActivity.class);
        intent.putExtra(ID, id);
        startActivity(intent);
    }
}