package ch.fhnw.shoppingorganizer.model;

import java.text.NumberFormat;
import java.util.Locale;

public class Globals {

    //constants for holding state information
    public final static boolean STATE_INACTIVE = false;
    public final static boolean STATE_ACTIVE = true;

    /*constants for holding item state in ShoppingList
    false = not selected
    true = selected, quantity >=1
     */
    public final static boolean STATE_SELECTED = true;
    public final static boolean STATE_DESELECTED = false;

    public final static String EMPTY_STRING = "";

    public static final NumberFormat NUMBERFORMAT = NumberFormat.getInstance(new Locale("de", "CH"));
}
