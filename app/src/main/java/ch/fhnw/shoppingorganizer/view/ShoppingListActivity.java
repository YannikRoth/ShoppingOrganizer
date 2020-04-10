package ch.fhnw.shoppingorganizer.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;

import static ch.fhnw.shoppingorganizer.view.MainActivity.LIST_ID;
import static ch.fhnw.shoppingorganizer.view.MainActivity.LIST_NAME;

public class ShoppingListActivity extends AppCompatActivity implements ShoppingListItemListener {

    public static final String SHOPPING_LIST_NAME = "Shopping list name";
    public static final String ITEM_NAME_EXTRA = "Item name";
    public static final int EDIT_REQUEST_CODE = 123;
    private final String TAG = this.getClass().getSimpleName();

    private ShoppingListAdapter adapter;

    // GUI controls
    private Toolbar tbSearch;
    private RecyclerView rvShoppingLists;
    private TextView tvNoResults;
    private FloatingActionButton btnAdd;

    private List<ShoppingItem> shoppingItem;
    private ShoppingList shoppingList;
    private List<ShoppingListItem> shoppingListItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        this.shoppingItem = RepositoryProvider.getShoppingItemRepositoryInstance().getAllItems();
        Intent intent = getIntent();
        Log.e(TAG, "onCreate NAME: " + intent.getStringExtra(LIST_NAME));
        Log.e(TAG, "onCreate ID: " + intent.getLongExtra(LIST_ID, 0));
        if(intent.hasExtra(LIST_ID) && intent.getLongExtra(LIST_ID, 0) > 0) {
            this.shoppingList = RepositoryProvider
                    .getShoppingListRepositoryInstance()
                    .getShoppingListById(intent.getLongExtra(LIST_ID, 0));
            this.shoppingListItems = RepositoryProvider.getShoppingListRepositoryInstance().getShoppingListItems(shoppingList);
        }

        initUi();
    }

    private void initUi() {
        // Getting references to the Toolbar, ListView and TextView
        tbSearch = findViewById(R.id.toolbarShoppingList);
        if (shoppingList.getListName() != null) {
            tbSearch.setTitle(getString(R.string.shopping_lists_title) + " " + shoppingList.getListName());
        }
        // Set Toolbar as ActionBar
        if (tbSearch != null) {
            setSupportActionBar(tbSearch);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditItemActivity.class);
            intent.putExtra(SHOPPING_LIST_NAME, shoppingList.getListName());
            startActivityForResult(intent, EDIT_REQUEST_CODE);
        });
        rvShoppingLists = findViewById(R.id.rvListItems);
        tvNoResults = findViewById(R.id.tvNoResults);
        adapter = new ShoppingListAdapter(shoppingList, shoppingItem, shoppingListItems, this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvShoppingLists.addItemDecoration(dividerItemDecoration);

        // Setup the touch helper which handle the swipe events
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvShoppingLists);

        rvShoppingLists.setLayoutManager(new LinearLayoutManager(this));
        rvShoppingLists.setAdapter(adapter);
        setEmptyView(shoppingItem);
    }

    private void setEmptyView(List<ShoppingItem> shoppingLists) {
        // TextView Message about "No results"
        if (shoppingLists.size() == 0) {
            rvShoppingLists.setVisibility(View.INVISIBLE);
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            rvShoppingLists.setVisibility(View.VISIBLE);
            tvNoResults.setVisibility(View.INVISIBLE);
        }
    }

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
                // Called when the query text is changed by the user
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Activity lifecycle method which handle the result from the edit screen.
     * The result is the list name which should be set on the toolbar
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String shoppingListName = data.getStringExtra(SHOPPING_LIST_NAME);
                    if (shoppingListName != null) {
                        tbSearch.setTitle(getString(R.string.shopping_lists_title) + " " + shoppingListName);
                    }
                }
            }
        }
    }

    /**
     * Callback from the adapter after the user swipe the item
     */
    @Override
    public void onSwipeLeft() {

    }

    /**
     * Callback from the adapter after the user swipe the item
     */
    @Override
    public void onSwipeRight() {

    }

    /**
     * Callback from the adapter after the user hold the item.
     * Start the new screen for editing.
     */
    @Override
    public void onHoldItem(String itemName) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra(SHOPPING_LIST_NAME, shoppingList.getListName());
        intent.putExtra(ITEM_NAME_EXTRA, itemName);
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }
}
