package ch.fhnw.shoppingorganizer.model.database;

import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public final class RepositoryProvider {

    private static ShoppingItemRepository shoppingItemRepository;
    private static ShoppingListItemRepository shoppingListItemRepository;
    private static ShoppingListRepository shoppingListRepository;

    public static ShoppingItemRepository getShoppingItemRepositoryInstance(){
        if(shoppingItemRepository == null){
            shoppingItemRepository = new ShoppingItemRepository();
        }
        return shoppingItemRepository;
    }

    public static ShoppingListItemRepository getShoppingListItemRepositoryInstance(){
        if(shoppingListItemRepository == null){
            shoppingListItemRepository = new ShoppingListItemRepository();
        }
        return shoppingListItemRepository;
    }


    public static ShoppingListRepository getShoppingListRepositoryInstance(){
        if(shoppingListRepository == null){
            shoppingListRepository = new ShoppingListRepository();
        }
        return shoppingListRepository;
    }
}
