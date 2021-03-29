package com.ost.noted;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class NotesActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final String TAG = "NotesActivity";
    private RecyclerView NoteList;
    private FirestoreRecyclerAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mNoteEditText;
    private Button mSendButton;
    private ImageView mAddNoteImageView;
    private static FirebaseStorage storage;
    private static final int REQUEST_IMAGE = 10;
    private static final int NOTE_TYPE_TEXT = 0;
    private static final int NOTE_TYPE_IMAGE = 1;
    private String id;

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView noteTextView;
        public NoteViewHolder(View v) {
            super(v);
            noteTextView = itemView.findViewById(R.id.noteTextView);
        }

        public void bind(NoteModel noteModel) {
            noteTextView.setText(noteModel.getText());
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView noteImageView;
        public ImageViewHolder(View v) {
            super(v);
            noteImageView = itemView.findViewById(R.id.noteImageView);
        }

        public void bind(NoteModel noteModel) {
            Glide.with(itemView.getContext()).load(noteModel.getImage()).into(noteImageView);
            noteImageView.setClipToOutline(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Intent intent = getIntent();
        id = intent.getStringExtra(JournalActivity.ID);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Query query = db.collection("journals").document(id).collection("notes").orderBy("ts");
        FirestoreRecyclerOptions<NoteModel> options = new FirestoreRecyclerOptions.Builder<NoteModel>()
                .setQuery(query, NoteModel.class)
                .build();

        NoteList = findViewById(R.id.noteRecyclerView);
        adapter = new FirestoreRecyclerAdapter<NoteModel, RecyclerView.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull NoteModel model) {
                if (getItemViewType(position) == NOTE_TYPE_TEXT) {
                    ((NoteViewHolder) holder).bind(getItem(position));
                } else {
                    ((ImageViewHolder) holder).bind(getItem(position));
                }
            }

            @Override
            public int getItemViewType(int position) {
                if(getItem(position).getNoteType() == 0L) {
                    return NOTE_TYPE_TEXT;
                } else {
                    return NOTE_TYPE_IMAGE;
                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == NOTE_TYPE_TEXT) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_single, parent, false);
                    return new NoteViewHolder(view);
                } else {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_note_single, parent, false);
                    return new ImageViewHolder(view);
                }
            }
        };

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int noteCount = adapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added note.
                if (lastVisiblePosition == -1 || (positionStart >= (noteCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    NoteList.scrollToPosition(positionStart);
                }
            }
        });
        NoteList.setLayoutManager(new LinearLayoutManager(this));
        NoteList.setAdapter(adapter);

        mNoteEditText = (EditText) findViewById(R.id.noteEditText);
        mNoteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteModel note = new NoteModel(mNoteEditText.getText().toString(), 0L);
                db.collection("journals").document(id).collection("notes").add(note);
                mNoteEditText.setText("");
            }
        });

        storage = FirebaseStorage.getInstance("gs://cec21-project.appspot.com");

        mAddNoteImageView = (ImageView) findViewById(R.id.addNoteImageView);
        mAddNoteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "onActivityResult: " + uri);
                    StorageReference storageRef = storage.getReference();
                    StorageReference imageRef = storageRef.child("images/" + "test.jpg");
                    UploadTask uploadTask = imageRef.putFile(uri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.w(TAG, "onFailure: Failed to upload");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            imageRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d("lorem", "onSuccess: " + uri.toString());
                                            NoteModel note = new NoteModel("test.jpg", 1L, uri.toString());
                                            db.collection("journals").document(id).collection("notes").add(note);
                                        }
                                    });
                        }
                    });
                }
            }
        }
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
}