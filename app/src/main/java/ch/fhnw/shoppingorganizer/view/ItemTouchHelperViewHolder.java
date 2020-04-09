package ch.fhnw.shoppingorganizer.view;

import androidx.recyclerview.widget.ItemTouchHelper;

/**
 * Interface which will connect [{@link ShoppingListAdapter.ShoppingListItem}] view holder and [{@link SimpleItemTouchHelperCallback}]
 */
public interface ItemTouchHelperViewHolder {

    /**
     * Called when the {@link ItemTouchHelper} first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected();


    /**
     * Called when the {@link ItemTouchHelper} has completed the move or swipe, and the active item
     * state should be cleared.
     */
    void onItemClear();
}