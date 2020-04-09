package ch.fhnw.shoppingorganizer.view;

/**
 * The interface which should be connection with [{@link ShoppingListActivity}] and the [{@link ShoppingListAdapter}]
 */
public interface ShoppingListItemListener {
    void onSwipeLeft();
    void onSwipeRight();
    void onHoldItem(String itemName);
}
