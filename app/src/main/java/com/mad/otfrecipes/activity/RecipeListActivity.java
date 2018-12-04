/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.otfrecipes.R;
import com.mad.otfrecipes.adapter.RecipeAdapter;
import com.mad.otfrecipes.constants.ExtrasConstants;
import com.mad.otfrecipes.model.Recipe;
import com.mad.otfrecipes.model.RecipeListModel;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.presenter.RecipeListPresenter;
import com.mad.otfrecipes.speech.SpeechActivator;
import com.mad.otfrecipes.speech.SpeechProcessor;
import com.mad.otfrecipes.view.RecipeListView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Initiates a search through the API for recipes that match the settings and any ingredients that
 * are being filtered by that were passed in from the MainActivity. Displays the results of this
 * search to the user and allows them to select a recipe to move to the RecipeActivity and display
 * more detailed information about it
 */
public class RecipeListActivity extends AppCompatActivity implements RecipeListView, SpeechProcessor {
    private RecyclerView mRecipeRecyclerView;
    private RecipeAdapter mRecipeAdapter;
    private ArrayList<Recipe> mRecipeList;

    private ProgressBar mRecipeProgress;
    private TextView mNoResultsText;

    private RecipeListPresenter mPresenter;
    private SpeechActivator mSpeechActivator;
    private String[] mVoiceHints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPresenter = new RecipeListPresenter(this, new RecipeListModel(), new SharedPreferencesHelperImpl(this.getApplicationContext()));

        //Set up RecyclerView for recipe search results
        mRecipeList = new ArrayList<Recipe>();
        mRecipeRecyclerView = findViewById(R.id.recycler_view_recipe);
        mRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecipeAdapter = new RecipeAdapter(mRecipeList, this);
        mRecipeRecyclerView.setAdapter(mRecipeAdapter);

        mRecipeProgress = findViewById(R.id.progress_recipe_list);
        mNoResultsText = findViewById(R.id.text_no_results);

        ArrayList<String> ingredients = getIntent().getExtras().getStringArrayList(ExtrasConstants.INGREDIENTS_FOR_SEARCH);
        if (ingredients != null) {
            mPresenter.loadRecipesHandler(getIntent().getExtras().getStringArrayList(ExtrasConstants.INGREDIENTS_FOR_SEARCH));
        } else {
            mNoResultsText.setText(R.string.recipe_list_error);
            showRecipes(new ArrayList<Recipe>());
        }

        mVoiceHints = getResources().getStringArray(R.array.recipe_list_voice_hints);

        mPresenter.setSpeechRecognition();
    }

    @Override
    protected void onPause() {
        if (mSpeechActivator != null) {
            mSpeechActivator.stopListeningForSpeech();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
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

        //Command for up navigation
        if (result.contains("back") || result.contains("return")) {
            mPresenter.upButtonClicked();
        }

        //Command for opening a recipe from the results of the search
        else if (result.contains("open") || result.contains("view")) {
            result = result.replace("open", "");
            result = result.replace("view", "");
            result = result.replaceAll("\\s+", "");
            for (int i = 0; i < mRecipeList.size(); i++) {
                if (result.contains(mRecipeList.get(i).getRecipeName().toLowerCase().replaceAll("\\s+", ""))) {
                    recipeClickedHandler(mRecipeList.get(i));
                }
            }
        }

        else if (result.contains("hint")) {
            mPresenter.hintRequested();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Set call for up navigation
                mPresenter.upButtonClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void recipeClickedHandler(Recipe recipe) {
        mPresenter.recipeClicked(recipe);
    }

    /** Shows progress bar while searching for recipes */
    @Override
    public void showProgress() {
        mRecipeRecyclerView.setVisibility(View.GONE);
        mNoResultsText.setVisibility(View.GONE);
        mRecipeProgress.setVisibility(View.VISIBLE);
    }

    /** Hides progress bar and shows any recipe search results */
    @Override
    public void hideProgress() {
        mRecipeProgress.setVisibility(View.GONE);
        mRecipeRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Adds and displays any returned recipes or an error message
     * @param recipes list of recipes to display
     */
    @Override
    public void showRecipes(ArrayList<Recipe> recipes) {
        mRecipeList.clear();

        if (recipes.size() > 0) {
            mRecipeList.addAll(recipes);
            mRecipeAdapter.notifyDataSetChanged();
        } else {
            mRecipeRecyclerView.setVisibility(View.GONE);
            mNoResultsText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Moves to RecipeActivity and passes parameters of the recipe to display and use
     * @param recipe recipe to display in RecipeActivity
     */
    @Override
    public void viewRecipe(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(ExtrasConstants.RECIPE_NAME, recipe.getRecipeName());
        intent.putExtra(ExtrasConstants.RECIPE_ID, recipe.getRecipeId());
        intent.putExtra(ExtrasConstants.RECIPE_IMAGE, recipe.getRecipeImageUrl());
        startActivity(intent);
    }

    /** Navigates to parent activity (MainActivity) */
    @Override
    public void navigateUp() {
        NavUtils.navigateUpFromSameTask(this);
    }

    /** Calls presenter to stop any async tasks before destroying activity */
    @Override
    protected void onDestroy() {
        mPresenter.stopTask();
        super.onDestroy();
    }
}
