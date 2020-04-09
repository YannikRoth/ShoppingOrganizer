package ch.fhnw.shoppingorganizer.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ch.fhnw.shoppingorganizer.R;

import static ch.fhnw.shoppingorganizer.view.ShoppingListActivity.ITEM_NAME_EXTRA;
import static ch.fhnw.shoppingorganizer.view.ShoppingListActivity.SHOPPING_LIST_NAME;

public class EditItemActivity extends AppCompatActivity {

    private Toolbar editItemToolbar;
    private EditText edtName;
    private EditText edtPrice;
    private Switch activeSwitch;
    private ImageView itemImage;

    private String shoppingListName;
    private String itemName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        shoppingListName = getIntent().getStringExtra(SHOPPING_LIST_NAME);
        itemName = getIntent().getStringExtra(ITEM_NAME_EXTRA);
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
            setSupportActionBar(editItemToolbar);
            // Set back button of Toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        edtName = findViewById(R.id.edtName);
        edtName.setText(itemName);

        edtPrice = findViewById(R.id.edtPrice);
        activeSwitch = findViewById(R.id.activeSwitch);
        itemImage = findViewById(R.id.imgItem);
    }
}
