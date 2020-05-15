package ch.fhnw.shoppingorganizer.view.Tutorial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;

import static ch.fhnw.shoppingorganizer.view.Tutorial.TutorialType.*;

public class TutorialSliderActivity extends TutorialActivity {

    private SharedPreferences prefs;
    private TutorialType tutorialType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if(intent.hasExtra(Globals.INTENT_TUTORIAL_TYPE) && valueOf(intent.getStringExtra(Globals.INTENT_TUTORIAL_TYPE)).ordinal() >= 0) {
            int type = valueOf(intent.getStringExtra(Globals.INTENT_TUTORIAL_TYPE)).ordinal();
            prefs = getApplicationContext().getSharedPreferences(Globals.PREF_TUTORIAL, MODE_PRIVATE);
            switch(type) {
                case 0: //TUTORIAL_SHOPPING_LIST
                    tutorialType = TUTORIAL_SHOPPING_LIST;
                    onCreateShoppingList(savedInstanceState);
                    break;
                case 1: //TUTORIAL_SHOPPING_ITEM_LIST
                    tutorialType = TUTORIAL_SHOPPING_ITEM_LIST;
                    onCreateShoppingItemList(savedInstanceState);
                    break;
                case 2: //TUTORIAL_SHOPPING_ITEM_EDIT
                        tutorialType = TUTORIAL_SHOPPING_ITEM_EDIT;
                        onCreateShoppingItemEdit(savedInstanceState);
                    break;
                default:
                    finish();
                    break;
            }
        } else
            finish();
    }

    public static void savePreferences(SharedPreferences prefs, TutorialType type) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(type.toString(), true);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(new Date());
        edit.putString(type.toString() + "Time", time);
        edit.apply();
    }

    private void onCreateShoppingList(Bundle savedInstanceState) {
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_list_edit_name_title))
                .setContent(getString(R.string.tutorial_shopping_list_edit_name_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_list_edit_name) // int top drawable
                .build());
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_list_open_title))
                .setContent(getString(R.string.tutorial_shopping_list_open_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_list_open) // int top drawable
                .build());
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_list_export_title))
                .setContent(getString(R.string.tutorial_shopping_list_export_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_list_export) // int top drawable
                .build());
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_list_delete_title))
                .setContent(getString(R.string.tutorial_shopping_list_delete_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_list_delete) // int top drawable
                .build());
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_list_delete_undo_title))
                .setContent(getString(R.string.tutorial_shopping_list_delete_undo_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_list_delete_undo) // int top drawable
                .build());
    }
    private void onCreateShoppingItemList(Bundle savedInstanceState) {
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_item_list_check_title))
                .setContent(getString(R.string.tutorial_shopping_item_list_check_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_item_list_check) // int top drawable
                .build());
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_item_list_delete_title))
                .setContent(getString(R.string.tutorial_shopping_item_list_delete_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_item_list_delete) // int top drawable
                .build());
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_item_list_edit_title))
                .setContent(getString(R.string.tutorial_shopping_item_list_edit_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_item_list_edit) // int top drawable
                .build());
    }
    private void onCreateShoppingItemEdit(Bundle savedInstanceState) {
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_item_edit_image_title))
                .setContent(getString(R.string.tutorial_shopping_item_edit_image_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_item_edit_image) // int top drawable
                .build());
     addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_item_edit_tutorial_title))
                .setContent(getString(R.string.tutorial_shopping_item_edit_tutorial_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_item_edit_tutorial) // int top drawable
                .build());
    }

    @Override
    public void finishTutorial() {
        finish();
    }

    @Override
    public void currentFragmentPosition(int position) {

    }
}
