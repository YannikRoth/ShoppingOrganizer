package ch.fhnw.shoppingorganizer.model;

import java.text.NumberFormat;
import java.util.Locale;

public class Globals {
    public final static boolean SHOPPING_ITEM_STATE_INACTIVE = false;
    public final static boolean SHOPPING_ITEM_STATE_ACTIVE = true;

    public final static boolean SHOPPING_LIST_ITEM_STATE_CHECKED = true;
    public final static boolean SHOPPING_LIST_ITEM_STATE_UNCHECKED = false;

    public final static String EMPTY_STRING = "";

    //View
    public static final NumberFormat NUMBERFORMAT = NumberFormat.getInstance(new Locale("de", "CH"));
    public final static String INTENT_TUTORIAL_TYPE = "SliderType";

    public final static String PREF_TUTORIAL = "UserTutorialSlider";
    public final static String PREF_LIFECYCLE = "LifecyclePrefs";

    public final static int IMPORT_ACTIVITY_REQ_IDENTIFIER = 1;
}
