package com.app.notemii;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {
    public static String TAG = "BoardActivity";
    public static String NOTE_ID = "NOTE_ID";
    public static String NOTE_TITLE = "NOTE_TITLE";
    public static String NOTE_CONTENT = "NOTE_CONTENT";
    public static String NOTE_ALARM_DATE = "NOTE_ALARM_DATE";
    public static String NOTE_ALARM_TIME = "NOTE_ALARM_TIME";

    private ArrayList<Note> notes;
    private RecyclerView listView;
    private NoteAdapter noteAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ReadAndWriteSnippets snippets;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        notes = new ArrayList<>();
        snippets = new ReadAndWriteSnippets(this);
        setUpRecycleView();
        pb= findViewById(R.id.board_pb);
    }

    private void setUpRecycleView() {
        listView = findViewById(R.id.note_list);
        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        noteAdapter = new NoteAdapter(notes,this, snippets);
        listView.setAdapter(noteAdapter);
        listView.addOnItemTouchListener(new RecyclerItemClickListener(this,listView, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position, NoteAdapter.NoteItemViewHolder holder) {
                if(!noteAdapter.getClicked(position)) {
                    noteAdapter.setClicked(position, true);
                    holder.hiddenLayout.setVisibility(View.VISIBLE);
                } else {
                    noteAdapter.setClicked(position, false);
                    holder.hiddenLayout.setVisibility(View.GONE);
                }
                noteAdapter.notifyItemChanged(position);
            }

            @Override
            public void onItemLongClick(View view, final int position, NoteAdapter.NoteItemViewHolder holder) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                // Add the buttons
                builder.setTitle(R.string.delete_note)
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked Yes button
                                //snippets.deleteNote(notes.get(position).getId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                                noteAdapter.remove(position);
                            }
                        });
                builder.setNegativeButton(R.string.no, null)
                        .create().show();
            }
        }) );

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(noteAdapter, new SwipeToDeleteCallback.SwipeCallBacks() {
            @Override
            public void onLeftSwipe(int itemPosition) {
                Intent intent = new Intent(getApplicationContext(), AddAndEditNoteActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString(NOTE_ID, notes.get(itemPosition).getId());
                bundle.putString(NOTE_TITLE, notes.get(itemPosition).getTitle());
                bundle.putString(NOTE_CONTENT, notes.get(itemPosition).getContent());
                bundle.putString(NOTE_ALARM_DATE, notes.get(itemPosition).getAlarmDate());
                bundle.putString(NOTE_ALARM_TIME, notes.get(itemPosition).getAlarmTime());

                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }

            @Override
            public void onRightSwipe(int itemPosition) {
                noteAdapter.remove(itemPosition);
            }
        }));

        itemTouchHelper.attachToRecyclerView(listView);
    }

    public void onAddButtonClick(View view) {
        startActivity(new Intent(this, AddAndEditNoteActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notes.clear();
        snippets.readNotes(noteAdapter, FirebaseAuth.getInstance().getUid(), pb);
    }
}


