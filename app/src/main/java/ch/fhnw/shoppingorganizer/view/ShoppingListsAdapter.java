package ch.fhnw.shoppingorganizer.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ShoppingListsItem> {

    private List<ShoppingList> shoppingLists;
    private ShoppingListsItemListener listsItemListener;

    ShoppingListsAdapter(List<ShoppingList> shoppingLists, ShoppingListsItemListener listener) {
        this.shoppingLists = shoppingLists;
        this.listsItemListener = listener;
    }

    /**
     * RecyclerView internal method which will create view holder class for each item
     */
    @NonNull
    @Override
    public ShoppingListsItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListsItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_lists_item, parent, false));
    }

    /**
     * RecyclerView internal method which will called for each item after change
     */
    @Override
    public void onBindViewHolder(@NonNull ShoppingListsItem holder, int position) {
        ShoppingList item = shoppingLists.get(position);
        holder.shoppingListName.setText(item.getListName());
        holder.shoppingListPrice.setText(String.format("price %.2f", item.getTotalPrice()));
        holder.shoppingListQuantity.setText(String.format("quantity %d", item.getTotalQuantity()));
    }

    /**
     * RecyclerView internal method which will create item with size of the list
     */
    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    class ShoppingListsItem extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        TextView shoppingListName;
        TextView shoppingListPrice;
        TextView shoppingListQuantity;

        ShoppingListsItem(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
            shoppingListName = itemView.findViewById(R.id.tvShoppingListName);
            shoppingListPrice = itemView.findViewById(R.id.tvShoppingListTotalPrice);
            shoppingListQuantity = itemView.findViewById(R.id.tvShoppingListTotalQuantity);
        }

        /**
         * Callback from the {@link View.OnLongClickListener}. Method should be update the view through the interface.
         */
        @Override
        public boolean onLongClick(View v) {
            listsItemListener.onHoldItem(getAdapterPosition());
            return false;
        }

        /**
         * Callback from the {@link  View.OnClickListener }. Method should be update the view through the interface.
         */
        @Override
        public void onClick(View v) {
            listsItemListener.onClickItem(getAdapterPosition());
        }
    }
}
