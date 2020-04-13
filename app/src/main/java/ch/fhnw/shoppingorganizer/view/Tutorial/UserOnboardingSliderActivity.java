package ch.fhnw.shoppingorganizer.view.Tutorial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

import ch.fhnw.shoppingorganizer.R;
import ch.fhnw.shoppingorganizer.model.Globals;

import static ch.fhnw.shoppingorganizer.view.Tutorial.OnboardingSliderType.*;

public class UserOnboardingSliderActivity extends TutorialActivity {

    private final String PREF_ONBOARDING = "UserOnboadringSlider";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if(intent.hasExtra(Globals.INTENT_ONBOARDING_SLIDER_TYPE) && valueOf(intent.getStringExtra(Globals.INTENT_ONBOARDING_SLIDER_TYPE)).ordinal() >= 0) {
            int type = valueOf(intent.getStringExtra(Globals.INTENT_ONBOARDING_SLIDER_TYPE)).ordinal();
            prefs = getApplicationContext().getSharedPreferences(PREF_ONBOARDING, MODE_PRIVATE);
            switch(type) {
                case 0: //ONBOARDING_SHOPPING_LIST
                    if(!prefs.getBoolean(ONBOARDING_SHOPPING_LIST.toString(), false)) {
                        onCreateShoppingList(savedInstanceState);
                        safePreferences(ONBOARDING_SHOPPING_LIST.toString());
                    } else
                        finish();
                    break;
                case 1: //ONBOARDING_SHOPPING_ITEM_LIST
                    if(!prefs.getBoolean(ONBOARDING_SHOPPING_ITEM_LIST.toString(), false)) {
                        onCreateShoppingItemList(savedInstanceState);
                        safePreferences(ONBOARDING_SHOPPING_ITEM_LIST.toString());
                    } else
                        finish();
                    break;
                case 2: //ONBOARDING_SHOPPING_ITEM_EDIT
                    if(!prefs.getBoolean(ONBOARDING_SHOPPING_ITEM_EDIT.toString(), false)) {
                        onCreateShoppingItemEdit(savedInstanceState);
                        safePreferences(ONBOARDING_SHOPPING_ITEM_EDIT.toString());
                    } else
                        finish();
                    break;
                default:
                    finish();
                    break;
            }
        } else
            finish();
    }

    private void safePreferences(String preference) {
        //TODO: Activate preferences after testing tutorial
//        SharedPreferences.Editor edit = prefs.edit();
//        edit.putBoolean(preference, true);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String time = dateFormat.format(new Date());
//        edit.putString(preference + "Time", time);
//        edit.apply();
    }

    private void onCreateShoppingList(Bundle savedInstanceState) {
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_list_delete_title))
                .setContent(getString(R.string.tutorial_shopping_list_delete_content))
                .setBackgroundColor(R.color.colorDelete) // int background color
                .setDrawable(R.drawable.shopping_list_delete) // int top drawable
                .build());
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_list_open_title))
                .setContent(getString(R.string.tutorial_shopping_list_open_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_list_open) // int top drawable
                .build());
        addFragment(new Step.Builder().setTitle(getString(R.string.tutorial_shopping_list_edit_name_title))
                .setContent(getString(R.string.tutorial_shopping_list_edit_name_content))
                .setBackgroundColor(R.color.colorAccent) // int background color
                .setDrawable(R.drawable.shopping_list_edit_name) // int top drawable
                .build());
    }
    private void onCreateShoppingItemList(Bundle savedInstanceState) {
        addFragment(new Step.Builder().setTitle("...")
                .setContent("...")
                .setBackgroundColor(R.color.colorDelete) // int background color
//                .setDrawable(R.drawable.shopping_list_delete) // int top drawable
                .build());
    }
    private void onCreateShoppingItemEdit(Bundle savedInstanceState) {
        addFragment(new Step.Builder().setTitle("...")
                .setContent("...")
                .setBackgroundColor(R.color.colorDelete) // int background color
//                .setDrawable(R.drawable.shopping_list_delete) // int top drawable
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
