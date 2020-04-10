package ch.fhnw.shoppingorganizer.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItemBuilder;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListBuilder;
import ch.fhnw.shoppingorganizer.model.database.DbUtils;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListItemRepository;
import ch.fhnw.shoppingorganizer.model.masterdata.CSVDataImporter;

public class MainActivity extends AppCompatActivity implements ShoppingListsItemListener {

    public static final String LIST_NAME = "ListName";

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


        //display basic values from database (as an example for the GUI guys)
        // IMPORTATN FOR GUI PEOPLE: always interact with database using the RepositoryProvider for each business object
        shoppingLists = RepositoryProvider.getShoppingListRepositoryInstance().getAllItems();


        //import masterdata to database if empty
        if(DbUtils.isEmpty(ShoppingItem.class)) {
            importMasterData();
        }
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
            showAddDialog();
        });
        rvShoppingLists = findViewById(R.id.rvShoppingLists);
        tvNoResults = findViewById(R.id.tvNoResults);


        String[] testArray = getResources().getStringArray(R.array.test_shopping_lists);

        adapter = new ShoppingListsAdapter(shoppingLists, this);
        rvShoppingLists.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvShoppingLists.addItemDecoration(dividerItemDecoration);
        rvShoppingLists.setAdapter(adapter);
        setEmptyView(testArray);
    }

    private void showAddDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        alert.setMessage(R.string.shopping_lists_popup_message);
        alert.setTitle(R.string.shopping_lists_popup_title);

        alert.setView(edittext);

        alert.setPositiveButton(R.string.shopping_lists_popup_yes_btn, (dialog, whichButton) -> {
            String inputText = edittext.getText().toString();

            ShoppingList shoppingList = new ShoppingListBuilder()
                    .withListName(inputText)
                    .build();
            RepositoryProvider.getShoppingListRepositoryInstance().saveEntity(shoppingList);
            shoppingLists.add(shoppingList);
            adapter.notifyDataSetChanged();
        });
        alert.setNegativeButton(R.string.shopping_lists_popup_no_btn, ((dialog, which) -> {
            dialog.dismiss();
        }));

        alert.show();


    }

    private void setEmptyView(String[] shoppingLists) {
        // TextView Message about "No results"
        if (shoppingLists.length == 0) {
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

    /**
     * Callback from the adapter's item
     * @param position of item in the adapter
     */
    @Override
    public void onHoldItem(int position) {

        Log.d("MainActivity", "On hold item ..." + position);
    }

    /**
     * Callback from the adapter's item
     * @param position of item in the adapter
     */
    @Override
    public void onClickItem(int position) {
        ShoppingList item = shoppingLists.get(position);
        Log.d("MainActivity", "On click item ...");
        Intent intent = new Intent(this, ShoppingListActivity.class);
        intent.putExtra(LIST_NAME, item.getListName());
        startActivity(intent);
    }
}
