package ch.fhnw.shoppingorganizer.model.businessobject;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.math.BigDecimal;
import java.util.List;

public class ShoppingList {
    private long id;
    private String listName;
    private List<ShoppingListItem> shoppingListItems;

    protected ShoppingList(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
