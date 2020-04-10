package ch.fhnw.shoppingorganizer.view;

/**
 * Interface which will connect the adapter with [{@link SimpleItemTouchHelperCallback}]
 */
public interface ItemTouchHelperAdapter {
    void onSwipedLeft(int position);

    void onSwipedRight(int position);
}