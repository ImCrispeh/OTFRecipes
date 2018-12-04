/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.presenter;

import android.os.AsyncTask;

import com.mad.otfrecipes.constants.PreferencesConstants;
import com.mad.otfrecipes.model.AddIngredientsModel;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.view.AddIngredientsView;

/** Provides functionality for handling user interaction in the AddIngredientsActivity */
public class AddIngredientsPresenter {
    private AddIngredientsView mView;
    private AddIngredientsModel mModel;
    private SharedPreferencesHelperImpl mSharedPrefsHelper;
    private SearchIngredientsTask mSearchIngredientsTask;

    //Constructor method
    public AddIngredientsPresenter(AddIngredientsView mView, AddIngredientsModel mModel, SharedPreferencesHelperImpl sharedPrefsHelper) {
        this.mView = mView;
        this.mModel = mModel;
        this.mSharedPrefsHelper = sharedPrefsHelper;
    }

    /** Enables speech recognition in Activity if it is set as on */
    public void setSpeechRecognition() {
        if (mSharedPrefsHelper.getSpeechStatus() == PreferencesConstants.SpeechRecognition.SPEECH_ON) {
            mView.activateSpeech();
        }
    }

    /** Calls for the search view to lose focus */
    public void searchResultsClickedOff() {
        mView.loseSearchFocus();
    }

    /** Stops the ingredient search AsyncTask if it is running */
    public void stopTask() {
        if (mSearchIngredientsTask != null && mSearchIngredientsTask.getStatus() == AsyncTask.Status.RUNNING) {
            mSearchIngredientsTask.cancel(true);
        }
    }

    /**
     * Stops any currently running ingredient search AsyncTask before executing a new one
     * @param ingredientToSearch ingredient text to pass in for the API call
     */
    public void searchButtonClicked(String ingredientToSearch) {
        if (mSearchIngredientsTask != null && mSearchIngredientsTask.getStatus() == AsyncTask.Status.RUNNING) {
            stopTask();
        }

        mSearchIngredientsTask = new SearchIngredientsTask(ingredientToSearch);
        mSearchIngredientsTask.execute();
    }

    //Basic button/voice handlers that only call view methods
    public void ingredientResultClicked(int pos) {
        mView.addIngredient(pos);
    }

    public void ingredientDeleteButtonClicked(int pos) {
        mView.deleteIngredient(pos);
    }

    public void saveButtonClicked() {
        mView.saveIngredients();
    }

    public void clearButtonClicked() {
        mView.clearIngredients();
    }

    public void hintRequested() {
        mView.showVoiceHint();
    }

    public void upButtonClicked() {
        mView.navigateUp();
    }

    /** AsyncTask for calling the model to handle API calling to search for ingredients */
    private class SearchIngredientsTask extends AsyncTask<Void, Void, String[]> {
        private String mIngredientToSearch;

        SearchIngredientsTask(String ingredientToSearch) {
            this.mIngredientToSearch = ingredientToSearch;
        }

        /** Show progress bar to user to indicate loading */
        @Override
        protected void onPreExecute() {
            mView.showIngredientResultsProgress();
        }

        /**
         * Calls model to query API for an ingredients search
         * @return results of API call
         */
        @Override
        protected String[] doInBackground(Void... voids) {
            return mModel.queryIngredients(mIngredientToSearch);
        }

        /**
         * Calls view to show ingredient search results of API call to user
         * @param results results of API call
         */
        @Override
        protected void onPostExecute(String[] results) {
            mView.hideIngredientResultsProgress();
            mView.showIngredientResults(results);
        }
    }
}
