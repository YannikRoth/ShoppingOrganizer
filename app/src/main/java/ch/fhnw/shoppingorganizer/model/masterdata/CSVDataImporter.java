package ch.fhnw.shoppingorganizer.model.masterdata;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.Buffer;

import ch.fhnw.shoppingorganizer.model.businessobject.Category;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItemBuilder;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;

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
                        .withItemActive(lineValues[itemActiveIndex]=="1" ? true : false)
                        .withItemName(lineValues[itemNameIndex])
                        .withPrice(new BigDecimal(lineValues[itemPriceIndex]))
                        .build();
                RepositoryProvider.getShoppingItemRepositoryInstance().saveEntity(shoppingItem);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
