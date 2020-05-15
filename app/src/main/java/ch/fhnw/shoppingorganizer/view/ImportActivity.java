package ch.fhnw.shoppingorganizer.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.datatransfer.Zipper;

public class ImportActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        List<ShoppingList> newShoppingListIds = new ArrayList<ShoppingList>();

        try {
            InputStream is = getContentResolver().openInputStream(getIntent().getData());
            ZipInputStream zis = new ZipInputStream(is);
            newShoppingListIds = Zipper.upzipApplicationData(zis, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //start main app
        Intent mainIntent = new Intent(this, MainActivity.class);
        String newIdString = String.join(",", newShoppingListIds.stream().map(e -> e.getId().toString()).collect(Collectors.toList()));
        mainIntent.putExtra(Globals.INTENT_NEW_LIST_IDS_EXTRA,newIdString);
        startActivity(mainIntent);

    }
}
