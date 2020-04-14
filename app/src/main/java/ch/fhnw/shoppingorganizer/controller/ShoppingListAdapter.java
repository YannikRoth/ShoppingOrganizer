package ch.fhnw.shoppingorganizer.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingList;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItem;
import ch.fhnw.shoppingorganizer.model.businessobject.ShoppingListItemBuilder;
import ch.fhnw.shoppingorganizer.model.database.RepositoryProvider;
import ch.fhnw.shoppingorganizer.model.database.ShoppingListItemRepository;

public abstract class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListItemHolder> implements Filterable, ListItemInteractionInterface {
    private Context context;
    private ShoppingList shoppingList;
    private List<ShoppingItem> shoppingItem;
    private List<ShoppingItem> shoppingItemFull;
    private final String TAG = this.getClass().getSimpleName();

    private final long QUANTITY_MIN_VALUE = 0;
    private final long QUANTITY_MAX_VALUE = 1000000;

    private ShoppingListItemRepository shoppingListItemRepository = RepositoryProvider.getShoppingListItemRepositoryInstance();

    public ShoppingListAdapter(Context context, ShoppingList shoppingList, List<ShoppingItem> shoppingItem, RecyclerView recyclerView) {
        this.context = context;
        this.shoppingList = shoppingList;
        this.shoppingItem = shoppingItem;
        this.sortShoppingItems(shoppingItem);
        shoppingItemFull = new ArrayList<ShoppingItem>(shoppingItem);
        this.createShoppingListItemTouchHelper(recyclerView, getSwipeDirs());
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public ShoppingListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.shopping_list_item, parent, false);
        return new ShoppingListItemHolder(view);
    }

    int defaultPaintFlags = 0;
    @Override
    public void onBindViewHolder(@NonNull ShoppingListAdapter.ShoppingListItemHolder holder, int position) {
        ShoppingItem item = shoppingItem.get(position);
        ShoppingListItem listItem = shoppingList.getShoppingListItem(item);

        holder.itemName.setText(context.getString(R.string.shopping_list_item_name) + ": " + item.getItemName());
        if(listItem != null && listItem.isItemState()) {
            if(defaultPaintFlags == 0)
                holder.itemName.getPaintFlags();
            holder.itemName.setPaintFlags(defaultPaintFlags | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemName.setTextColor(context.getResources().getColor(R.color.colorTextChecked, context.getTheme()));
        } else {
            holder.itemName.setPaintFlags(defaultPaintFlags);
            holder.itemName.setTextColor(context.getResources().getColor(android.R.color.black, context.getTheme()));
        }


        File itemImageFile = new File(item.getImgPath());
        if(itemImageFile != null && itemImageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(itemImageFile.getAbsolutePath());
            holder.itemImage.setImageBitmap(bitmap);
        } else {
            holder.itemImage.setImageResource(R.mipmap.ic_launcher);
        }

        if(listItem != null) {
            holder.itemPrice.setText(formatPrice(listItem.getTotalItemPrice()));
            holder.inputQuantity.setText(String.format("%d", listItem.getQuantity()));
        } else {
            holder.itemPrice.setText(formatPrice(new BigDecimal(0.00)));
            holder.inputQuantity.setText(Globals.NUMBERFORMAT.format(0));
        }

        Log.d("ShoppingListAdapter", "onBindViewHolder: end");
    }

    private String formatPrice(BigDecimal price) {
        return Globals.NUMBERFORMAT.getCurrency() + " " + Globals.NUMBERFORMAT.format(price);
    }

    /**
     * RecyclerView internal method which will create item with size of the list
     */
    @Override
    public int getItemCount() {
        return shoppingItem.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingListFilter;
    }
    private Filter shoppingListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ShoppingItem> filteredList = new ArrayList<ShoppingItem>();
            if(constraint == null || constraint.length() == 0)
                filteredList.addAll(shoppingItemFull);
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(ShoppingItem l:shoppingItemFull) {
                    if(l.getItemName().toLowerCase().contains(filterPattern)
                    || l.getCategory().toString().toLowerCase().contains(filterPattern))
                        filteredList.add(l);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            shoppingItem.clear();
            shoppingItem.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    class ShoppingListItemHolder extends RecyclerView.ViewHolder {

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
            inputQuantity.setFilters(new InputFilter[]{ new MinMaxFilter(String.format("%d", QUANTITY_MIN_VALUE), String.format("%d", QUANTITY_MAX_VALUE))});
            inputQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Long q = new Long(0);
                    if(!s.toString().isEmpty()) {
                        try {
                            q = Long.parseLong(s.toString());
                        } catch (NumberFormatException e) {
                            q = Long.MAX_VALUE;
                        }
                    }
                    handleShoppingListItem(q, false);
                    ShoppingItem item = shoppingItem.get(getAdapterPosition());
                    ShoppingListItem listItem = shoppingList.getShoppingListItem(item);
                    if(listItem != null)
                        itemPrice.setText(formatPrice(listItem.getTotalItemPrice()));
                    else
                        itemPrice.setText(formatPrice(new BigDecimal(0.00)));
                }
            });

            increaseQuantity.setOnClickListener(v -> increaseQuantity());
            decreaseQuantity.setOnClickListener(v -> decreaseQuantity());
            itemView.setOnClickListener(v -> onItemClick(v, getAdapterPosition()));
            itemView.setOnLongClickListener(v -> {
                onLongItemClick(v, getAdapterPosition());
                return true;
            });
        }



        protected void handleShoppingListItem(long number, boolean notifyChange) {
            ShoppingItem item = shoppingItem.get(this.getAdapterPosition());
            ShoppingListItem listItem = shoppingList.getShoppingListItem(item);
            //0 = Delete form List
            if(number == 0 && listItem != null) {
                shoppingList.getShoppingListItems().remove(listItem);
                shoppingListItemRepository.deleteEntity(listItem);
                if(notifyChange)
                    notifyItemChanged(this.getAdapterPosition());
                Log.d(TAG, "Shopping Item " + listItem.getShoppingItem().getItemName() + " removed");
                return;
            }

            if(listItem == null) {
                listItem = new ShoppingListItemBuilder()
                        .withShoppingItem(item)
                        .withShoppingList(shoppingList)
                        .withQuantity(number)
                        .withItemState(Globals.SHOPPING_LIST_ITEM_STATE_UNCHECKED)
                        .build();
            } else
                listItem.setQuantity(number);
            shoppingListItemRepository.saveEntity(listItem);
            if(shoppingList.getShoppingListItems().contains(listItem)) {
                int position = shoppingList.getShoppingListItems().indexOf(listItem);
                shoppingList.getShoppingListItems().set(position, listItem);
                if(notifyChange)
                    notifyItemChanged(this.getAdapterPosition());
                Log.d(TAG, "Shopping Item " + listItem.getShoppingItem().getItemName() + " updated");
            } else {
                shoppingList.getShoppingListItems().add(listItem);
                if(notifyChange)
                    notifyItemChanged(this.getAdapterPosition());
                Log.d(TAG, "Shopping Item " + listItem.getShoppingItem().getItemName() + " inserted");
            }
        }

        private void increaseQuantity() {
            String text = inputQuantity.getText().toString().replace("'", "");
            if(text.isEmpty())
                text = "0";
            long number = Long.parseLong(text);
            if(number < QUANTITY_MAX_VALUE)
                number++;
            handleShoppingListItem(number, true);
        }

        private void decreaseQuantity() {
            String text = inputQuantity.getText().toString().replace("'", "");
            if(text == "" || text == "0" || text.isEmpty())
                return;
            Long number = Long.parseLong(text);
            if(number > QUANTITY_MIN_VALUE)
                number--;
            handleShoppingListItem(number, true);
        }
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
                int position = viewHolder.getAdapterPosition();

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
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
                int position = holder.getAdapterPosition();
                ShoppingItem item = shoppingItem.get(position);
                return shoppingList.getShoppingListItem(item) == null ? 0:super.getSwipeDirs(recyclerView, holder);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                onChildDrawDetails(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
    }

    //Pull-Refresh SwipeRefreshLayout
    public void onRefreshViewOnPull() {
//        shoppingListFilter.
        shoppingItem.clear();
        sortShoppingItems(shoppingItemFull);
        shoppingItem.addAll(shoppingItemFull);
        this.notifyDataSetChanged();
    }

    public void addShoppingItem(ShoppingItem shoppingItem) {
        if(!shoppingItemFull.contains(shoppingItem)) {
            shoppingItemFull.add(shoppingItem);
            onRefreshViewOnPull();
        }
    }

    private void sortShoppingItems(List<ShoppingItem> items) {
        Collections.sort(items, (o1, o2) -> {
            int i1 = 3, i2 = 3;
            for(ShoppingListItem it:shoppingList.getShoppingListItems()) {
                if(it.getShoppingItem().equals(o1)) {
                    if(it.isItemState())
                        i1 = 2;
                    else
                        i1 = 1;
                }
                if(it.getShoppingItem().equals(o2)) {
                    if(it.isItemState())
                        i2 = 2;
                    else
                        i2 = 1;
                }
            }
            if(i1 == i2)
                return o1.getItemName().compareTo(o2.getItemName());
            return i1-i2;//Globals.STATE_SELECTED
        });
    }


}