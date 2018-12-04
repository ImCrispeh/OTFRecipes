/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.presenter;

import android.os.AsyncTask;

import com.mad.otfrecipes.constants.PreferencesConstants;
import com.mad.otfrecipes.model.RecipeModel;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.view.RecipeView;

import java.util.ArrayList;

/** Provides functionality for handling user interaction in the RecipeActivity */
public class RecipePresenter {
    private RecipeView mView;
    private RecipeModel mModel;
    private SharedPreferencesHelperImpl mSharedPrefsHelper;
    private LoadRecipeInfoTask mLoadRecipeInfoTask;
    private LoadRecipeMethodTask mLoadRecipeMethodTask;
    private int mIngredientCounter;
    private int mMethodCounter;
    private int mNutrientCounter;
    private int mMaxIngredient;
    private int mMaxMethod;
    private int mMaxNutrient;

    //Constructor method
    public RecipePresenter(RecipeView mView, RecipeModel mModel, SharedPreferencesHelperImpl sharedPrefsHelper) {
        this.mView = mView;
        this.mModel = mModel;
        this.mSharedPrefsHelper = sharedPrefsHelper;
        mIngredientCounter = -1;
        mMethodCounter = -1;
        mNutrientCounter = -1;
    }

    /** Enables speech recognition in Activity if it is set as on */
    public void setSpeechRecognition() {
        if (mSharedPrefsHelper.getSpeechStatus() == PreferencesConstants.SpeechRecognition.SPEECH_ON) {
            mView.activateSpeech();
        }
    }

    //Setter methods for providing the max indices used for reading recipe information
    public void setMaxIngredient(int maxIngredient) {
        mMaxIngredient = maxIngredient;
    }

    public void setMaxMethod(int maxMethod) {
        mMaxMethod = maxMethod;
    }

    public void setMaxNutrient(int maxNutrient) {
        mMaxNutrient = maxNutrient;
    }

    /** Stops the recipe info loading AsyncTask if it is running */
    public void stopInfoTask() {
        if (mLoadRecipeInfoTask != null && mLoadRecipeInfoTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadRecipeInfoTask.cancel(true);
        }
    }

    /** Stops the recipe method loading AsyncTask if it is running */
    public void stopMethodTask() {
        if (mLoadRecipeMethodTask != null && mLoadRecipeMethodTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadRecipeMethodTask.cancel(true);
        }
    }

    /**
     * Stops any currently running recipe info loading AsyncTask before executing a new one
     * @param recipeId id of recipe to pass in for API call
     */
    public void loadRecipeInfoHandler(String recipeId) {
        if (mLoadRecipeInfoTask != null && mLoadRecipeInfoTask.getStatus() == AsyncTask.Status.RUNNING) {
            stopInfoTask();
        }

        mLoadRecipeInfoTask = new LoadRecipeInfoTask(recipeId);
        mLoadRecipeInfoTask.execute();
    }

    /**
     * Stops any currently running recipe method loading AsyncTask before executing a new one
     * @param recipeId id of recipe to pass in for API call
     */
    public void loadRecipeMethodHandler(String recipeId) {
        if (mLoadRecipeMethodTask != null && mLoadRecipeMethodTask.getStatus() == AsyncTask.Status.RUNNING) {
            stopInfoTask();
        }

        mLoadRecipeMethodTask = new LoadRecipeMethodTask(recipeId);
        mLoadRecipeMethodTask.execute();
    }

    //Basic button handlers that only call view methods
    public void openWebPageButtonClicked() {
        mView.navigateToSource();
    }

    public void upButtonClicked() {
        mView.navigateUp();
    }

    /** Passes position of current ingredient to be read out by TextToSpeech */
    public void readCurrentIngredient() {
        if (mIngredientCounter == -1) {
            mIngredientCounter = 0;
        }
        mView.readIngredientText(mIngredientCounter);
    }

    /** Passes position of current method item to be read out by TextToSpeech */
    public void readCurrentMethod() {
        if (mMethodCounter == -1) {
            mMethodCounter = 0;
        }
        mView.readMethodText(mMethodCounter);
    }

    /** Passes position of current method item to be read out by TextToSpeech */
    public void readCurrentNutrient() {
        if (mNutrientCounter == -1) {
            mNutrientCounter = 0;
        }
        mView.readNutrientText(mNutrientCounter);
    }

    /** Passes position of next ingredient to be read out by TextToSpeech */
    public void readNextIngredient() {
        if (mIngredientCounter < mMaxIngredient) {
            mIngredientCounter++;
        }
        mView.readIngredientText(mIngredientCounter);
    }

    /** Passes position of next method item to be read out by TextToSpeech */
    public void readNextMethod() {
        if (mMethodCounter < mMaxMethod) {
            mMethodCounter++;
        }
        mView.readMethodText(mMethodCounter);
    }

    /** Passes position of next method item to be read out by TextToSpeech */
    public void readNextNutrient() {
        if (mNutrientCounter < mMaxNutrient) {
            mNutrientCounter++;
        }
        mView.readNutrientText(mNutrientCounter);
    }

    /** Passes position of next ingredient to be read out by TextToSpeech */
    public void readPreviousIngredient() {
        if (mIngredientCounter == -1) {
            mIngredientCounter = 0;
        }

        if (mIngredientCounter > 0) {
            mIngredientCounter--;
        }
        mView.readIngredientText(mIngredientCounter);
    }

    /** Passes position of next method item to be read out by TextToSpeech */
    public void readPreviousMethod() {
        if (mMethodCounter == -1) {
            mMethodCounter = 0;
        }

        if (mMethodCounter > 0) {
            mMethodCounter--;
        }
        mView.readMethodText(mMethodCounter);
    }

    /** Passes position of next method item to be read out by TextToSpeech */
    public void readPreviousNutrient() {
        if (mNutrientCounter < mMaxNutrient) {
            mNutrientCounter++;
        }
        mView.readNutrientText(mNutrientCounter);
    }

    public void hintRequested() {
        mView.showVoiceHint();
    }

    /**
     * AsyncTask for calling the model to handle API calling to get info (ingredients and source URL)
     * of specified recipe
     */
    private class LoadRecipeInfoTask extends AsyncTask<Void, Void, ArrayList<ArrayList<String>>> {
        private String mRecipeId;

        LoadRecipeInfoTask(String recipeId) {
            this.mRecipeId = recipeId;
        }

        /** Show progress bar to user to indicate loading */
        @Override
        protected void onPreExecute() {
            mView.showProgress();
        }

        /**
         * Calls model to query for the source URL and ingredients of the specified recipe
         * @return results of API call
         */
        @Override
        protected ArrayList<ArrayList<String>> doInBackground(Void... voids) {
            return mModel.queryRecipeUrlAndIngredients(mRecipeId);
        }

        /**
         * Calls view to set URL to pass to RecipeWebActivity and display ingredient retrieving results
         * of API call to user
         * @param recipeInfo results of API call
         */
        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> recipeInfo) {
            if (recipeInfo != null) {
                ArrayList<String> urlIngredients = recipeInfo.get(0);
                String recipeUrl = urlIngredients.get(0);
                ArrayList<String> recipeIngredients = new ArrayList<String>();

                for (int i = 1; i < urlIngredients.size(); i++) {
                    recipeIngredients.add(urlIngredients.get(i));
                }

                ArrayList<String> nutrients = recipeInfo.get(1);

                mView.showRecipeNutrients(nutrients);
                mView.setUrl(recipeUrl);
                mView.showRecipeIngredients(recipeIngredients);
            } else {
                mView.showError();
            }

            mView.hideProgress();
        }
    }

    /** AsyncTask for calling the model to handle API calling to get the method of specified recipe */
    private class LoadRecipeMethodTask extends AsyncTask<Void, Void, ArrayList<ArrayList<String>>> {
        private String mRecipeId;

        LoadRecipeMethodTask(String recipeId) {
            this.mRecipeId = recipeId;
        }

        /** Show progress bar to user to indicate loading */
        @Override
        protected void onPreExecute() {
            mView.showProgress();
        }

        /**
         * Calls model to query for the method of the specified recipe
         * @return results of API call
         */
        @Override
        protected ArrayList<ArrayList<String>> doInBackground(Void... voids) {
            return mModel.queryRecipeInstructions(mRecipeId);
        }

        /**
         * Calls view to display method retrieving results of API call to user
         * @param recipeMethod results of API call
         */
        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> recipeMethod) {
            if (recipeMethod != null) {
                mView.showRecipeInstructions(recipeMethod);
            } else {
                mView.showError();
            }

            mView.hideProgress();
        }
    }
}
