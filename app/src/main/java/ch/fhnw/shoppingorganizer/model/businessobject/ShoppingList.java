package ch.fhnw.shoppingorganizer.model.businessobject;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.math.BigDecimal;
import java.util.List;

@Table(name="ShoppingList")
public class ShoppingList extends Model {

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
        return shoppingListItems;
    }

    public void setShoppingListItems(List<ShoppingListItem> shoppingListItems) {
        this.shoppingListItems = shoppingListItems;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public BigDecimal getTotalPrice(){
        BigDecimal result = BigDecimal.ZERO;
        if(this.shoppingListItems != null){
            result = this.shoppingListItems.stream()
                    .map(listItem -> listItem.getTotalItemPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public long getTotalQuantity(){
        long result = 0;
        if(this.shoppingListItems != null){
            result = this.shoppingListItems.stream()
                    .map(listItem -> listItem.getQuantity())
                    .reduce(Long.valueOf(0), Long::sum);
        }
        return result;
    }
}
