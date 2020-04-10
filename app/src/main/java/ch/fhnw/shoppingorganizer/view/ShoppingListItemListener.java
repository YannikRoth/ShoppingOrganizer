package ch.fhnw.shoppingorganizer.view;

import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;

/**
 * The interface which should be connection with [{@link ShoppingListActivity}] and the [{@link ShoppingListAdapter}]
 */
public interface ShoppingListItemListener {
    void onSwipeLeft(int position);
    void onSwipeRight(int position);
    void onHoldItem(ShoppingItem itemName);
}
