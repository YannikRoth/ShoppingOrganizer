package ch.fhnw.shoppingorganizer;

import android.content.Context;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.Category;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItemBuilder;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListBuilder;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItemBuilder;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testEnum(){
       // Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Category c = Category.getById(2);
        assertEquals("MEAT", c.name());
    }

    @Test
    public void testShoppingItemBuilder(){
        ShoppingItem shoppingItem = new ShoppingItemBuilder()
                .withCategory(Category.VEGETABLES)
                .withPrice(BigDecimal.valueOf(12.30))
                .withItemActive(Globals.STATE_ACTIVE)
                .build();

        assertEquals(BigDecimal.valueOf(12.30), shoppingItem.getPrice());
        assertEquals(Category.VEGETABLES, shoppingItem.getCategory());
    }

    @Test
    public void testShoppigItemList(){
        ShoppingItem shoppingItem = new ShoppingItemBuilder()
                .withCategory(Category.VEGETABLES)
                .withPrice(BigDecimal.valueOf(12.30))
                .withItemActive(Globals.STATE_ACTIVE)
                .build();

        ShoppingListItem shoppingListItem = new ShoppingListItemBuilder()
                .withQuantity(3)
                .withShoppingItem(shoppingItem)
                .build();

        assertEquals(BigDecimal.valueOf(36.9), shoppingListItem.getTotalItemPrice());
    }

    @Test
    public void testShoppingList(){
        ShoppingItem shoppingItem = new ShoppingItemBuilder()
                .withCategory(Category.VEGETABLES)
                .withPrice(BigDecimal.valueOf(12.30))
                .withItemActive(Globals.STATE_ACTIVE)
                .build();

        ShoppingListItem shoppingListItem = new ShoppingListItemBuilder()
                .withQuantity(3)
                .withShoppingItem(shoppingItem)
                .build();

        ShoppingListItem shoppingListItem2 = new ShoppingListItemBuilder()
                .withQuantity(2)
                .withShoppingItem(shoppingItem)
                .build();

        List<ShoppingListItem> myShoppingList = new ArrayList<>();
        myShoppingList.add(shoppingListItem);
        myShoppingList.add(shoppingListItem2);

        ShoppingList shoppingList = new ShoppingListBuilder()
                .withListName("TestList")
                .withShoppingListItems(myShoppingList)
                .build();

        assertEquals(BigDecimal.valueOf(61.5), shoppingList.getTotalPrice());
        assertEquals(5, shoppingList.getTotalQuantity());
    }
}