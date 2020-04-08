package ch.fhnw.shoppingorganizer.model.businessobject;

import java.math.BigDecimal;

public class ShoppingListItem {
    private long id;
    private long quantity;
    private boolean itemState; //when true, item is selected
    private ShoppingItem shoppingItem;
    private ShoppingList shoppingList;

    protected ShoppingListItem(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity < 0 ? 0 : quantity;
    }

    public boolean isItemState() {
        return itemState;
    }

    public void setItemState(boolean itemState) {
        this.itemState = itemState;
    }

    public BigDecimal getTotalItemPrice(){
        return this.shoppingItem.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public ShoppingItem getShoppingItem() {
        return shoppingItem;
    }

    public void setShoppingItem(ShoppingItem shoppingItem) {
        this.shoppingItem = shoppingItem;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

}
