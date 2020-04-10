package ch.fhnw.shoppingorganizer.view;

/**
 * The interface which should be connection with [{@link MainActivity}] and the [{@link ShoppingListsAdapter}]
 */
public interface ShoppingListsItemListener {
    void onHoldItem(int position);
    void onClickItem(String item);
}
