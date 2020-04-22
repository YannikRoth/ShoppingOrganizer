package ch.fhnw.shoppingorganizer.model.database;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.util.List;

import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;

public class ShoppingListItemRepository extends AbstractRepository{

    protected ShoppingListItemRepository(){}
    @Override
    public List<ShoppingListItem> getAllItems() {
        return new Select().from(ShoppingListItem.class).execute();
    }

    /**
     * Returns only one shopping list item with provided id.
     * Returns null if tupel with given id does not exist
     */
    public ShoppingListItem getShoppingListById(long id){
        return (ShoppingListItem) super.getById(ShoppingListItem.class, id);
    }

    /**
     * Returns all assigned shopping items in given shoppingList
     */
    public List<ShoppingItem> getShoppingItems(ShoppingList shoppingList){
        return new Select().from(ShoppingItem.class)
                .join(ShoppingListItem.class).on("ShoppingItem.Id = ShoppingListItem.shoppingItem")
                .where("ShoppingListItem.shoppingList=?", new Object[]{shoppingList.getId()})
                .execute();
    }
}
