package com.app.notemii;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadAndWriteSnippets {
    public static String TAG = "ReadAndWriteSnippets";
    public static String IMAGE_REF = "images/";
    private Context mContext;
    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference storageRef;

    public ReadAndWriteSnippets(Context mContext) {
        this.mContext = mContext;
        this.mDatabase = FirebaseDatabase.getInstance();
        this.mReference = mDatabase.getReference();
        this.mStorage = FirebaseStorage.getInstance();
        this.storageRef = mStorage.getReference();
    }

    public void writeNote(String noteTitle, String noteContent, String date, String alarmDate, String alarmTime, String userId) {
        mReference = mDatabase.getReference().child("users").child(userId);
        String key = mReference.push().getKey();
        Note note = new Note(key,noteTitle, noteContent, date, alarmDate,alarmTime);
        Map<String, Object> noteValues = note.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, noteValues);

        mReference.updateChildren(childUpdates);
    }

    public void editNote(String noteId,String noteTitle, String noteContent, String date, String alarmDate, String alarmTime, String userId){
        mReference = mDatabase.getReference().child("users").child(userId).child(noteId);
        Note note = new Note(noteId, noteTitle, noteContent, date, alarmDate, alarmTime);
        Map<String, Object> noteValues = note.toMap();

        mReference.updateChildren(noteValues);
    }

    public void readNotes(final NoteAdapter notes, String userId, final ProgressBar pb){
        mReference = mDatabase.getReference().child("users").child(userId);

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pb.setVisibility(ProgressBar.GONE);
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    notes.add(noteSnapshot.getValue(Note.class));
                    Log.i(TAG, "onDataChange: Note added!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deleteNote(String noteId, String userId){
        mDatabase.getReference().child("users").child(userId).child(noteId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Toast.makeText(mContext, R.string.note_removed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadImage(Uri image, String noteId){
        storageRef.child(IMAGE_REF+noteId+".jpg").putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(mContext,"Upload image succefully!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext,"Upload image failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

//    public void downloadPhoto(String noteId, ImageView imageView, ImageButton button){
//        StorageReference tempRef = storageRef.child(IMAGE_REF+noteId+".jpg");
//
//        Glide.with(mContext)
//                .load(tempRef)
//                .into(imageView);
//
//        imageView.setVisibility(View.VISIBLE);
//
//    }

}
