package ch.fhnw.shoppingorganizer.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItemBuilder;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListItemHolder> implements ItemTouchHelperAdapter {
    private ShoppingList shoppingList;
    private List<ShoppingItem> shoppingItem;
    private ShoppingListItemListener listItemListener;

    ShoppingListAdapter(ShoppingList shoppingList, List<ShoppingItem> shoppingItem, List<ShoppingListItem> shoppingListItem, ShoppingListItemListener listener) {
        this.shoppingList = shoppingList;
        this.shoppingItem = shoppingItem;
        this.listItemListener = listener;
    }

    /**
     * RecyclerView internal method which will create view holder class for each item
     */
    @NonNull
    @Override
    public ShoppingListAdapter.ShoppingListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListAdapter.ShoppingListItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false));
    }

    /**
     * RecyclerView internal method which will called for each item after change
     */
    @Override
    public void onBindViewHolder(@NonNull ShoppingListAdapter.ShoppingListItemHolder holder, int position) {
        ShoppingItem item = shoppingItem.get(position);
        ShoppingListItem listItem = null;
        for (ShoppingListItem it:shoppingList.getShoppingListItems()) {
            if(it.getShoppingItem().equals(item)) {
                listItem = it;
                break;
            }
        }
        holder.itemName.setText("Name: " + item.getItemName());
        holder.itemImage.setImageResource(R.mipmap.ic_launcher);

        if(listItem != null) {
            holder.itemPrice.setText(String.format("CHF %.2f", listItem.getTotalItemPrice()));
            holder.inputQuantity.setText(String.format("%o", listItem.getQuantity()) );
        } else {
            holder.itemPrice.setText(String.format("CHF %.2f", 0.00));
            holder.inputQuantity.setText(String.format("%o", 0) );

        }

        Log.d("ShoppingListAdapter", "onBindViewHolder: end");
    }

    /**
     * RecyclerView internal method which will create item with size of the list
     */
    @Override
    public int getItemCount() {
        return shoppingItem.size();
    }

    /**
     * Callback from the {@link SimpleItemTouchHelperCallback}. Method should be update the view through the interface.
     */
    @Override
    public void onSwipedLeft(int position) {
        Log.d("ShoppingListAdapter", "Swiped to the left");
    }

    /**
     * Callback from the {@link SimpleItemTouchHelperCallback}. Method should be update the view through the interface.
     */
    @Override
    public void onSwipedRight(int position) {
        Log.d("ShoppingListAdapter", "Swiped to the right");
        listItemListener.onSwipeRight();
    }

    class ShoppingListItemHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        TextView itemName;
        TextView itemPrice;
        ImageView itemImage;
        ImageButton increaseQuantity;
        ImageButton decreaseQuantity;
        EditText inputQuantity;

        ShoppingListItemHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.txtItemTitle);
            itemPrice = itemView.findViewById(R.id.txtPrice);
            itemImage = itemView.findViewById(R.id.imgItem);
            increaseQuantity = itemView.findViewById(R.id.btnPlus);
            decreaseQuantity = itemView.findViewById(R.id.btnMinus);
            inputQuantity = itemView.findViewById(R.id.edQuantity);

            increaseQuantity.setOnClickListener(v -> increaseQuantity());
            decreaseQuantity.setOnClickListener(v -> decreaseQuantity());
            itemView.setOnLongClickListener(v -> {
                listItemListener.onHoldItem(itemName.getText().toString());
                return true;
            });
        }

        private void handleShoppingListItem(int number) {
            ShoppingItem item = shoppingItem.get(this.getAdapterPosition());
            ShoppingListItem listItem = null;
            Log.e("TAG", "Search for: " + item);
            for (ShoppingListItem it:shoppingList.getShoppingListItems()) {
                Log.e("TAG", "handleShoppingListItem: " + it);

                if(it.getShoppingItem().equals(item)) {
                    listItem = it;
                    break;
                }
            }

            if(number == 0 && listItem != null) {
                RepositoryProvider.getShoppingListItemRepositoryInstance()
                        .deleteEntity(listItem);
                shoppingList.getShoppingListItems().remove(listItem);
                notifyItemRemoved(this.getAdapterPosition());
                return;
            }

            if(listItem == null) {
                listItem = new ShoppingListItemBuilder()
                        .withShoppingItem(item)
                        .withShoppingList(shoppingList)
                        .withQuantity(number)
                        .withItemState(Globals.STATE_SELECTED)
                        .build();
            } else
                listItem.setQuantity(number);
            RepositoryProvider.getShoppingListItemRepositoryInstance()
                    .saveEntity(listItem);
            if(shoppingList.getShoppingListItems().contains(listItem)) {
                shoppingList.getShoppingListItems().set(shoppingList.getShoppingListItems().indexOf(listItem), listItem);
                notifyItemChanged(this.getAdapterPosition());

            } else {
                shoppingList.getShoppingListItems().add(listItem);
                notifyDataSetChanged();
            }
        }

        private void increaseQuantity() {
            String text = inputQuantity.getText().toString();
            int number = Integer.parseInt(text);
            number++;
            if (number < 200) {
                inputQuantity.setText(Integer.toString(number));
            }
            setPrice(number);

            handleShoppingListItem(number);
        }

        private void decreaseQuantity() {
            String text = inputQuantity.getText().toString();
            int number = Integer.parseInt(text);
            number--;
            if (number >= 0) {
                inputQuantity.setText(Integer.toString(number));
            }
            setPrice(number);

            handleShoppingListItem(number);
        }

        private void setPrice(int number) {
            int position = getAdapterPosition() == 0 ? 10 : getAdapterPosition();
            itemPrice.setText(String.format("CHF %.2f", (float) number * position));
        }

        /**
         * Callback from the {@link SimpleItemTouchHelperCallback}. Method should be update the view through the interface.
         */
        @Override
        public void onItemSelected() {
            Log.d("ShoppingListAdapter", "in view holder onItemSelected");

        }

        /**
         * Callback from the {@link SimpleItemTouchHelperCallback}. Method should be update the view through the interface.
         */
        @Override
        public void onItemClear() {
            Log.d("ShoppingListAdapter", "in view holder onItemClear");

        }
    }
}
