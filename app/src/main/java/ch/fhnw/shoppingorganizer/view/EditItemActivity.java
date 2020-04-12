package ch.fhnw.shoppingorganizer.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.Category;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItemBuilder;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingItemRepository;

import static ch.fhnw.shoppingorganizer.view.ShoppingListActivity.SHOPPING_ITEM_ID;
import static ch.fhnw.shoppingorganizer.view.ShoppingListActivity.SHOPPING_LIST_NAME;

public class EditItemActivity extends AppCompatActivity {

    private Toolbar editItemToolbar;
    private EditText edtName;
    private EditText edtPrice;
    private Switch activeSwitch;
    private ImageView itemImage;
    private Spinner categoryList;

    private ShoppingItem shoppingItem;
    private String shoppingListName;
    private static final int PIC_ID = 123;
    private static final int SELECT_PICTURE = 345;

    private final static ShoppingItemRepository shoppingItemRepository = RepositoryProvider.getShoppingItemRepositoryInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        shoppingListName = getIntent().getStringExtra(SHOPPING_LIST_NAME);
        shoppingItem = shoppingItemRepository
                .getShoppingItemById(getIntent().getLongExtra(SHOPPING_ITEM_ID, 0));

        //if new button was clicked, DB will NOT return an object, therefore create one and save it to the database
        if (shoppingItem == null) {
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
        categoryList = findViewById(R.id.categoryList);
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
        edtPrice.setText(String.format("%.2f", shoppingItem.getPrice()));
        activeSwitch = findViewById(R.id.activeSwitch);
        activeSwitch.setChecked(shoppingItem.isItemActive());
        itemImage = findViewById(R.id.imgItem);

        itemImage.setOnClickListener(v -> cameraIntent());

        // TODO Should be get the category from the DB
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Category 1");
        spinnerArray.add("Category 2");
        spinnerArray.add("Category 3");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryList.setAdapter(spinnerArrayAdapter);
    }

    private void cameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        Intent chooser = Intent.createChooser(galleryIntent, "Choose the type of source:");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        startActivityForResult(chooser, PIC_ID);
    }

    public void saveShoppingItem(View v) {
        this.shoppingItem.setItemName(this.<EditText>findCastedViewById(R.id.edtName).getText().toString());
        this.shoppingItem.setPrice(new BigDecimal(this.<EditText>findCastedViewById(R.id.edtPrice).getText().toString()));
        this.shoppingItem.setItemActive(this.<Switch>findCastedViewById(R.id.activeSwitch).isChecked());
        this.shoppingItem.setImgPath("further implementation required...");
        shoppingItemRepository.saveEntity(shoppingItem);

        finish();
    }

    // This method will help to retrieve the image
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id' with requestCode and resultCode
        if (requestCode == PIC_ID && resultCode == RESULT_OK) {
            // Result from the camera intent
            if (data.getExtras() != null) {     // Check if Intent is empty
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // Set the image in ImageView for display
                itemImage.setImageBitmap(photo);
            }

            // Result from the gallery intent
            if (data.getData() != null) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    itemImage.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Helper method to get casted view element
     *
     * @param GUI element id
     * @param <T> Type of GUI element
     * @return the casted type T of the element
     */
    private <T extends View> T findCastedViewById(int id) {
        return (T) findViewById(id);
    }
}
