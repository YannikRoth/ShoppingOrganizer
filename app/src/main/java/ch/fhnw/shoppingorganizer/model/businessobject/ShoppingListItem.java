package ch.fhnw.shoppingorganizer.model.businessobject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.math.BigDecimal;

@Table(name ="ShoppingListItem")
public class ShoppingListItem extends Model {

    @Column(name = "quantity")
    private long quantity;

    @Column(name="itemState")
    private boolean itemState; //when true, item is selected

    @Column(name="shoppingItem", notNull = true, onNullConflict = Column.ConflictAction.FAIL)
    private ShoppingItem shoppingItem;

    @Column(name="shoppingList", notNull = true, onNullConflict = Column.ConflictAction.FAIL)
    private ShoppingList shoppingList;

    public ShoppingListItem(){}

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

    @Override
    public String toString() {
        return shoppingList + " -> ID:" + getId() + "," +  this.getTotalItemPrice() + " CHF," + this.getQuantity() + "x " + this.getShoppingItem();
    }
}
