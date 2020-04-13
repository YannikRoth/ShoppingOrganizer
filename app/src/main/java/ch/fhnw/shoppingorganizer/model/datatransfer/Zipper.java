package ch.fhnw.shoppingorganizer.model.datatransfer;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingItemRepository;

public class Zipper {
    private static final int BUFFER = 2048;
    private static final ShoppingItemRepository itemRepository = RepositoryProvider.getShoppingItemRepositoryInstance();

    public static void zipApplicationData(Context applicationContext){
        List<String> filePaths = new ArrayList<>();

        try {
            //Get all current (used imaged)
            filePaths.addAll(
                    itemRepository.getAllItems()
                            .stream()
                            .map(entry -> new File(entry.getImgPath()))
                            .map(file -> file.getAbsolutePath())
                            .collect(Collectors.toList())
            );

            //add json file
            final ContextWrapper cw = new ContextWrapper(applicationContext);
            File dir = cw.getDir("transfer", Context.MODE_PRIVATE);
            File file = new File(dir, "exportData.json");
            dir.mkdir();

            OutputStream outputStream = new FileOutputStream(file);
            Writer writer = new OutputStreamWriter(outputStream);
            writer.write(DataExporter.serializeToJsonFromDatabase().toString());
            outputStream.flush();
            outputStream.close();

            //add filePath to zip list
           filePaths.add(file.getAbsolutePath());
        }catch (Exception e){

        }

        performZip(filePaths, "");

    }

    private static void performZip(List<String> files, String zipFile) {
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