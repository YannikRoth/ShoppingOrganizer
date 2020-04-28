package ch.fhnw.shoppingorganizer.model.datatransfer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingItemRepository;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListItemRepository;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListRepository;

public class DataExporter {
    private static ShoppingItemRepository itemRepository = RepositoryProvider.getShoppingItemRepositoryInstance();
    private static ShoppingListItemRepository listItemRepository = RepositoryProvider.getShoppingListItemRepositoryInstance();
    private static ShoppingListRepository listRepository = RepositoryProvider.getShoppingListRepositoryInstance();

    public static JSONObject serializeToJsonFromDatabase() throws JSONException {
        //read all shopping items
        List<ShoppingItem> items = itemRepository.getAllItems();

        //read all ShoppingListItems
        List<ShoppingListItem> listItems = listItemRepository.getAllItems();

        //read all shoppingLists
        List<ShoppingList> lists = listRepository.getAllItems();

        return serializeToJson(items, listItems, lists);
    }

    public static JSONObject serializeShoppingListFromDatabase(ShoppingList shoppingList) throws JSONException {
        //only export one specific list, so simply add parameter as only list element
        List<ShoppingList> inScopeShoppingList = new ArrayList<>();
        inScopeShoppingList.add(shoppingList);

        //retrieve only shopping list elements which are linked to given shopping list
        List<ShoppingListItem> shoppingListItems = listRepository.getShoppingListItems(shoppingList);

        //get used ShoppingItems in this list
        List<ShoppingItem> shoppingItems = listItemRepository.getShoppingItems(shoppingList);

        return serializeToJson(shoppingItems, shoppingListItems, inScopeShoppingList);
    }

    public static JSONObject serializeToJson(List<ShoppingItem> items, List<ShoppingListItem> listItems, List<ShoppingList> lists) throws JSONException {
        // Serialize all items
        JSONObject itemsInJson = new JSONObject();
        for (ShoppingItem item : items) {
            itemsInJson.put(String.valueOf(item.getId()), item.toJson());
        }

        // Serialize all shopping list items
        JSONObject listItemsInJson = new JSONObject();
        for (ShoppingListItem listItem : listItems) {
            listItemsInJson.put(String.valueOf(listItem.getId()), listItem.toJson());
        }

        // Serialize all shopping lists
        JSONObject listsInJson = new JSONObject();
        for (ShoppingList list : lists) {
            listsInJson.put(String.valueOf(list.getId()), list.toJson());
        }

        // Combine all lists into one
        JSONObject data = new JSONObject();
        data.put("items", itemsInJson);
        data.put("listItems", listItemsInJson);
        data.put("lists", listsInJson);

        // Return JSON object
        return data;
    }
}
