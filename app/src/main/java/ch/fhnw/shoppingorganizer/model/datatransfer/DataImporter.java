package ch.fhnw.shoppingorganizer.model.datatransfer;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.fhnw.shoppingorganizer.model.businessobject.Category;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItemBuilder;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListBuilder;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItemBuilder;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingItemRepository;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListItemRepository;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListRepository;

public class DataImporter {

    private static final ShoppingItemRepository itemRepository = RepositoryProvider.getShoppingItemRepositoryInstance();
    private static final ShoppingListItemRepository listItemRepository = RepositoryProvider.getShoppingListItemRepositoryInstance();
    private static final ShoppingListRepository listRepository = RepositoryProvider.getShoppingListRepositoryInstance();

    //Returns imported ShoppingLists
    public static List<ShoppingList> unserializeFromJson(String jsonString) throws JSONException {
        JSONObject data = new JSONObject(jsonString);

        List<ShoppingList> newShoppingListIds = new ArrayList<ShoppingList>();

        // Restore all items (ignoring duplicate names)
        JSONObject itemsInJson = data.getJSONObject("items");

        for (Iterator<String> i = itemsInJson.keys(); i.hasNext(); ) {
            JSONObject item = itemsInJson.getJSONObject(i.next());

            // Get all attributes
            String itemName = item.getString("itemName");
            boolean itemActive = item.getBoolean("itemActive");
            String imgPath = item.getString("imgPath");
            Category category = Category.valueOf(item.getString("category"));
            BigDecimal price = BigDecimal.valueOf(item.getLong("price"));

            // Check if the item already exists
            ShoppingItemRepository itemRepository = RepositoryProvider.getShoppingItemRepositoryInstance();
            if (itemRepository.getByName(itemName) != null) {
                continue;
            }

            // Create new item
            ShoppingItem newItem = new ShoppingItemBuilder()
                    .withItemName(itemName)
                    .withItemActive(itemActive)
                    .withImgPath(imgPath)
                    .withCategory(category)
                    .withPrice(price)
                    .build();

            // Save new item in database
            itemRepository.saveEntity(newItem);
        }

        // Restore all lists
        JSONObject listsInJson = data.getJSONObject("lists");

        for (Iterator<String> i = listsInJson.keys(); i.hasNext(); ) {
            JSONObject list = listsInJson.getJSONObject(i.next());

            // Get all attributes
            long listId = list.getLong("id");
            String listName = list.getString("listName");

            // Check if the item already exists
            int nr = 1;
            if (listRepository.getByName(listName) != null) {
                while(listRepository.getByName(listName + " (" + nr + ")") != null)
                    nr++;
                listName = listName + " (" + nr + ")";
            }

            // Create new item
            ShoppingList newItem = new ShoppingListBuilder()
                    .withListName(listName)
                    .build();

            // Save new item in database
            listRepository.saveEntity(newItem);

            newShoppingListIds.add(newItem);

            // Restore all list items if the list itself was also added
            JSONObject listItemsInJson = data.getJSONObject("listItems");

            for (Iterator<String> i2 = listItemsInJson.keys(); i2.hasNext(); ) {
                JSONObject listItem = listItemsInJson.getJSONObject(i2.next());

                // Get all attributes
                long quantity = listItem.getLong("quantity");
                boolean itemState = listItem.getBoolean("itemState");
                long shoppingItemId = listItem.getLong("shoppingItem");
                long shoppingListId = listItem.getLong("shoppingList");

                // Check if we are in the loop of the list we just created
                if (listId != shoppingListId) {
                    continue;
                }

                // Find the existing item inside the local database for further usage
                ShoppingItem shoppingItem = null;
                for (Iterator<String> i3 = itemsInJson.keys(); i3.hasNext(); ) {
                    JSONObject item = itemsInJson.getJSONObject(i3.next());

                    long itemId = item.getLong("id");
                    String itemName = item.getString("itemName");

                    // Check if the item already exists
                    if (shoppingItemId == itemId) {
                        shoppingItem = itemRepository.getByName(itemName);
                        break;
                    }
                }

                // Find the existing list inside the local database for further usage
                ShoppingList shoppingList = listRepository.getByName(listName);

                // Create new item
                ShoppingListItem newItem2 = new ShoppingListItemBuilder()
                        .withQuantity(quantity)
                        .withItemState(itemState)
                        .withShoppingItem(shoppingItem)
                        .withShoppingList(shoppingList)
                        .build();

                // Save new item in database
                listItemRepository.saveEntity(newItem2);
            }
        }
        return newShoppingListIds;
    }
}
