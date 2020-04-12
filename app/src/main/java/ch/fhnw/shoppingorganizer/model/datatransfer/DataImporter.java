package ch.fhnw.shoppingorganizer.model.datatransfer;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Iterator;

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
    public static void unserializeFromJson(String jsonString) throws JSONException {
        JSONObject data = new JSONObject(jsonString);

        // Restore all items (ignoring duplicate names)
        JSONObject itemsInJson = data.getJSONObject("items");

        for (Iterator<String> i = itemsInJson.keys(); i.hasNext(); ) {
            JSONObject item = itemsInJson.getJSONObject(i.next());

            // Get all attributes
            String itemName = item.getString("itemName");
            boolean itemActive = item.getBoolean("itemActive");
            String imgPath = item.getString("itemImgPath");
            Category category = Category.valueOf(item.getString("itemCategory"));
            BigDecimal price = BigDecimal.valueOf(item.getLong("itemPrice"));

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
            String listName = list.getString("listName");

            // Check if the item already exists
            ShoppingListRepository listRepository = RepositoryProvider.getShoppingListRepositoryInstance();
            if (listRepository.getByName(listName) != null) {
                continue;
            }

            // Create new item
            ShoppingList newItem = new ShoppingListBuilder()
                    .withListName(listName)
                    .build();

            // Save new item in database
            listRepository.saveEntity(newItem);

            // Restore all list items if the list itself was also added
            JSONObject listItemsInJson = data.getJSONObject("listItems");

            for (Iterator<String> i2 = listItemsInJson.keys(); i2.hasNext(); ) {
                JSONObject listItem = listItemsInJson.getJSONObject(i2.next());

                // Get all attributes
                long quantity = listItem.getLong("quantity");
                boolean itemState = listItem.getBoolean("itemState");
                ShoppingItem shoppingItem = (ShoppingItem) listItem.get("shoppingItem"); // TODO: Does this work though?
                ShoppingList shoppingList = (ShoppingList) listItem.get("shoppingList"); // TODO: Does this work though?

                // Check if we are in the loop of the list we just created
                ShoppingListItemRepository listItemRepository = RepositoryProvider.getShoppingListItemRepositoryInstance();
                if (!listName.equals(shoppingList.getListName())) {
                    continue;
                }

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
    }
}
