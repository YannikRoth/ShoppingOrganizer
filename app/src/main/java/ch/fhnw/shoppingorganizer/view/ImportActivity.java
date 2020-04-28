package ch.fhnw.shoppingorganizer.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipInputStream;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.datatransfer.DataImporter;
import ch.fhnw.shoppingorganizer.model.datatransfer.Zipper;

public class ImportActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        try {
            InputStream is = getContentResolver().openInputStream(getIntent().getData());
            ZipInputStream zis = new ZipInputStream(is);
            Zipper.upzipApplicationData(zis, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //start main app
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);

    }
}
