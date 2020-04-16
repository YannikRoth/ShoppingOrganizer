package ch.fhnw.shoppingorganizer.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;

public abstract class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ShoppingListsItemHolder> implements Filterable, ListItemInteractionInterface {
    private Context context;
    private List<ShoppingList> shoppingLists;
    private List<ShoppingList> shoppingListsFull;

    public ShoppingListsAdapter(Context context, List<ShoppingList> shoppingLists, RecyclerView recyclerView) {
        this.context = context;
        this.shoppingLists = shoppingLists;
        Collections.sort(this.shoppingLists);
        shoppingListsFull = new ArrayList<ShoppingList>(shoppingLists);
        this.createShoppingListItemTouchHelper(recyclerView, getSwipeDirs());
    }

    public void addShoppingList(ShoppingList shoppingList) {
        if(!this.shoppingListsFull.contains(shoppingList)) {
            this.shoppingListsFull.add(shoppingList);
            this.shoppingLists.clear();
            Collections.sort(this.shoppingListsFull);
            this.shoppingLists.addAll(this.shoppingListsFull);
            notifyDataSetChanged();
        }
    }
    public void removeShoppingList(ShoppingList shoppingList) {
        if(shoppingListsFull.contains(shoppingList))
            shoppingListsFull.remove(shoppingList);
        if(shoppingLists.contains(shoppingList)) {
            int index = shoppingLists.indexOf(shoppingList);
            shoppingLists.remove(shoppingList);
            notifyItemRemoved(index);
        }
    }

    public Context getContext() {
        return this.context;
    }

    /**
     * RecyclerView internal method which will create view holder class for each item
     */
    @NonNull
    @Override
    public ShoppingListsItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.shopping_lists_item, parent, false);
        return new ShoppingListsItemHolder(view);
    }

    public void createShoppingListItemTouchHelper(RecyclerView recyclerView, int swipeDirs) {
        new ItemTouchHelper(this.getCountryItemTouchHelper(swipeDirs)).attachToRecyclerView(recyclerView);
    }

    private ItemTouchHelper.SimpleCallback getCountryItemTouchHelper(int swipeDirs){
        return new ItemTouchHelper.SimpleCallback(0, swipeDirs) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                switch(direction) {
                    case ItemTouchHelper.LEFT:
                        onSwipeLeft(viewHolder);
                        break;
                    case ItemTouchHelper.RIGHT:
                        onSwipeRight(viewHolder);
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                onChildDrawDetails(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
    }

    /**
     * RecyclerView internal method which will called for each item after change
     */
    @Override
    public void onBindViewHolder(@NonNull ShoppingListsAdapter.ShoppingListsItemHolder holder, int position) {
        ShoppingList item = shoppingLists.get(position);
        holder.shoppingListName.setText(item.getListName());

        holder.shoppingListPrice.setText(context.getString(R.string.shopping_lists_price) + ": " + Globals.NUMBERFORMAT.format(item.getTotalPrice()));
        holder.shoppingListQuantity.setText(context.getString(R.string.shopping_lists_quantity) + ": " + Globals.NUMBERFORMAT.format(item.getTotalQuantity()));
    }

    /**
     * RecyclerView internal method which will create item with size of the list
     */
    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingListsFilter;
    }
    private Filter shoppingListsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ShoppingList> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0)
                filteredList.addAll(shoppingListsFull);
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(ShoppingList l:shoppingListsFull) {
                    if(l.getListName().toLowerCase().contains(filterPattern))
                        filteredList.add(l);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            shoppingLists.clear();
            shoppingLists.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    class ShoppingListsItemHolder extends RecyclerView.ViewHolder {

        TextView shoppingListName;
        TextView shoppingListPrice;
        TextView shoppingListQuantity;

        ShoppingListsItemHolder(@NonNull View itemView) {
            super(itemView);
            shoppingListName = itemView.findViewById(R.id.tvShoppingListName);
            shoppingListPrice = itemView.findViewById(R.id.tvShoppingListTotalPrice);
            shoppingListQuantity = itemView.findViewById(R.id.tvShoppingListTotalQuantity);

            itemView.setOnClickListener(v -> onItemClick(v, getAdapterPosition()));
            itemView.setOnLongClickListener(v -> {
                onLongItemClick(v, getAdapterPosition());
                return true;
            });
        }
    }
}
