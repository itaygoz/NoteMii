package com.app.notemii;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteItemViewHolder> {
    private ArrayList<Note> mNotes;
    private ArrayList<Boolean> mClicks;
    private ReadAndWriteSnippets mSnippets;
    private Context mContext;
    private Activity mActivity;
    private Note mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private boolean mRecentlyDeletedItemClicked;

    public NoteAdapter(ArrayList<Note> notes,Activity activity, ReadAndWriteSnippets snippets) {
        super();
        this.mNotes = notes;
        this.mClicks = new ArrayList<>(Collections.nCopies(notes.size(), false));
        mSnippets = snippets;
        mActivity = activity;
        mContext=activity;
    }

    public void add(Note note){
        mNotes.add(note);
        mClicks.add(false);
        //notifyDataSetChanged();
        notifyItemInserted(mNotes.size());
    }

    @NonNull
    @Override
    public NoteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteItemViewHolder holder = new NoteItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_clicked, null, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteItemViewHolder holder, final int position) {
        //Get the current note object in the list
        final Note currentNote = mNotes.get(position);

        holder.date.setText(currentNote.getDate());
        holder.title.setText(currentNote.getTitle());
        holder.content.setText(currentNote.getContent());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!mClicks.get(position)) {
//                    mClicks.set(position, true);
//                    holder.hiddenLayout.setVisibility(View.VISIBLE);
//                } else {
//                    mClicks.set(position, false);
//                    holder.hiddenLayout.setVisibility(View.GONE);
//                }
//                notifyItemChanged(position);
//            }
//        });

//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
//                // Add the buttons
//                builder.setTitle(R.string.delete_note)
//                        .setMessage(R.string.are_you_sure)
//                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // User clicked Yes button
//                                mSnippets.deleteNote(mNotes.get(position).getId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                noteAdapter.remove(position);
//                            }
//                        });
//                builder.setNegativeButton(R.string.no, null)
//                        .create().show();
//
//                return true;
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public Object remove(int position){
        Note t = mNotes.remove(position);
        boolean clicked = mClicks.remove(position);
        mRecentlyDeletedItem = t;
        mRecentlyDeletedItemPosition = position;
        mRecentlyDeletedItemClicked = clicked;
        notifyItemRemoved(position);
//        mSnippets.deleteNote(t.getId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
        showUndoSnackbar();
        return t;
    }

    public boolean getClicked(int position){
        return mClicks.get(position);
    }

    public void setClicked(int position, boolean flg){
        mClicks.set(position,flg);
    }

    public Context getContext() {
        return mContext;
    }

    private void showUndoSnackbar() {
        View view = mActivity.findViewById(R.id.coordinator_layout);
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotes.add(mRecentlyDeletedItemPosition,
                        mRecentlyDeletedItem);
                mClicks.add(mRecentlyDeletedItemPosition,mRecentlyDeletedItemClicked);
                notifyItemInserted(mRecentlyDeletedItemPosition);
            }
        }).addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT)
                    mSnippets.deleteNote(mRecentlyDeletedItem.getId(), FirebaseAuth.getInstance().getCurrentUser().getUid());

            }
        }).show();
    }

    static class NoteItemViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView title;
        TextView content;
        RelativeLayout hiddenLayout;

        public NoteItemViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_textView_note);
            title = itemView.findViewById(R.id.title_textView_note);
            content = itemView.findViewById(R.id.note_content_note);
            hiddenLayout = itemView.findViewById(R.id.clicked_note_layout);
        }
    }
}

