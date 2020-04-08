package ch.fhnw.shoppingorganizer.model.businessobject;

import java.math.BigDecimal;
import java.nio.file.Path;

public class ShoppingItemBuilder {
    private BigDecimal price;
    private String itemName;
    private boolean itemActive;
    private Path imgPath;

    public ShoppingItemBuilder withPrice(BigDecimal price){
        this.price = price;
        return this;
    }

    public ShoppingItemBuilder withItemName(String itemName){
        this.itemName = itemName;
        return this;
    }

    public ShoppingItemBuilder withItemActive(Boolean isActive){
        this.itemActive = isActive;
        return this;
    }

    public ShoppingItemBuilder withImgPath(Path imgPath){
        this.imgPath = imgPath;
        return this;
    }

    public ShoppingItem build(){
        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setPrice(this.price);
        shoppingItem.setItemName(this.itemName);
        shoppingItem.setItemActive(this.itemActive);
        shoppingItem.setImgPath(this.imgPath);
        return shoppingItem;
    }

}
