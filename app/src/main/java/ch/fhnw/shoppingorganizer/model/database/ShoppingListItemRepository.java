package ch.fhnw.shoppingorganizer.model.database;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.util.List;

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
}
