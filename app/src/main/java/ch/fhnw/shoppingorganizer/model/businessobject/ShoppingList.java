package ch.fhnw.shoppingorganizer.model.businessobject;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.math.BigDecimal;
import java.util.List;

import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListRepository;

@RequiresApi(api = Build.VERSION_CODES.N)
@Table(name="ShoppingList")
public class ShoppingList extends Model implements Comparable<ShoppingList> {

    @Column(name="listName")
    private String listName;

    private List<ShoppingListItem> shoppingListItems;

    public ShoppingList(){}

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<ShoppingListItem> getShoppingListItems() {
        return getShoppingListItemsFromDB(false);
    }

    public ShoppingListItem getShoppingListItem(ShoppingItem shoppingItem) {
        for (ShoppingListItem it:this.getShoppingListItems())
            if(it.getShoppingItem().equals(shoppingItem))
                return it;
        return null;
    }

    public void setShoppingListItems(List<ShoppingListItem> shoppingListItems) {
        this.shoppingListItems = shoppingListItems;
    }

    public BigDecimal getTotalPrice(){
        return getShoppingListItemsFromDB(false).stream()
                .map(listItem -> listItem.getTotalItemPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }


    public long getTotalQuantity(){
        return getShoppingListItemsFromDB(false).stream()
                .map(listItem -> listItem.getQuantity())
                .reduce(Long.valueOf(0), Long::sum);
    }

    private List<ShoppingListItem> getShoppingListItemsFromDB(boolean bypassCache){
        if(this.shoppingListItems == null || bypassCache){
            this.shoppingListItems = RepositoryProvider.getShoppingListRepositoryInstance().getShoppingListItems(this);
        }
        return this.shoppingListItems;
    }

    @Override
    public String toString() {
        return "ShoppingList{ID: " + getId() +
                ",listName='" + listName + '\'' +
                '}';
    }

    @Override
    public int compareTo(ShoppingList o) {
        return this.getListName().compareTo(o.getListName());
    }
}
