package ch.fhnw.shoppingorganizer.view;

import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * Implementation of [{@link ItemTouchHelper.Callback}] which will handle the swipes and notify the adapter through [{@link ItemTouchHelperAdapter}
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    private final ShoppingListAdapter mAdapter;
    private ShoppingListActivity shoppingListActivity;

    public SimpleItemTouchHelperCallback(ShoppingListAdapter adapter, ShoppingListActivity shoppingListActivity) {
        super(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
        this.mAdapter = adapter;
        this.shoppingListActivity = shoppingListActivity;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * Disabled the drag action. Only swipe actions.
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * Handle the swiped event. Check the direction of the swipe
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        if (i == ItemTouchHelper.LEFT) {
            Log.d("onSwiped", "Swiped to the left");
            mAdapter.onSwipedLeft(viewHolder.getAdapterPosition());
        } else if (i == ItemTouchHelper.RIGHT) {
            mAdapter.onSwipedRight(viewHolder.getAdapterPosition());
            Log.d("onSwiped", "Swiped to the right");
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(shoppingListActivity, R.color.colorDelete))
                .addSwipeLeftActionIcon(R.drawable.ic_delete_sweep)
                .addSwipeRightBackgroundColor(ContextCompat.getColor(shoppingListActivity, R.color.colorCheck))
                .addSwipeRightActionIcon(R.drawable.ic_check)
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemSelected();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
        itemViewHolder.onItemClear();
    }
}