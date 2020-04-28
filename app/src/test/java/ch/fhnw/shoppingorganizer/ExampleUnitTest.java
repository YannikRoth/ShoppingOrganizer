package ch.fhnw.shoppingorganizer;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
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
import ch.fhnw.shoppingorganizer.model.datatransfer.DataExporter;

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
    public void testEnum() {
        // Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Category c = Category.getById(2);
        assertEquals("MEAT", c.name());
    }

    @Test
    public void testShoppingItemBuilder() {
        ShoppingItem shoppingItem = new ShoppingItemBuilder()
                .withCategory(Category.VEGETABLES)
                .withPrice(BigDecimal.valueOf(12.30))
                .withItemActive(Globals.SHOPPING_ITEM_STATE_ACTIVE)
                .build();

        assertEquals(BigDecimal.valueOf(12.30), shoppingItem.getPrice());
        assertEquals(Category.VEGETABLES, shoppingItem.getCategory());
    }

    @Test
    public void testShoppigItemList() {
        ShoppingItem shoppingItem = new ShoppingItemBuilder()
                .withCategory(Category.VEGETABLES)
                .withPrice(BigDecimal.valueOf(12.30))
                .withItemActive(Globals.SHOPPING_ITEM_STATE_ACTIVE)
                .build();

        ShoppingListItem shoppingListItem = new ShoppingListItemBuilder()
                .withQuantity(3)
                .withShoppingItem(shoppingItem)
                .build();

        assertEquals(BigDecimal.valueOf(36.9), shoppingListItem.getTotalItemPrice());
    }

    @Test
    public void testShoppingList() {
        ShoppingItem shoppingItem = new ShoppingItemBuilder()
                .withCategory(Category.VEGETABLES)
                .withPrice(BigDecimal.valueOf(12.30))
                .withItemActive(Globals.SHOPPING_ITEM_STATE_ACTIVE)
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

    @Test
    public void testImport() {

    }

    @Test
    public void testExport() {
        // Setup
        List<ShoppingItem> items = new ArrayList<>();
        ShoppingItem item1 = new ShoppingItemBuilder()
                .withPrice(BigDecimal.valueOf(12.30))
                .withItemName("Test Name")
                .withItemActive(Globals.SHOPPING_ITEM_STATE_ACTIVE)
                .withImgPath("/some/path")
                .withCategory(Category.VEGETABLES)
                .build();
        items.add(item1);

        List<ShoppingList> lists = new ArrayList<>();
        List<ShoppingListItem> listItems = new ArrayList<>();
        ShoppingList list1 = new ShoppingListBuilder()
                .withListName("Test List")
                .withShoppingListItems(listItems)
                .build();
        lists.add(list1);

        ShoppingListItem listItem1 = new ShoppingListItemBuilder()
                .withQuantity(3)
                .withItemState(Globals.SHOPPING_LIST_ITEM_STATE_CHECKED)
                .withShoppingItem(item1)
                .withShoppingList(list1)
                .build();
        listItems.add(listItem1);

        try {
            JSONObject data = DataExporter.serializeToJson(items, listItems, lists);
            System.out.println(data.toString(4));

            System.out.println("####################################################");

            JSONObject data2 = DataExporter.serializeToJsonFromDatabase();
            System.out.println(data2.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}