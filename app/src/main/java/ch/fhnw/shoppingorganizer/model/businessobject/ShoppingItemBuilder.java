package ch.fhnw.shoppingorganizer.model.businessobject;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.file.Path;

public class ShoppingItemBuilder {
    private long id;
    private BigDecimal price;
    private String itemName;
    private boolean itemActive;
    private Path imgPath;
    private Category category;

    public ShoppingItemBuilder(){
        //set default values
        this.price = BigDecimal.ZERO;
        this.itemName = StringUtils.EMPTY;
        this.itemActive = true;
        this.category = Category.NONE;
    }

    public ShoppingItemBuilder withId(long id){
        this.id = id;
        return this;
    }

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

    public ShoppingItemBuilder withCategory(Category category){
        this.category = category;
        return this;
    }

    public ShoppingItem build(){
        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setPrice(this.price);
        shoppingItem.setItemName(this.itemName);
        shoppingItem.setItemActive(this.itemActive);
        shoppingItem.setImgPath(this.imgPath);
        shoppingItem.setCategory(this.category);
        return shoppingItem;
    }

}
