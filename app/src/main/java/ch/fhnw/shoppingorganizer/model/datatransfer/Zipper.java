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
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingItemRepository;

public class Zipper {
    private static final int BUFFER = 2048;
    private static final ShoppingItemRepository itemRepository = RepositoryProvider.getShoppingItemRepositoryInstance();

    public static final String JSONExportFileName = "exportData.json";

    public static void zipApplicationData(final Context applicationContext){
        final List<String> filePaths = new ArrayList<>();
        final ContextWrapper cw = new ContextWrapper(applicationContext);
        final File dir = cw.getDir("transfer", Context.MODE_PRIVATE);
        dir.mkdir();

        try {
            //Get all current (used images)
            filePaths.addAll(
                    itemRepository.getAllItems()
                            .stream()
                            .map(entry -> new File(entry.getImgPath()))
                            .filter(File::exists)
                            .map(file -> file.getAbsolutePath())
                            .collect(Collectors.toList())
            );

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

        File zipFile = new File(dir, "ShoppingOrganizer.zip");

        performZip(filePaths, zipFile);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void upzipApplicationData(final ZipFile zipFile, final Context applicationContext){
        try {
            //Get json data
            final ZipEntry jsonFile = zipFile.getEntry(JSONExportFileName);
            final StringBuilder sb = new StringBuilder();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(jsonFile)));
            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }

            //perform data import (into database)
            DataImporter.unserializeFromJson(sb.toString());

            //copy all images to transfer imageDir directory
            final ContextWrapper cw = new ContextWrapper(applicationContext);
            final File dir = cw.getDir("transfer", Context.MODE_PRIVATE);

            zipFile.stream()
                    .<ZipEntry>filter(entry -> entry.getName() == JSONExportFileName)
                    .forEach(imageResource -> {
                        try {
                            String path = dir.getAbsolutePath() + "\\" + imageResource.getName();
                            Path storeLocation = Paths.get(path);
                            Files.copy(zipFile.getInputStream(imageResource), storeLocation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

        }catch (Exception e){

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
