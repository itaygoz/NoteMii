package com.app.notemii;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback extends  ItemTouchHelper.SimpleCallback{
    private NoteAdapter mAdapter;
    private Drawable deleteIcon;
    private Drawable editIcon;
    private final ColorDrawable deleteBackground;
    private final ColorDrawable editBackground;
    private SwipeCallBacks mListener;

    public SwipeToDeleteCallback(NoteAdapter adapter, SwipeCallBacks listener) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        deleteIcon = ContextCompat.getDrawable(mAdapter.getContext(),
                R.drawable.ic_delete_white_36dp);
        editIcon = ContextCompat.getDrawable(mAdapter.getContext(),
                R.drawable.ic_edit_white_36dp);
        deleteBackground = new ColorDrawable(Color.RED);
        editBackground = new ColorDrawable(Color.GREEN);
        mListener=listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if(direction == ItemTouchHelper.RIGHT){
            mListener.onRightSwipe(position);
            //mAdapter.remove(position);
        }


        if(direction == ItemTouchHelper.LEFT){
            mListener.onLeftSwipe(position);
            //Toast.makeText(mAdapter.getContext(), "Edit!!!", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
//            int iconLeft = itemView.getLeft() + iconMargin + deleteIcon.getIntrinsicWidth();
//            int iconRight = itemView.getLeft() + iconMargin;
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin+ deleteIcon.getIntrinsicWidth();
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            deleteBackground.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - editIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            editBackground.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            deleteBackground.setBounds(0, 0, 0, 0);
            editBackground.setBounds(0, 0, 0, 0);
        }

        deleteBackground.draw(c);
        deleteIcon.draw(c);
        editBackground.draw(c);
        editIcon.draw(c);
    }

    public interface SwipeCallBacks{
        void onLeftSwipe(int itemPosition);
        void onRightSwipe(int itemPosition);
    }
}
