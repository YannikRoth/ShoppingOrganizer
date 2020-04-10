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

import ch.fhnw.shoppingorganizer.R;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListItem> implements ItemTouchHelperAdapter {
    private String[] shoppingList;
    private ShoppingListItemListener listItemListener;

    ShoppingListAdapter(String[] shoppingLists, ShoppingListItemListener listener) {
        this.shoppingList = shoppingLists;
        this.listItemListener = listener;
    }

    /**
     * RecyclerView internal method which will create view holder class for each item
     */
    @NonNull
    @Override
    public ShoppingListAdapter.ShoppingListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListAdapter.ShoppingListItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false));
    }

    /**
     * RecyclerView internal method which will called for each item after change
     */
    @Override
    public void onBindViewHolder(@NonNull ShoppingListAdapter.ShoppingListItem holder, int position) {
        holder.itemName.setText("Name: " + position);
        holder.itemImage.setImageResource(R.mipmap.ic_launcher);

        float price = Integer.parseInt(holder.inputQuantity.getText().toString()) * position;
        holder.itemPrice.setText(String.format("CHF %.2f", price));
    }

    /**
     * RecyclerView internal method which will create item with size of the list
     */
    @Override
    public int getItemCount() {
        return shoppingList.length;
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

    class ShoppingListItem extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        TextView itemName;
        TextView itemPrice;
        ImageView itemImage;
        ImageButton increaseQuantity;
        ImageButton decreaseQuantity;
        EditText inputQuantity;

        ShoppingListItem(@NonNull View itemView) {
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

        private void increaseQuantity() {
            String text = inputQuantity.getText().toString();
            int number = Integer.parseInt(text);
            number++;
            if (number < 200) {
                inputQuantity.setText(Integer.toString(number));
            }
            setPrice(number);
        }

        private void decreaseQuantity() {
            String text = inputQuantity.getText().toString();
            int number = Integer.parseInt(text);
            number--;
            if (number >= 0) {
                inputQuantity.setText(Integer.toString(number));
            }
            setPrice(number);
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
