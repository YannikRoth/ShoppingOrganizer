package ch.fhnw.shoppingorganizer.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.controller.ShoppingListsAdapter;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListBuilder;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItemBuilder;
import ch.fhnw.shoppingorganizer.model.database.DbUtils;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListItemRepository;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListRepository;
import ch.fhnw.shoppingorganizer.model.datatransfer.Zipper;
import ch.fhnw.shoppingorganizer.view.Tutorial.TutorialSliderActivity;
import ch.fhnw.shoppingorganizer.view.Tutorial.TutorialType;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static ch.fhnw.shoppingorganizer.view.Tutorial.TutorialType.TUTORIAL_SHOPPING_LIST;

public class MainActivity extends AppCompatActivity {

    public static final String LIST_NAME = "ListName";
    public static final String LIST_ID = "pkShoppingList";
    public static final int CALL_SHOPPING_LIST_INTENT = 4;
    public final String TAG = this.getClass().getSimpleName();

    private ShoppingListRepository shoppingListRepositoryInstance = RepositoryProvider.getShoppingListRepositoryInstance();
    private ShoppingListItemRepository shoppingListItemRepositoryInstance = RepositoryProvider.getShoppingListItemRepositoryInstance();

    //Data elemens
    List<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();

    private ShoppingListsAdapter adapter;

    // GUI controls
    private Toolbar tbSearch;
    private RecyclerView rvShoppingLists;
    private TextView tvNoResults;
    private FloatingActionButton btnAdd;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //import masterdata to database if empty
        if (DbUtils.isEmpty(ShoppingItem.class)) {
            importMasterData();
        }

        //display basic values from database (as an example for the GUI guys)
        shoppingLists = shoppingListRepositoryInstance.getAllItems();

        initUi();

        callTutorial(false);


        Intent intent = getIntent();
        if(intent.hasExtra(Globals.INTENT_NEW_LIST_IDS_EXTRA) && intent.getStringExtra(Globals.INTENT_NEW_LIST_IDS_EXTRA) != Globals.EMPTY_STRING) {
            final List<ShoppingList> list = Stream.of(intent.getStringExtra(Globals.INTENT_NEW_LIST_IDS_EXTRA).split(Globals.STRING_SEPERATOR))
                    .map(e -> shoppingListRepositoryInstance.getShoppingListById(Long.parseLong(e)))
                    .collect(Collectors.toList());
            highLightNewImport(list);
        }
    }

    private void callTutorial(boolean forceCall) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Globals.PREF_TUTORIAL, MODE_PRIVATE);
        if (forceCall || !prefs.getBoolean(TUTORIAL_SHOPPING_LIST.toString(), false)) {
            Intent intentTutorial = new Intent(this, TutorialSliderActivity.class);
            intentTutorial.putExtra(Globals.INTENT_TUTORIAL_TYPE, TutorialType.TUTORIAL_SHOPPING_LIST.toString());
            startActivity(intentTutorial);
            TutorialSliderActivity.savePreferences(prefs, TUTORIAL_SHOPPING_LIST);
        }
    }

    private void initUi() {
        // Getting references to the Toolbar, ListView and TextView
        tbSearch = findViewById(R.id.toolbarEditItem);
        // Set Toolbar as ActionBar
        if (tbSearch != null)
            setSupportActionBar(tbSearch);
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            showShoppingListDialog(null);
        });

        rvShoppingLists = findViewById(R.id.rvShoppingLists);
        tvNoResults = findViewById(R.id.tvNoResults);

        adapter = new ShoppingListsAdapter(this, shoppingLists, rvShoppingLists) {
            @Override
            public void onItemClick(View view, int position) {
                ShoppingList item = shoppingLists.get(position);
                Log.d(TAG, "On click List: " + item.getListName());
                Intent intent = new Intent(this.getContext(), ShoppingListActivity.class);
                intent.putExtra(LIST_NAME, item.getListName());
                intent.putExtra(LIST_ID, item.getId());
                startActivityForResult(intent, CALL_SHOPPING_LIST_INTENT);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                showShoppingListDialog(shoppingLists.get(position));
            }

            @Override
            public int getSwipeDirs() {
                return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }

            ShoppingList deletedShoppingList = null;
            List<ShoppingListItem> deletedShoppingListItems = null;

            @Override
            public void onSwipeLeft(@NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                deletedShoppingList = shoppingLists.get(position);
                deletedShoppingListItems = new ArrayList<>(deletedShoppingList.getShoppingListItems());
                for (ShoppingListItem item : deletedShoppingListItems) {
                    shoppingListItemRepositoryInstance.deleteEntity(item);
                    deletedShoppingList.getShoppingListItems().remove(item);
                }
                shoppingListRepositoryInstance.deleteEntity(deletedShoppingList);
                adapter.removeShoppingList(deletedShoppingList);
                Snackbar.make(rvShoppingLists, getString(R.string.snackbar_list_removed) + ": " + deletedShoppingList.getListName(), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.snackbar_undo), v -> {
                            deletedShoppingList = new ShoppingListBuilder()
                                    .withListName(deletedShoppingList.getListName())
                                    .build();
                            shoppingListRepositoryInstance.saveEntity(deletedShoppingList);
                            for (ShoppingListItem item : deletedShoppingListItems) {
                                item = new ShoppingListItemBuilder()
                                        .withShoppingItem(item.getShoppingItem())
                                        .withItemState(item.isItemState())
                                        .withQuantity(item.getQuantity())
                                        .withShoppingList(deletedShoppingList)
                                        .build();
                                shoppingListItemRepositoryInstance.saveEntity(item);
                                deletedShoppingList.getShoppingListItems().add(item);
                            }
                            adapter.addShoppingList(deletedShoppingList);
                        })
                        .show();
            }

            @Override
            public void onSwipeRight(@NonNull RecyclerView.ViewHolder viewHolder) {
                notifyItemChanged(viewHolder.getAdapterPosition());
                ShoppingList toExportShoppingList = shoppingLists.get(viewHolder.getAdapterPosition());

                //create export file
                Zipper.zipExportShoppingList(getApplicationContext(), toExportShoppingList);

                //send export file
                Context context = getApplicationContext();
                File dir = context.getDir("export", Context.MODE_PRIVATE);
                File exportFile = new File(dir, Zipper.ExportedShoppingListFileName);

                //create send intent and attach export file
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Exported Shopping List");
                intent.putExtra(Intent.EXTRA_TEXT, "Please find my exported shopping list attached");
                Uri uri = FileProvider.getUriForFile(context, "ch.fhnw.shoppingorganizer.fileprovider", exportFile);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Export..."));
            }

            @Override
            public void onChildDrawDetails(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorDelete))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete_sweep)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorSend))
                        .addSwipeRightActionIcon(R.drawable.ic_message)
                        .create()
                        .decorate();
            }
        };
        rvShoppingLists.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvShoppingLists.addItemDecoration(dividerItemDecoration);
        rvShoppingLists.setAdapter(adapter);
        setEmptyView(shoppingLists);

    }

    private AlertDialog alertDialog = null;

    private void showShoppingListDialog(ShoppingList shoppingListBase) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.shopping_list_alert_dialog, null);
        alert.setView(dialogView);

        final EditText edittext = dialogView.findViewById(R.id.edCreateNewList);
        if (shoppingListBase != null)
            edittext.setText(shoppingListBase.getListName());
        Button saveButton = dialogView.findViewById(R.id.buttonSaveNewList);

        saveButton.setOnClickListener((view) -> {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            String inputText = edittext.getText().toString().trim();

            ShoppingList shoppingList;
            if (shoppingListBase != null) {
                shoppingList = shoppingListBase;
                shoppingList.setListName(inputText);
            } else {
                shoppingList = new ShoppingListBuilder()
                        .withListName(inputText)
                        .build();
            }

            shoppingListRepositoryInstance.saveEntity(shoppingList);
            if (!shoppingLists.contains(shoppingList)) {
                adapter.addShoppingList(shoppingList);
                setEmptyView(shoppingLists);
            } else {
                adapter.notifyItemChanged(shoppingLists.indexOf(shoppingList));
            }
        });

        alertDialog = alert.show();
    }

    private void setEmptyView(List<ShoppingList> shoppingLists) {
        // TextView Message about "No results"
        if (shoppingLists.size() == 0) {
            rvShoppingLists.setVisibility(View.INVISIBLE);
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            rvShoppingLists.setVisibility(View.VISIBLE);
            tvNoResults.setVisibility(View.INVISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void importMasterData() {
        final AssetManager am = getAssets();
        try {
            //  CSVDataImporter csvDataImporter = new CSVDataImporter(am.open("masterdata.csv"));
            //  csvDataImporter.performImport();
            Zipper.upzipApplicationData(new ZipInputStream(am.open("ShoppingOrganizer.sho")), getApplicationContext());
        } catch (Exception e) {

        }
    }

    /**
     * Internal method catch the menu item's clicks. For this case is search button
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_lists_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint(getString(R.string.toolbar_search_menu));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // This method can be used when a query is submitted e.g.
                // creating search history using SQLite DB
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        MenuItem mMoreDropdown = menu.findItem(R.id.showTutorial);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showTutorial:
                callTutorial(true);
                break;
            case R.id.exportAll:
                try {
                    Context context = getApplicationContext();
                    Zipper.zipApplicationData(context);

                    //get the backup file
                    File dir = context.getDir("transfer", Context.MODE_PRIVATE);
                    File exportFile = new File(dir, Zipper.ExportedShoppingrganizerFileName);

                    //create send intent and attach export file
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Exported backup of ShoppingOrganizer");
                    intent.putExtra(Intent.EXTRA_TEXT, "Here is my backup file");
                    Uri uri = FileProvider.getUriForFile(context, "ch.fhnw.shoppingorganizer.fileprovider", exportFile);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(intent, "Export..."));
                } catch (Exception e) {
                    Log.d("Export exception: ", e.getMessage());
                }
                break;
            case R.id.importAll:
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, Globals.IMPORT_ACTIVITY_REQ_IDENTIFIER);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Globals.IMPORT_ACTIVITY_REQ_IDENTIFIER:
                if (resultCode == -1) {
                    Log.d("switch entered", "switch entered");
                    final Uri fileUri = data.getData();
                    try {
                        final InputStream is = getApplicationContext().getContentResolver().openInputStream(fileUri);
                        Zipper.upzipApplicationData(new ZipInputStream(is), getApplicationContext());

                        //highlight new import
                        this.highLightNewImport();

                    } catch (Exception e) {
                        Log.d("exception", e.getMessage());
                    }

                }
                break;
            case CALL_SHOPPING_LIST_INTENT:
                if(resultCode == Activity.RESULT_OK) {
                    Long shoppingListId = data.getLongExtra(LIST_ID, 0);
                    ShoppingList list = shoppingListRepositoryInstance.getShoppingListById(shoppingListId);
                    int position = 0;
                    if(shoppingLists.contains(list))
                        position = shoppingLists.indexOf(list);
                    adapter.notifyItemChanged(position);
                }
                break;
        }

    }

    private void highLightNewImport(){
        final List<ShoppingList> allLists = shoppingListRepositoryInstance.getAllItems();
        List<ShoppingList> newLists = new ArrayList<>();
        for(ShoppingList sl : allLists){
            if(!adapter.getShoppingListFull().contains(sl)){
                adapter.getShoppingListFull().add(sl);
                shoppingLists.add(sl);
                newLists.add(sl);
            }
        }
        Collections.sort(shoppingLists);
        Collections.sort(adapter.getShoppingListFull());
        adapter.notifyDataSetChanged();

        highLightNewImport(newLists);
    }
    private void highLightNewImport(List<ShoppingList> newShoppingLists){
        final List<Integer> indices = newShoppingLists.stream()
                .map(e -> shoppingLists.indexOf(e))
                .collect(Collectors.toList());
        if(!indices.isEmpty()){
            rvShoppingLists.scrollToPosition(indices.get(indices.size()-1));
            adapter.setHighlightPositions(indices);
        }
    }
}
