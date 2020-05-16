package ch.fhnw.shoppingorganizer.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.datatransfer.Zipper;

public class ImportActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        Set<Long> newShoppingListIds = new TreeSet<>();

        try {
            final ZipInputStream zis = new ZipInputStream(getContentResolver().openInputStream(getIntent().getData()));
            newShoppingListIds = Zipper.upzipApplicationData(zis, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //start main app
        final Intent mainIntent = new Intent(this, MainActivity.class);
        final String newIdString = String.join(Globals.STRING_SEPERATOR, newShoppingListIds.stream()
                .map(e -> String.valueOf(e))
                .collect(Collectors.toSet()));
        mainIntent.putExtra(Globals.INTENT_NEW_LIST_IDS_EXTRA,newIdString);
        startActivity(mainIntent);

    }
}
