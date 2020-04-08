package ch.fhnw.shoppingorganizer.model.businessobject;

import java.math.BigDecimal;
import java.nio.file.Path;

public class ShoppingItem {
    private BigDecimal price;
    private String itemName;
    private boolean itemActive;
    private Path imgPath;

    protected ShoppingItem(){

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

    public Path getImgPath() {
        return imgPath;
    }

    public void setImgPath(Path imgPath) {
        this.imgPath = imgPath;
    }
}
