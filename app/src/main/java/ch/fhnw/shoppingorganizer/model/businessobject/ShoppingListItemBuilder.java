package ch.fhnw.shoppingorganizer.model.businessobject;

import java.math.BigDecimal;

public class ShoppingListItemBuilder {
    private long id;
    private long quantity;
    private BigDecimal totalItemPrice;
    private boolean itemState; //when true, item is selected
    private ShoppingItem shoppingItem;
    private ShoppingList shoppingList;

    public ShoppingListItemBuilder(){
        this.quantity = 0;
        this.totalItemPrice = BigDecimal.ZERO;
    }

    public ShoppingListItemBuilder withId(long id){
        this.id = id;
        return this;
    }

    public ShoppingListItemBuilder withQuantity(long quantity){
        this.quantity = quantity;
        return this;
    }

    public ShoppingListItemBuilder withItemState(boolean itemState){
        this.itemState = itemState;
        return this;
    }

    public ShoppingListItemBuilder withShoppingItem(ShoppingItem shoppingItem){
        this.shoppingItem = shoppingItem;
        updateTotalItemPrice();
        return this;
    }

    public ShoppingListItemBuilder withShoppingList(ShoppingList shoppingList){
        this.shoppingList = shoppingList;
        return this;
    }

    public ShoppingListItem build(){
        ShoppingListItem shoppingListItem = new ShoppingListItem();
        shoppingListItem.setId(this.id);
        shoppingListItem.setQuantity(this.quantity);
        shoppingListItem.setItemState(this.itemState);
        shoppingListItem.setShoppingItem(this.shoppingItem);
        shoppingListItem.setShoppingList(this.shoppingList);
        return shoppingListItem;
    }

    private void updateTotalItemPrice(){
        this.totalItemPrice = this.shoppingItem.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
