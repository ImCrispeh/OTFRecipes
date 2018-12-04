/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mad.otfrecipes.R;
import com.mad.otfrecipes.adapter.IngredientAdapter;
import com.mad.otfrecipes.constants.ExtrasConstants;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.presenter.MainPresenter;
import com.mad.otfrecipes.speech.SpeechActivator;
import com.mad.otfrecipes.speech.SpeechProcessor;
import com.mad.otfrecipes.view.MainView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Displays the ingredients that are to be used in the recipe searching filter and the ability to
 * delete select items from it or clear the entire list. Also provides navigation to
 * AddIngredientsActivity, SettingsActivity, and RecipeListActivity
 */
public class MainActivity extends AppCompatActivity implements MainView, SpeechProcessor {
    private RecyclerView mIngredientRecyclerView;
    private IngredientAdapter mIngredientAdapter;
    private ArrayList<String> mIngredientsList;

    private Button mAddBtn;
    private Button mSettingsBtn;
    private Button mSearchBtn;
    private Button mClearBtn;

    private MainPresenter mPresenter;

    private final static int ADD_INGREDIENTS_REQUEST_CODE = 1;
    private SpeechActivator mSpeechActivator;
    private String[] mVoiceHints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainPresenter(this, new SharedPreferencesHelperImpl(this.getApplicationContext()));

        //Set up RecyclerView for recipe searching list
        mIngredientsList = new ArrayList<String>();
        mIngredientRecyclerView = findViewById(R.id.recycler_view_ingredient);
        mIngredientRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mIngredientAdapter = new IngredientAdapter(mIngredientsList, this);
        mIngredientRecyclerView.setAdapter(mIngredientAdapter);

        //Set up buttons
        mAddBtn = findViewById(R.id.btn_add);
        mSettingsBtn = findViewById(R.id.btn_settings);
        mSearchBtn = findViewById(R.id.btn_search);
        mClearBtn = findViewById(R.id.btn_clear);

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addButtonClicked();
            }
        });

        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.settingsButtonClicked();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.searchButtonClicked();
            }
        });

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clearButtonClicked();
            }
        });

        mPresenter.setInitialSettings();

        mVoiceHints = getResources().getStringArray(R.array.main_voice_hints);

        mPresenter.setSpeechRecognition();
    }

    @Override
    protected void onPause() {
        Log.d("lifecycle", "MAIN PAUSED");
        if (mSpeechActivator != null) {
            mSpeechActivator.stopListeningForSpeech();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("lifecycle", "MAIN RESUMED");
        super.onResume();
        mPresenter.setSpeechRecognition();
    }

    /** Creates object that holds speech recognition functionality and starts recording */
    @Override
    public void activateSpeech() {
        mSpeechActivator = new SpeechActivator(this, this);
        mSpeechActivator.activateSpeech();
    }

    /**
     * Extracts voice commands from results of speech recognition
     * @param result speech recognition results
     */
    @Override
    public void processSpeechResults(String result) {
        result = result.toLowerCase();

        //Command for adding ingredients
        if (result.contains("add")) {
            if (result.contains("ingredient")) {
                mPresenter.addButtonClicked();
            }
        }

        //Command for initiating search
        else if (result.contains("search")) {
            mPresenter.searchButtonClicked();
        }

        //Command for moving to settings
        else if (result.contains("go")) {
            if (result.contains("settings")) {
                mPresenter.settingsButtonClicked();
            }
        }

        //Command for clearing ingredients list
        else if (result.contains("clear")) {
            if (result.contains("ingredient")) {
                mPresenter.clearButtonClicked();
            }
        }

        //Command for deleting an item in ingredients list
        else if (result.contains("delete") || result.contains("remove")) {
            result = result.replace("delete", "");
            result = result.replace("remove", "");
            result = result.replaceAll("\\s+", "");
            //Iterate backwards to avoid ConcurrentModificationException
            for (int i = mIngredientsList.size() - 1; i > 0; i++) {
                if (result.equals(mIngredientsList.get(i).replaceAll("\\s+", ""))) {
                    deleteIngredientHandler(i);
                }
            }
        }

        else if (result.contains("hint")) {
            mPresenter.hintRequested();
        }

        else if (result.contains("close")) {
            if (result.contains("app")) {
                mPresenter.closeRequested();
            }
        }

        if (mSpeechActivator.getIsListening()) {
            mSpeechActivator.activateSpeech();
        }

    }

    /** Shows hint for possible voice command for the current activity */
    @Override
    public void showVoiceHint() {
        Random rand = new Random();
        int randInt = rand.nextInt(mVoiceHints.length);
        Toast.makeText(this.getApplicationContext(), mVoiceHints[randInt], Toast.LENGTH_LONG).show();
    }

    /**
     * Move to AddIngredientsActivity and call for result to be returned (any ingredients to add
     * to the recipe search list)
     */
    @Override
    public void navigateToAddIngredients() {
        startActivityForResult(new Intent(this, AddIngredientsActivity.class), ADD_INGREDIENTS_REQUEST_CODE);
    }

    /** Move to SettingsActivity */
    @Override
    public void navigateToSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    /** Move to RecipeListActivity and pass ingredients to use in the recipe search */
    @Override
    public void searchForRecipes() {
        Intent intent = new Intent(this, RecipeListActivity.class);
        intent.putStringArrayListExtra(ExtrasConstants.INGREDIENTS_FOR_SEARCH, mIngredientsList);
        startActivity(intent);
    }

    /**
     * Calls handler for deleting an ingredient from the recipe search list
     * @param pos position of item to delete in recipe search list
     */
    public void deleteIngredientHandler(int pos) {
        mPresenter.ingredientDeleteButtonClicked(pos);
    }

    /**
     * Deletes ingredient from the recipe search list
     * @param pos position of item to delete in recipe search list
     */
    @Override
    public void deleteIngredient(int pos) {
        mIngredientsList.remove(pos);
        mIngredientAdapter.notifyItemRemoved(pos);
        mIngredientAdapter.notifyItemRangeChanged(pos, mIngredientAdapter.getItemCount());
    }

    /** Clears all ingredients from the recipe search list */
    @Override
    public void clearIngredients() {
        mIngredientsList.clear();
        mIngredientAdapter.notifyDataSetChanged();
        Toast.makeText(this.getApplicationContext(), "Ingredients cleared", Toast.LENGTH_SHORT).show();
    }

    /** Gets result of any ingredients to add to the recipe search list and adds them */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_INGREDIENTS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> ingredientsToAdd = data.getStringArrayListExtra(ExtrasConstants.INGREDIENTS_TO_ADD);
                mIngredientsList.addAll(ingredientsToAdd);
                mIngredientAdapter.notifyDataSetChanged();
            }
        }
    }

    /** Finishes application */
    @Override
    public void closeApp() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mSpeechActivator != null) {
            mSpeechActivator.stopListeningForSpeech();
        }
        super.onDestroy();
    }
}
