/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.otfrecipes.R;
import com.mad.otfrecipes.adapter.IngredientAdapter;
import com.mad.otfrecipes.adapter.IngredientResultAdapter;
import com.mad.otfrecipes.constants.ExtrasConstants;
import com.mad.otfrecipes.model.AddIngredientsModel;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.presenter.AddIngredientsPresenter;
import com.mad.otfrecipes.speech.SpeechActivator;
import com.mad.otfrecipes.speech.SpeechProcessor;
import com.mad.otfrecipes.view.AddIngredientsView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Provides the user with the ability to search the API for ingredients through the use of an EditText
 * and add them to an ingredients to add list. This list can be passed back to the MainActivity
 * and added to the ingredients that are used for the recipe search filter. Can remove select items
 * from the ingredients to add list or clear the entire list
 */
public class AddIngredientsActivity extends AppCompatActivity implements AddIngredientsView, SpeechProcessor {
    private RecyclerView mIngredientRecyclerView;
    private IngredientAdapter mIngredientAdapter;
    private ArrayList<String> mIngredientsList;

    private RecyclerView mIngredientResultRecyclerView;
    private IngredientResultAdapter mIngredientResultAdapter;
    private ArrayList<String> mIngredientResultsList;

    private EditText mSearchText;
    private Button mSearchBtn;
    private Button mSaveBtn;
    private Button mClearBtn;
    private CardView mIngredientResultsCardView;
    private ProgressBar mIngredientResultsProgress;
    private TextView mNoResultsText;

    private AddIngredientsPresenter mPresenter;
    private SpeechActivator mSpeechActivator;
    private String[] mVoiceHints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPresenter = new AddIngredientsPresenter(this, new AddIngredientsModel(), new SharedPreferencesHelperImpl(this.getApplicationContext()));

        //Set up RecyclerView of ingredients to add
        mIngredientsList = new ArrayList<String>();
        mIngredientRecyclerView = findViewById(R.id.recycler_view_ingredient);
        mIngredientRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mIngredientAdapter = new IngredientAdapter(mIngredientsList, this);
        mIngredientRecyclerView.setAdapter(mIngredientAdapter);

        //Set up RecyclerView of ingredient search results
        mIngredientResultsList = new ArrayList<String>();
        mIngredientResultRecyclerView = findViewById(R.id.recycler_view_ingredient_result);
        mIngredientResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mIngredientResultAdapter = new IngredientResultAdapter(mIngredientResultsList, this);
        mIngredientResultRecyclerView.setAdapter(mIngredientResultAdapter);

        //Set up other views e.g. buttons
        mSearchText = findViewById(R.id.edit_text_search);
        mSearchBtn = findViewById(R.id.btn_search);
        mSaveBtn = findViewById(R.id.btn_save);
        mClearBtn = findViewById(R.id.btn_clear);

        mIngredientResultsCardView = findViewById(R.id.card_view_search_results);
        mIngredientResultsProgress = findViewById(R.id.progress_ingredient_result);
        mNoResultsText = findViewById(R.id.text_no_results);

        mSearchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showResultsView();
                } else {
                    closeSearchResults(v);
                }
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchIngredients();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveButtonClicked();
            }
        });

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clearButtonClicked();
            }
        });

        mVoiceHints = getResources().getStringArray(R.array.add_ingredients_voice_hints);

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

        //Command for searching for ingredients
        else if (result.contains("search")) {
            result = result.replace("search", "");
            if (result.contains("for")) {
                result = result.replace("for", "");
            }
            mSearchText.setText(result.trim());
            searchIngredients();
        }

        //Command for adding ingredient from search results to ingredients to add list
        else if (result.contains("add")) {
            result = result.replace("add", "");
            result = result.replaceAll("\\s+", "");
            for (int i = 0; i < mIngredientResultsList.size(); i++) {
                if (result.equals(mIngredientResultsList.get(i).replaceAll("\\s+", ""))) {
                    addIngredient(i);
                }
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

        //Command for saving ingredients and adding them back to MainActivity
        else if (result.contains("save")) {
            mPresenter.saveButtonClicked();
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
            //Set call for up navigation
            case android.R.id.home:
                mPresenter.upButtonClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Used to detect any touches that are outside the cardView, editText and button used
     * for the ingredient searching functionality and close the search results if any occur
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect searchRect = new Rect();
                Rect resultsRect = new Rect();
                v.getGlobalVisibleRect(searchRect);
                mIngredientResultsCardView.getGlobalVisibleRect(resultsRect);
                int x = (int)event.getRawX();
                int y = (int)event.getRawY();
                if (!searchRect.contains(x, y) && !resultsRect.contains(x, y)) {
                    mPresenter.searchResultsClickedOff();
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    /** Forces the search text to lose focus */
    @Override
    public void loseSearchFocus() {
        mSearchText.clearFocus();
    }

    /** Shows ingredient search results */
    public void showResultsView() {
        mIngredientResultsCardView.setVisibility(View.VISIBLE);
    }

    /** Closes ingredient search results and the keyboard */
    @Override
    public void closeSearchResults(View v) {
        if (mIngredientResultsCardView.getVisibility() == View.VISIBLE) {
            mIngredientResultsCardView.setVisibility(View.GONE);
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * Clears any results that are already showing in the cardView before initiating a search
     * using the text entered into the search box
     */
    public void searchIngredients() {
        mIngredientResultsList.clear();
        mIngredientResultAdapter.notifyDataSetChanged();
        String ingredientToSearch = mSearchText.getText().toString();
        mPresenter.searchButtonClicked(ingredientToSearch);
    }

    /** Shows progress bar while searching for ingredients */
    @Override
    public void showIngredientResultsProgress() {
        mIngredientResultsCardView.setVisibility(View.VISIBLE);
        mIngredientRecyclerView.setVisibility(View.GONE);
        mNoResultsText.setVisibility(View.GONE);
        mIngredientResultsProgress.setVisibility(View.VISIBLE);
    }

    /** Hides progress bar and shows ingredient search results */
    @Override
    public void hideIngredientResultsProgress() {
        mIngredientResultsProgress.setVisibility(View.GONE);
        mIngredientRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows any results returned from the ingredient search in the recyclerView in the results
     * cardView
     * @param foundIngredients returned ingredient search results
     */
    @Override
    public void showIngredientResults(String[] foundIngredients) {
        mSearchText.requestFocus();

        if (foundIngredients.length > 0) {
            for (String foundIngredient : foundIngredients) {
                mIngredientResultsList.add(foundIngredient);
                Log.d("test", foundIngredient);
            }
            mIngredientResultAdapter.notifyDataSetChanged();
            mIngredientResultsCardView.setVisibility(View.VISIBLE);
            mIngredientResultRecyclerView.setVisibility(View.VISIBLE);
            Log.d("test", "correct");
        } else {
            mNoResultsText.setVisibility(View.VISIBLE);
            mIngredientResultRecyclerView.setVisibility(View.GONE);
        }

    }

    /**
     * Calls the presenter handler for adding an ingredient to the list of ingredients to add to
     * the recipe search through clicking
     * @param pos position of the clicked ingredient in the results list
     */
    public void ingredientResultHandler(int pos) {
        mPresenter.ingredientResultClicked(pos);
    }

    /**
     * Adds the clicked ingredient to the ingredients to add list
     * @param pos position of the item to add in the results list
     */
    @Override
    public void addIngredient(int pos) {
        mIngredientsList.add(mIngredientResultsList.get(pos));
        mIngredientAdapter.notifyDataSetChanged();
        loseSearchFocus();
    }

    /**
     * Calls the handler to remove an ingredient from the ingredients to add list
     * @param pos position of the item to delete in the ingredients to add list
     */
    public void deleteIngredientHandler(int pos) {
        mPresenter.ingredientDeleteButtonClicked(pos);
    }

    /**
     * Removes ingredient from the ingredients to add list
     * @param pos position of the item to delete in the ingredients to add list
     */
    @Override
    public void deleteIngredient(int pos) {
        mIngredientsList.remove(pos);
        mIngredientAdapter.notifyItemRemoved(pos);
        mIngredientAdapter.notifyItemRangeChanged(pos, mIngredientAdapter.getItemCount());
    }

    /** Passes the ingredients to all list back to the main activity and moves back to the MainActivity */
    @Override
    public void saveIngredients() {
        Intent data = new Intent();
        data.putExtra(ExtrasConstants.INGREDIENTS_TO_ADD, mIngredientsList);
        setResult(RESULT_OK, data);
        if (mSpeechActivator != null) {
            mSpeechActivator.stopListeningForSpeech();
        }
        Toast.makeText(this.getApplicationContext(), getString(R.string.ingredients_saved_toast), Toast.LENGTH_SHORT).show();
        finish();
    }

    /** Clears the ingredients to add list */
    @Override
    public void clearIngredients() {
        mIngredientsList.clear();
        mIngredientAdapter.notifyDataSetChanged();
        Toast.makeText(this.getApplicationContext(), getString(R.string.ingredients_cleared_toast), Toast.LENGTH_SHORT).show();
    }

    /** Navigates to parent activity (MainActivity) */
    @Override
    public void navigateUp() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onPause() {
        if (mSpeechActivator != null) {
            mSpeechActivator.stopListeningForSpeech();
        }
        super.onPause();
    }

    /** Calls presenter to stop any async tasks before destroying activity */
    @Override
    protected void onDestroy() {
        mPresenter.stopTask();
        super.onDestroy();
    }
}
