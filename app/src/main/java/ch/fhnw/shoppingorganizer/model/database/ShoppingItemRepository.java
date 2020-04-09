package ch.fhnw.shoppingorganizer.model.database;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.util.List;

import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;

public class ShoppingItemRepository extends AbstractRepository{

    @Override
    public List<ShoppingItem> getAllItems() {
        return new Select().from(ShoppingItem.class).execute();
    }

    /**
     * Returns only one shopping item with provided id.
     * Returns null if tupel with given id does not exist
     */
    public ShoppingItem getShoppingItemById(long id){
        return (ShoppingItem) super.getById(ShoppingItem.class, id);
    }
}
