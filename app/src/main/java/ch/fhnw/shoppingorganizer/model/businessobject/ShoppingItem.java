package ch.fhnw.shoppingorganizer.model.businessobject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

@Table(name="ShoppingItem")
public class ShoppingItem extends Model {

    @Column(name="price")
    private BigDecimal price;

    @Column(name="itemName")
    private String itemName;

    @Column(name="itemActive")
    private boolean itemActive;

    @Column(name="itemPath")
    private String imgPath;

    @Column(name = "category", notNull = true, onNullConflict = Column.ConflictAction.FAIL)
    private Category category;

    public ShoppingItem(){

    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isItemActive() {
        return itemActive;
    }

    public void setItemActive(boolean itemActive) {
        this.itemActive = itemActive;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String toStringSimple() {
        return this.getItemName() + (this.isItemActive() ? "":" (Inactive)");
    }

    @Override
    public String toString() {
        return this.getItemName() + " [ID:" + this.getId() +"," + this.getCategory() + "," + this.getPrice() + " CHF," + this.getImgPath() + "," + this.isItemActive() + "]";
    }

    public JSONObject toJson(){
        try {
            JSONObject json = new JSONObject();
            json.put("id", getId());
            json.put("price", price);
            json.put("itemName", itemName);
            json.put("itemActive", itemActive);
            json.put("imgPath", imgPath);
            json.put("category", category.toString());
            return json;
        } catch (JSONException e) {
            return null;
        }
    }
}
