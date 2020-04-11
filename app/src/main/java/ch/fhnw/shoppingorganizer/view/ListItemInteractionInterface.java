package ch.fhnw.shoppingorganizer.view;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface ListItemInteractionInterface {
    void onItemClick(View view, int position);
    void onLongItemClick(View view, int position);
    int getSwipeDirs();
    void onSwipeLeft(@NonNull RecyclerView.ViewHolder viewHolder);
    void onSwipeRight(@NonNull RecyclerView.ViewHolder viewHolder);
    void onChildDrawDetails(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive);
}
