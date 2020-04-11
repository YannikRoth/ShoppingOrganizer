package ch.fhnw.shoppingorganizer.model.datatransfer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingItemRepository;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListItemRepository;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListRepository;

public class DataExporter {
    public static String serializeToJson() throws JSONException {
        // Serialize all items
        ShoppingItemRepository itemRepository = RepositoryProvider.getShoppingItemRepositoryInstance();
        List<ShoppingItem> items = itemRepository.getAllItems();

        JSONObject itemsInJson = new JSONObject();
        for (ShoppingItem item : items) {
            itemsInJson.put(String.valueOf(item.getId()), item);
        }

        // Serialize all shopping list items
        ShoppingListItemRepository listItemRepository = RepositoryProvider.getShoppingListItemRepositoryInstance();
        List<ShoppingListItem> listItems = listItemRepository.getAllItems();

        JSONObject listItemsInJson = new JSONObject();
        for (ShoppingListItem listItem : listItems) {
            listItemsInJson.put(String.valueOf(listItem.getId()), listItem);
        }

        // Serialize all shopping lists
        ShoppingListRepository listRepository = RepositoryProvider.getShoppingListRepositoryInstance();
        List<ShoppingList> lists = listRepository.getAllItems();

        JSONObject listsInJson = new JSONObject();
        for (ShoppingList list : lists) {
            listsInJson.put(String.valueOf(list.getId()), list);
        }

        // Combine all lists into one
        JSONObject data = new JSONObject();
        data.put("items", itemsInJson);
        data.put("listItems", listItemsInJson);
        data.put("lists", listsInJson);

        // Return a one-line JSON string
        return data.toString();
    }
}
