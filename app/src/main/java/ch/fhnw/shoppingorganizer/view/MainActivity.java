package ch.fhnw.shoppingorganizer.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.math.BigDecimal;
import java.util.List;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.businessobject.Category;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItemBuilder;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListBuilder;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItemBuilder;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListRepository;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runDatabaseQueries();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void runDatabaseQueries(){
//        //create the shopping list
        ShoppingList shoppingList = new ShoppingListBuilder()
                .withListName("Kleiderliste")
                .build();
//        shoppingList.save();
//
//        ShoppingItem shoppingItem = new ShoppingItemBuilder()
//                .withItemActive(true)
//                .withCategory(Category.VEGETABLES)
//                .withPrice(BigDecimal.valueOf(10))
//                .withItemName("Banana")
//                .build();
//        shoppingItem.save();
//
//        ShoppingItem shoppingItem2 = new ShoppingItemBuilder()
//                .withItemActive(true)
//                .withCategory(Category.MEAT)
//                .withPrice(BigDecimal.valueOf(20))
//                .withItemName("Steak")
//                .build();
//        shoppingItem2.save();
//
//        ShoppingListItem shoppingListItem = new ShoppingListItemBuilder()
//                .withQuantity(1)
//                .withItemState(true)
//                .withShoppingItem(shoppingItem)
//                .withShoppingList(shoppingList)
//                .build();
//        shoppingListItem.save();
//
//        ShoppingListItem shoppingListItem2 = new ShoppingListItemBuilder()
//                .withQuantity(2)
//                .withItemState(true)
//                .withShoppingItem(shoppingItem2)
//                .withShoppingList(shoppingList)
//                .build();
//        shoppingListItem2.save();

        new Select().from(ShoppingList.class).join(ShoppingListItem.class).on("ShoppingList.Id = ShoppingListItem.Id").execute();

        List<ShoppingListItem> myList =
                new Select().from(ShoppingListItem.class).join(ShoppingList.class).on("ShoppingListItem.shoppingList = ShoppingList.Id").execute();

        List<ShoppingListItem> myList2 =
                new Select().from(ShoppingListItem.class).where("shoppingList =?", new String[]{"1"}).execute();

        BigDecimal v = myList.stream().map(e -> e.getTotalItemPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);

        ShoppingList sl = new Select().from(ShoppingList.class).where("Id=1").executeSingle();

        ShoppingListRepository rep = new ShoppingListRepository();
        List<ShoppingListItem> sss = rep.getShoppingListItems(sl);
        BigDecimal vv = sss.stream().map(e -> e.getTotalItemPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);

        ShoppingListRepository repList = new ShoppingListRepository();
        ShoppingList l = repList.getShoppingListById(3);
        List<ShoppingList> abc = repList.getAllItems();
    }
}
