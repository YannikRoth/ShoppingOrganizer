package ch.fhnw.shoppingorganizer.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.controller.MinMaxFilter;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.Category;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItemBuilder;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingItemRepository;
import ch.fhnw.shoppingorganizer.view.Tutorial.TutorialType;
import ch.fhnw.shoppingorganizer.view.Tutorial.TutorialSliderActivity;

import static ch.fhnw.shoppingorganizer.view.ShoppingListActivity.SHOPPING_ITEM_ID;
import static ch.fhnw.shoppingorganizer.view.ShoppingListActivity.SHOPPING_LIST_NAME;
import static ch.fhnw.shoppingorganizer.view.Tutorial.TutorialType.TUTORIAL_SHOPPING_ITEM_EDIT;

public class EditItemActivity extends AppCompatActivity {

    private Toolbar editItemToolbar;
    private EditText edtName;
    private EditText edtPrice;
    private Switch activeSwitch;
    private ImageView itemImage;
    private File itemImageFile;
    private Spinner categoryList;

    private ShoppingItem shoppingItem;
    private String shoppingListName;
    private static final int PIC_ID = 123;

    private Intent intent;

    boolean newImage = false;

    private String TAG = this.getClass().getSimpleName();

    private final int QUANTITY_MIN_VALUE = 0;
    private final int QUANTITY_MAX_VALUE = 1000000;

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

        //Reques permissions for picture handling
        String [] permissionsNeeded = new String[3];
        permissionsNeeded[0] = Manifest.permission.CAMERA;
        permissionsNeeded[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permissionsNeeded[2] = Manifest.permission.READ_EXTERNAL_STORAGE;
        Random r = new Random();
        ActivityCompat.requestPermissions(this, permissionsNeeded, r.nextInt(99)+1);

        initUi();

        callTutorial(false);
    }

    private void callTutorial(boolean forceCall) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Globals.PREF_TUTORIAL, MODE_PRIVATE);
        if(forceCall || !prefs.getBoolean(TUTORIAL_SHOPPING_ITEM_EDIT.toString(), false)) {
            Intent intentTutorial = new Intent(this, TutorialSliderActivity.class);
            intentTutorial.putExtra(Globals.INTENT_TUTORIAL_TYPE, TutorialType.TUTORIAL_SHOPPING_ITEM_EDIT.toString());
            startActivity(intentTutorial);
            TutorialSliderActivity.safePreferences(prefs, TUTORIAL_SHOPPING_ITEM_EDIT);
        }
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
        edtPrice.setText(shoppingItem.getPrice().toString());
        edtPrice.setFilters(new InputFilter[]{ new MinMaxFilter(QUANTITY_MIN_VALUE, QUANTITY_MAX_VALUE)});
        activeSwitch = findViewById(R.id.activeSwitch);
        activeSwitch.setChecked(shoppingItem.isItemActive());
        itemImage = findViewById(R.id.imgItem);
        itemImageFile = new File(shoppingItem.getImgPath());
        if(itemImageFile != null)
            presentFileOnView(itemImageFile);

        itemImage.setOnClickListener(v -> cameraIntent());

        List<String> spinnerArray = new ArrayList<>((Arrays.stream(Category.values())
                .map(e -> e.name())
                .collect(Collectors.toList())));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryList.setAdapter(spinnerArrayAdapter);

        //set the default value of spinner
        categoryList.setSelection(this.shoppingItem.getCategory().ordinal());
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

    public void saveShoppingItem(View v){
        if(this.<EditText>findCastedViewById(R.id.edtName).getText().toString().equals("")) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }
        this.shoppingItem.setItemName(this.<EditText>findCastedViewById(R.id.edtName).getText().toString().trim());
        this.shoppingItem.setCategory(Category.valueOf(this.<Spinner>findCastedViewById(R.id.categoryList).getSelectedItem().toString()));
        this.shoppingItem.setPrice(new BigDecimal(this.<EditText>findCastedViewById(R.id.edtPrice).getText().toString()));
        this.shoppingItem.setItemActive(this.<Switch>findCastedViewById(R.id.activeSwitch).isChecked());
        if(itemImageFile.getAbsoluteFile().exists() && newImage)
            this.shoppingItem.setImgPath(itemImageFile.getAbsoluteFile().toString());
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

    private File safeBitmapToFileDirectory(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        dir.mkdir();
        Timestamp ts = new Timestamp(new Date().getTime());
        File file = new File(dir, shoppingItem.getItemName() + ts + ".png");
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(getApplicationContext(), "Image saved to app" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            this.newImage = true;
            return file;
        } catch (IOException e) {
            Log.e(TAG, "safeImageToFileDirectory: " + e.getMessage());
            return null;
        }
    }

    private void presentFileOnView(File file) {
        if(file.exists()) {
            itemImageFile = file;
            Bitmap bitmap = BitmapFactory.decodeFile(itemImageFile.getAbsolutePath());
            itemImage.setImageBitmap(bitmap);
        }
    }

    // This method will help to retrieve the image
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id' with requestCode and resultCode
        if (requestCode == PIC_ID && resultCode == RESULT_OK) {
            // Result from the camera intent
            if (data.getExtras() != null) {     // Check if Intent is empty
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                File photoFile = safeBitmapToFileDirectory(photo);
                if(photoFile != null)
                    presentFileOnView(photoFile);
                else
                    Toast.makeText(this, "No file picked", Toast.LENGTH_SHORT).show();
            }

            // Result from the gallery intent
            if (data.getData() != null) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    File photoFile = safeBitmapToFileDirectory(selectedImage);
                    if(photoFile != null)
                        presentFileOnView(photoFile);
                    else
                        Toast.makeText(this, "No file picked", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showTutorial:
                callTutorial(true);
                break;
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra(SHOPPING_LIST_NAME, shoppingListName);
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to get casted view element
     * @param
     * @param <T> Type of GUI element
     * @return the casted type T of the element
     */
    private <T extends View> T findCastedViewById(int id) {
        return (T) findViewById(id);
    }
}
