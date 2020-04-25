package ch.fhnw.shoppingorganizer.model.datatransfer;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingItemRepository;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListItemRepository;

public class Zipper {
    private static final int BUFFER = 2048;
    private static final ShoppingItemRepository itemRepository = RepositoryProvider.getShoppingItemRepositoryInstance();
    private static final ShoppingListItemRepository listItemRepository = RepositoryProvider.getShoppingListItemRepositoryInstance();

    public static final String JSONExportFileName = "exportData.json";
    public static final String ExportedShoppingListFileName = "ExportedShoppingList.sho";
    public static final String ExportedShoppingrganizerFileName = "ShoppingOrganizer.sho";
    private static final Set<String> IMAGE_FILETYPES = new TreeSet<>();

    static {
        IMAGE_FILETYPES.add("PNG");
        IMAGE_FILETYPES.add("JPG");
        IMAGE_FILETYPES.add("JPEG");
    }

    public static void zipApplicationData(final Context applicationContext){
        final List<String> filePaths = new ArrayList<>();
        final ContextWrapper cw = new ContextWrapper(applicationContext);
        final File dir = cw.getDir("transfer", Context.MODE_PRIVATE);
        dir.mkdir();

        try {
            //Get all current (used images)
            filePaths.addAll(getAllFilePaths(itemRepository.getAllItems()));

            //add json file
            File file = new File(dir, JSONExportFileName);

            Writer writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.write(DataExporter.serializeToJsonFromDatabase().toString());
            writer.flush();
            writer.close();

            //add filePath to zip list
           filePaths.add(file.getAbsolutePath());
        }catch (Exception e){

        }

        File zipFile = new File(dir, ExportedShoppingrganizerFileName);

        performZip(filePaths, zipFile);
    }

    public static void zipExportShoppingList(final Context applicationContext, final ShoppingList shoppingList){
        final List<String> filePaths = new ArrayList<>();
        final ContextWrapper cw = new ContextWrapper(applicationContext);
        final File dir = cw.getDir("export", Context.MODE_PRIVATE);
        dir.mkdir();

        try {
            //add used images
            filePaths.addAll(getAllFilePaths(listItemRepository.getShoppingItems(shoppingList)));

            //add json file
            File file = new File(dir, JSONExportFileName);

            Writer writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.write(DataExporter.serializeShoppingListFromDatabase(shoppingList).toString());
            writer.flush();
            writer.close();

            //add filePath to zip list
            filePaths.add(file.getAbsolutePath());
        }catch (Exception e){

        }

        File exportFile = new File(dir, ExportedShoppingListFileName);
        performZip(filePaths, exportFile);
    }

    private static List<String> getAllFilePaths(List<ShoppingItem> shoppingItems){
        return shoppingItems
                .stream()
                .map(entry -> new File(entry.getImgPath()))
                .filter(File::exists)
                .map(file -> file.getAbsolutePath())
                .collect(Collectors.toList());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void upzipApplicationData(final ZipInputStream zipInputStream, final Context applicationContext) throws IOException {
        final byte buffer[] = new byte[BUFFER];
        final File dir = applicationContext.getDir("imageDir", Context.MODE_PRIVATE);

        ZipEntry jsonImportFile;
        ZipEntry entry;
        while((entry = zipInputStream.getNextEntry()) != null){
            //split at dots and take last entry for file type
            String[] fileParts = entry.getName().split("\\.");
            if(IMAGE_FILETYPES.contains(fileParts[fileParts.length-1].toUpperCase())){
                File imageFile = new File(dir, entry.getName());

                try(FileOutputStream fos = new FileOutputStream(imageFile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)){
                    int len;
                    while((len = zipInputStream.read(buffer)) > 0){
                        bos.write(buffer, 0, len);
                    }
                }
            }else{
                String jsonString = new BufferedReader(new InputStreamReader(zipInputStream))
                        .lines().collect(Collectors.joining(""));
                try {
                    DataImporter.unserializeFromJson(jsonString);
                }catch (Exception e){

                }
            }
        }


    }

    private static void performZip(final List<String> files, final File zipFile) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for (int i = 0; i < files.size(); i++) {
                FileInputStream fi = new FileInputStream(files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(files.get(i).substring(files.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.finish();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
