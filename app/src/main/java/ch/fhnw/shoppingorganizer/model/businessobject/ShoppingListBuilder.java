package ch.fhnw.shoppingorganizer.model.businessobject;

import java.util.List;

public class ShoppingListBuilder {
    private String listName;
    private List<ShoppingListItem> shoppingListItems;

    public ShoppingListBuilder(){}

    public ShoppingListBuilder withId(long id){
        return this;
    }

    public ShoppingListBuilder withListName(String listName){
        this.listName = listName;
        return this;
    }

    public ShoppingListBuilder withShoppingListItems(List<ShoppingListItem> shoppingListItems){
        this.shoppingListItems = shoppingListItems;
        return this;
    }

    public ShoppingList build(){
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setListName(this.listName);
        shoppingList.setShoppingListItems(this.shoppingListItems);
        return  shoppingList;
    }
}
