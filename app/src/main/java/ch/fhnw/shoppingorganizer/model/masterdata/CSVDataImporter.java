package ch.fhnw.shoppingorganizer.model.masterdata;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.Buffer;

import ch.fhnw.shoppingorganizer.model.Globals;
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

public class CSVDataImporter {

    private InputStream inputStream;
    public CSVDataImporter(InputStream pathToFile){
        this.inputStream = pathToFile;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void performImport(){
        try {
            InputStreamReader isReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isReader);

            final int categoryIndex = 0;
            final int pathIndex = 1;
            final int itemActiveIndex = 2;
            final int itemNameIndex = 3;
            final int itemPriceIndex = 4;

            String line = "";
            final String delimiter = ";";

            //omit header
            String header = reader.readLine();

            while((line = reader.readLine()) != null){
                String[] lineValues = line.split(delimiter);
                ShoppingItem shoppingItem = new ShoppingItemBuilder()
                        .withCategory(Category.valueOf(lineValues[categoryIndex].toUpperCase()))
                        .withImgPath(lineValues[pathIndex])
                        .withItemActive(Integer.parseInt(lineValues[itemActiveIndex])==1)
                        .withItemName(lineValues[itemNameIndex])
                        .withPrice(new BigDecimal(lineValues[itemPriceIndex]))
                        .build();
                RepositoryProvider.getShoppingItemRepositoryInstance().saveEntity(shoppingItem);
            }

            //add shopping list
            ShoppingListRepository shoppingListRepository = RepositoryProvider.getShoppingListRepositoryInstance();
            ShoppingListItemRepository shoppingListItemRepository = RepositoryProvider.getShoppingListItemRepositoryInstance();
            ShoppingItemRepository shoppingItemRepository = RepositoryProvider.getShoppingItemRepositoryInstance();

            ShoppingList shoppingList = new ShoppingListBuilder()
                    .withListName("Einkaufsliste (IMPORTED)")
                    .build();
            shoppingListRepository.saveEntity(shoppingList);

            ShoppingListItem shoppingListItem = new ShoppingListItemBuilder()
                    .withShoppingList(shoppingList)
                    .withItemState(Globals.STATE_ACTIVE)
                    .withQuantity(3)
                    .withShoppingItem(shoppingItemRepository.getShoppingItemById(2))
                    .build();
            shoppingListItemRepository.saveEntity(shoppingListItem);

            ShoppingListItem shoppingListItem2 = new ShoppingListItemBuilder()
                    .withShoppingList(shoppingList)
                    .withItemState(Globals.STATE_ACTIVE)
                    .withQuantity(1)
                    .withShoppingItem(shoppingItemRepository.getShoppingItemById(5))
                    .build();
            shoppingListItemRepository.saveEntity(shoppingListItem2);

            ShoppingListItem shoppingListItem3 = new ShoppingListItemBuilder()
                    .withShoppingList(shoppingList)
                    .withItemState(Globals.STATE_ACTIVE)
                    .withQuantity(5)
                    .withShoppingItem(shoppingItemRepository.getShoppingItemById(7))
                    .build();
            shoppingListItemRepository.saveEntity(shoppingListItem3);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
