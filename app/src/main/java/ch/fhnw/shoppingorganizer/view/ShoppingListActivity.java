package ch.fhnw.shoppingorganizer.view;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static ch.fhnw.shoppingorganizer.view.MainActivity.LIST_ID;

public class ShoppingListActivity extends AppCompatActivity {

    public static final String SHOPPING_LIST_NAME = "Shopping list name";
    public static final String ITEM_NAME_EXTRA = "Item name";
    public static final String SHOPPING_ITEM_ID = "pkShoppingItem";
    public static final int EDIT_REQUEST_CODE = 123;
    private final String TAG = this.getClass().getSimpleName();

    private ShoppingListAdapter adapter;

    // GUI controls
    private Toolbar tbSearch;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvShoppingLists;
    private TextView tvNoResults;
    private FloatingActionButton btnAdd;

    private List<ShoppingItem> shoppingItem;
    private ShoppingList shoppingList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        this.shoppingItem = RepositoryProvider.getShoppingItemRepositoryInstance().getAllItems();
        Intent intent = getIntent();
        if(intent.hasExtra(LIST_ID) && intent.getLongExtra(LIST_ID, 0) > 0) {
            this.shoppingList = RepositoryProvider
                    .getShoppingListRepositoryInstance()
                    .getShoppingListById(intent.getLongExtra(LIST_ID, 0));
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

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.onRefreshViewOnPull();
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

        rvShoppingLists = findViewById(R.id.rvListItems);
        tvNoResults = findViewById(R.id.tvNoResults);
        adapter = new ShoppingListAdapter(this, shoppingList, shoppingItem, rvShoppingLists) {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                ShoppingItem item = shoppingItem.get(position);
                Intent intent = new Intent(this.getContext(), EditItemActivity.class);
                intent.putExtra(SHOPPING_LIST_NAME, shoppingList.getListName());
                intent.putExtra(ITEM_NAME_EXTRA, item.getItemName());
                intent.putExtra(SHOPPING_ITEM_ID, item.getId());
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }

            @Override
            public int getSwipeDirs() {
                return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }

            private ShoppingListItem deletedItem = null;
            @Override
            public void onSwipeLeft(@NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                ShoppingItem item = shoppingItem.get(position);
                if(shoppingList.getShoppingListItems() == null || shoppingList.getShoppingListItem(item) == null) {
                    adapter.notifyItemChanged(position);
                    Toast.makeText(this.getContext(), "Item has no quantity, action not possible.", Toast.LENGTH_SHORT).show();
                    return;
                }
                deletedItem = shoppingList.getShoppingListItem(item);
                RepositoryProvider.getShoppingItemRepositoryInstance().deleteEntity(deletedItem);
                shoppingList.getShoppingListItems().remove(deletedItem);
                adapter.notifyItemChanged(position);
                Snackbar.make(rvShoppingLists, "Item removed: " + deletedItem.getShoppingItem().getItemName(), Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            RepositoryProvider.getShoppingItemRepositoryInstance().saveEntity(deletedItem);
                            shoppingList.getShoppingListItems().add(position, deletedItem);
                            adapter.notifyItemChanged(position);
                        })
                        .show();
            }

            private ShoppingListItem checkedItem = null;
            @Override
            public void onSwipeRight(@NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                ShoppingItem item = shoppingItem.get(position);
                if(shoppingList.getShoppingListItems() == null || shoppingList.getShoppingListItem(item) == null) {
                    adapter.notifyItemChanged(position);
                    Toast.makeText(this.getContext(), "Item has no quantity, action not possible.", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkedItem = shoppingList.getShoppingListItem(item);
                checkedItem.setItemState(Globals.SHOPPING_LIST_ITEM_STATE_CHECKED);
                RepositoryProvider.getShoppingListItemRepositoryInstance().saveEntity(checkedItem);
                adapter.notifyItemChanged(position);
                Snackbar.make(rvShoppingLists, "Item checked: " + checkedItem.getShoppingItem().getItemName(), Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            checkedItem.setItemState(Globals.SHOPPING_LIST_ITEM_STATE_UNCHECKED);
                            RepositoryProvider.getShoppingListItemRepositoryInstance().saveEntity(checkedItem);
                            adapter.notifyItemChanged(position);
                        })
                        .show();
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvShoppingLists.addItemDecoration(dividerItemDecoration);

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
                adapter.getFilter().filter(newText);
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
                        tbSearch.setTitle(shoppingListName);
                    }
                }
            }
        }
    }


}
