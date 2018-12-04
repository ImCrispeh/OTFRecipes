/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.presenter;

import android.os.AsyncTask;

import com.mad.otfrecipes.constants.PreferencesConstants;
import com.mad.otfrecipes.model.Recipe;
import com.mad.otfrecipes.model.RecipeListModel;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.view.RecipeListView;

import java.util.ArrayList;

/** Provides functionality for handling user interaction in the RecipeListActivity */
public class RecipeListPresenter {
    private RecipeListView mView;
    private RecipeListModel mModel;
    private SharedPreferencesHelperImpl mSharedPrefsHelper;
    private LoadRecipesTask mLoadRecipesTask;

    //Constructor method
    public RecipeListPresenter(RecipeListView mView, RecipeListModel mModel, SharedPreferencesHelperImpl sharedPrefsHelper) {
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

    /** Stops the recipe and loading AsyncTask if it is running */
    public void stopTask() {
        if (mLoadRecipesTask != null && mLoadRecipesTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadRecipesTask.cancel(true);
        }
    }

    /**
     * Stops any currently running recipe search and loading AsyncTask before executing a new one
     * @param ingredientsToSearch list of ingredients to pass in for API call
     */
    public void loadRecipesHandler(ArrayList<String> ingredientsToSearch) {
        if (mLoadRecipesTask != null && mLoadRecipesTask.getStatus() == AsyncTask.Status.RUNNING) {
            stopTask();
        }

        mLoadRecipesTask = new LoadRecipesTask(ingredientsToSearch);
        mLoadRecipesTask.execute();
    }

    //Basic button/voice handlers that only call view methods
    public void recipeClicked(Recipe recipe) {
        mView.viewRecipe(recipe);
    }

    public void hintRequested() {
        mView.showVoiceHint();
    }

    public void upButtonClicked() {
        mView.navigateUp();
    }

    /**
     * AsyncTask for calling the model to handle API calling to search for recipes based on given
     * parameters
     */
    private class LoadRecipesTask extends AsyncTask<Void, Void, ArrayList<Recipe>> {
        private ArrayList<String> mIngredientsForFilter;

        LoadRecipesTask(ArrayList<String> ingredientsForFilter) {
            this.mIngredientsForFilter = ingredientsForFilter;
        }

        /** Show progress bar to user to indicate loading */
        @Override
        protected void onPreExecute() {
            mView.showProgress();
        }

        /**
         * Calls model to set any restrictions for the recipe search API call before making the actual
         * call
         * @return results of API call
         */
        @Override
        protected ArrayList<Recipe> doInBackground(Void... voids) {
            mModel.setIngredientRestrict(mSharedPrefsHelper.getIngredientRestriction());
            mModel.setIngredientRestrictNumber(mSharedPrefsHelper.getIngredientNumber());
            mModel.setCalorieRestrict(mSharedPrefsHelper.getCalorieRestriction());
            mModel.setCalorieRestrictNumber(mSharedPrefsHelper.getCalorieNumber());
            mModel.setFatRestrict(mSharedPrefsHelper.getFatRestriction());
            mModel.setFatRestrictNumber(mSharedPrefsHelper.getFatNumber());
            mModel.setSugarRestrict(mSharedPrefsHelper.getSugarRestriction());
            mModel.setSugarRestrictNumber(mSharedPrefsHelper.getSugarNumber());
            mModel.setDiet(mSharedPrefsHelper.getDiet());
            return mModel.queryRecipes(mIngredientsForFilter);
        }

        /**
         * Calls view to display recipe search results of API call to user
         * @param recipes results of API call
         */
        @Override
        protected void onPostExecute(ArrayList<Recipe> recipes) {
            mView.showRecipes(recipes);
            mView.hideProgress();
        }
    }
}
