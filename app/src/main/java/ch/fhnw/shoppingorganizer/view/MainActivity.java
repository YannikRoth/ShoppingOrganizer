package ch.fhnw.shoppingorganizer.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItemBuilder;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListBuilder;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItemBuilder;
import ch.fhnw.shoppingorganizer.model.database.DbUtils;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListItemRepository;
import ch.fhnw.shoppingorganizer.model.masterdata.CSVDataImporter;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    public static final String LIST_NAME = "ListName";
    public static final String LIST_ID = "pkShoppingList";
    public final String TAG = this.getClass().getSimpleName();

    //Data elemens
    List<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();

    private ShoppingListsAdapter adapter;

    // GUI controls
    private Toolbar tbSearch;
    private RecyclerView rvShoppingLists;
    private TextView tvNoResults;
    private FloatingActionButton btnAdd;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //import masterdata to database if empty
        if(DbUtils.isEmpty(ShoppingItem.class)) {
            importMasterData();
        }

        //display basic values from database (as an example for the GUI guys)
        // IMPORTATN FOR GUI PEOPLE: always interact with database using the RepositoryProvider for each business object
        shoppingLists = RepositoryProvider.getShoppingListRepositoryInstance().getAllItems();

        initUi();
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
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                showShoppingListDialog(shoppingLists.get(position));
            }

            @Override
            public int getSwipeDirs() {
                return ItemTouchHelper.LEFT;
            }

            ShoppingList deletedShoppingList = null;
            List<ShoppingListItem> deletedShoppingListItems = null;
            @Override
            public void onSwipeLeft(@NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                deletedShoppingList = shoppingLists.get(position);
                deletedShoppingListItems = new ArrayList<>(deletedShoppingList.getShoppingListItems());
                for(ShoppingListItem item:deletedShoppingListItems) {
                    RepositoryProvider.getShoppingListItemRepositoryInstance().deleteEntity(item);
                    deletedShoppingList.getShoppingListItems().remove(item);
                }
                RepositoryProvider.getShoppingListRepositoryInstance().deleteEntity(deletedShoppingList);
                shoppingLists.remove(deletedShoppingList);
                adapter.notifyItemRemoved(position);
                Snackbar.make(rvShoppingLists, "List removed: " + deletedShoppingList.getListName(), Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            deletedShoppingList = new ShoppingListBuilder()
                                    .withListName(deletedShoppingList.getListName())
                                    .build();
                            RepositoryProvider.getShoppingListRepositoryInstance().saveEntity(deletedShoppingList);
                            for(ShoppingListItem item:deletedShoppingListItems) {
                                item = new ShoppingListItemBuilder()
                                        .withShoppingItem(item.getShoppingItem())
                                        .withItemState(item.isItemState())
                                        .withQuantity(item.getQuantity())
                                        .withShoppingList(deletedShoppingList)
                                        .build();
                                RepositoryProvider.getShoppingListItemRepositoryInstance().saveEntity(item);
                                deletedShoppingList.getShoppingListItems().add(item);
                            }
                            shoppingLists.add(position, deletedShoppingList);
                            adapter.notifyItemInserted(position);
                        })
                        .show();
            }

            @Override
            public void onSwipeRight(@NonNull RecyclerView.ViewHolder viewHolder) {

            }

            @Override
            public void onChildDrawDetails(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorDelete))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete_sweep)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorCheck))
                        .addSwipeRightActionIcon(R.drawable.ic_check)
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

    private void showShoppingListDialog(ShoppingList shoppingListBase) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        alert.setMessage(R.string.shopping_lists_popup_message);
        alert.setTitle(R.string.shopping_lists_popup_title);

        if(shoppingListBase != null)
            edittext.setText(shoppingListBase.getListName());
        alert.setView(edittext);

        alert.setPositiveButton(R.string.shopping_lists_popup_yes_btn, (dialog, whichButton) -> {
            String inputText = edittext.getText().toString();

            ShoppingList shoppingList;
            if(shoppingListBase != null) {
                shoppingList = shoppingListBase;
                shoppingList.setListName(inputText);
            } else {
                shoppingList = new ShoppingListBuilder()
                        .withListName(inputText)
                        .build();
            }

            RepositoryProvider.getShoppingListRepositoryInstance().saveEntity(shoppingList);
            if(!shoppingLists.contains(shoppingList)) {
                shoppingLists.add(shoppingList);
                adapter.notifyDataSetChanged();
                setEmptyView(shoppingLists);
            } else {
                adapter.notifyItemChanged(shoppingLists.indexOf(shoppingList));
            }
        });
        alert.setNegativeButton(R.string.shopping_lists_popup_no_btn, ((dialog, which) -> {
            dialog.dismiss();
        }));

        alert.show();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void importMasterData(){
        final AssetManager am = getAssets();
        try {
            CSVDataImporter csvDataImporter = new CSVDataImporter(am.open("masterdata.csv"));
            csvDataImporter.performImport();
        }catch (Exception e){

        }
    }

    /**
     * Internal method catch the menu item's clicks. For this case is search button
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
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
        return super.onCreateOptionsMenu(menu);
    }
}
