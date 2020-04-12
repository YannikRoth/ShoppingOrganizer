package ch.fhnw.shoppingorganizer.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.Category;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItemBuilder;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingItemRepository;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListRepository;

import static ch.fhnw.shoppingorganizer.view.ShoppingListActivity.ITEM_NAME_EXTRA;
import static ch.fhnw.shoppingorganizer.view.ShoppingListActivity.SHOPPING_ITEM_ID;
import static ch.fhnw.shoppingorganizer.view.ShoppingListActivity.SHOPPING_LIST_NAME;

public class EditItemActivity extends AppCompatActivity {

    private Toolbar editItemToolbar;
    private EditText edtName;
    private EditText edtPrice;
    private Switch activeSwitch;
    private ImageView itemImage;

    private ShoppingItem shoppingItem;
    private String shoppingListName;

    private Intent intent;


    private final static ShoppingItemRepository shoppingItemRepository = RepositoryProvider.getShoppingItemRepositoryInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.activity_edit_item);
        shoppingListName = getIntent().getStringExtra(SHOPPING_LIST_NAME);
        shoppingItem = shoppingItemRepository
                .getShoppingItemById(intent.getLongExtra(SHOPPING_ITEM_ID, 0));



        //if new button was clicked, DB will NOT return an object, therefore create one and save it to the database
        if(shoppingItem == null){
            ShoppingItem newItem = new ShoppingItemBuilder()
                    .withItemActive(true)
                    .withItemName(Globals.EMPTY_STRING)
                    .withImgPath(Globals.EMPTY_STRING)
                    .withCategory(Category.NONE)
                    .withPrice(BigDecimal.ZERO)
                    .build();

            this.shoppingItem = newItem;
        }

        initUi();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Return the list name to ShoppingList for the toolbar title
            Intent intent = new Intent();
            intent.putExtra(SHOPPING_LIST_NAME, shoppingListName);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initUi() {
        editItemToolbar = findViewById(R.id.toolbarEditItem);
        if (editItemToolbar != null) {
            String title = shoppingItem.getItemName() == Globals.EMPTY_STRING ? "New item" : "Edit: " + shoppingItem.getItemName();
            editItemToolbar.setTitle(title);
            setSupportActionBar(editItemToolbar);
            // Set back button of Toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        edtName = findViewById(R.id.edtName);
        edtName.setText(shoppingItem.getItemName());

        edtPrice = findViewById(R.id.edtPrice);
        edtPrice.setText(Globals.NUMBERFORMAT.format(shoppingItem.getPrice()));
        activeSwitch = findViewById(R.id.activeSwitch);
        activeSwitch.setChecked(shoppingItem.isItemActive());
        itemImage = findViewById(R.id.imgItem);
    }

    public void saveShoppingItem(View v){
        this.shoppingItem.setItemName(this.<EditText>findCastedViewById(R.id.edtName).getText().toString().trim());
        this.shoppingItem.setPrice(new BigDecimal(this.<EditText>findCastedViewById(R.id.edtPrice).getText().toString()));
        this.shoppingItem.setItemActive(this.<Switch>findCastedViewById(R.id.activeSwitch).isChecked());
        this.shoppingItem.setImgPath("further implementation required...");
        shoppingItemRepository.saveEntity(shoppingItem);

        int ShoppingItemId = shoppingItem.getId().intValue();
        if(!shoppingItemRepository.getAllItems().contains(shoppingItem)) {
            shoppingItemRepository.getAllItems().add(shoppingItem);
        }

        Intent result = new Intent();
        result.putExtra(SHOPPING_ITEM_ID, ShoppingItemId);
        setResult(Activity.RESULT_OK, result);

        finish();
    }

    /**
     * Helper method to get casted view element
     * @param GUI element id
     * @param <T> Type of GUI element
     * @return the casted type T of the element
     */
    private <T extends View> T findCastedViewById(int id){
        return (T) findViewById(id);
    }
}
