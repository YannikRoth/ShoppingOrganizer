package ch.fhnw.shoppingorganizer.model.database;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.activeandroid.query.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ShoppingListRepository extends AbstractRepository{

    protected ShoppingListRepository(){}

    /**
     * Returns a list of elements found in the ShoppingList table.
     */
    @Override
    public List<ShoppingList> getAllItems(){
        return new Select().from(ShoppingList.class).execute();
    }

    /**
     * Returns only one shopping list with provided id.
     * Returns null if tupel with given id does not exist
     */
    public ShoppingList getShoppingListById(long id){
        return (ShoppingList) super.getById(ShoppingList.class, id);
    }

    /**
     * Returns a list of all ShoppingListItems that have benn placed into this shopping list
     */
    public List<ShoppingListItem> getShoppingListItems(ShoppingList shoppingList){
        return new Select().from(ShoppingListItem.class)
                .where("shoppingList=?", new Object[]{shoppingList.getId()}).execute();
    }

    /**
     * Returns an accumulated price for all item within this shopping list
     */
    public BigDecimal getTotalShoppingListTotalPrice(ShoppingList shoppingList){
        return this.getShoppingListItems(shoppingList)
                .stream()
                .map(item -> item.getTotalItemPrice())
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    /**
     * Returns an accumulated sum of quantities within this shopping list
     */
    public long getTotalShoppingListQuantity(ShoppingList shoppingList){
        return this.getShoppingListItems(shoppingList)
                .stream()
                .map(item -> item.getQuantity())
                .reduce(Long.valueOf(0), Long::sum);
    }


}
