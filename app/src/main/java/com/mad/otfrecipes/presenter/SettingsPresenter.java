/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.presenter;

import com.mad.otfrecipes.constants.PreferencesConstants;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.view.SettingsView;

/** Provides functionality for handling user interaction in the SettingsActivity */
public class SettingsPresenter {
    private SettingsView mView;

    //This is essentially the model for the settings
    private SharedPreferencesHelperImpl mSharedPrefsHelper;

    //Constructor method
    public SettingsPresenter(SettingsView view, SharedPreferencesHelperImpl sharedPrefsHelper) {
        this.mView = view;
        this.mSharedPrefsHelper = sharedPrefsHelper;
    }

    /**
     * Passes the status of speech recognition to the shared preferences
     * @param isEnabled status of speech recognition
     */
    public void enableSpeechSettings(boolean isEnabled) {
        mSharedPrefsHelper.setSpeechSettings(isEnabled);
    }

    /** Enables speech recognition in Activity if it is set as on */
    public void setSpeechRecognition() {
        if (mSharedPrefsHelper.getSpeechStatus() == PreferencesConstants.SpeechRecognition.SPEECH_ON) {
            mView.activateSpeech();
        }
    }

    /**
     * Retrieves the user settings that are saved in SharedPreferences and passes them in a call to
     * the view to display them to the user
     */
    public void setUpSelected() {
        PreferencesConstants.IngredientsRestriction ingredientRestrict;
        int ingredientNumber;
        PreferencesConstants.NutritionalRestriction calorieRestrict;
        String calorieNumber;
        PreferencesConstants.NutritionalRestriction fatRestrict;
        String fatNumber;
        PreferencesConstants.NutritionalRestriction sugarRestrict;
        String sugarNumber;
        PreferencesConstants.SpeechRecognition speechStatus;
        String diet;

        ingredientRestrict = mSharedPrefsHelper.getIngredientRestriction();
        ingredientNumber = mSharedPrefsHelper.getIngredientNumber();
        calorieRestrict = mSharedPrefsHelper.getCalorieRestriction();
        calorieNumber = mSharedPrefsHelper.getCalorieNumber();
        fatRestrict = mSharedPrefsHelper.getFatRestriction();
        fatNumber = mSharedPrefsHelper.getFatNumber();
        sugarRestrict = mSharedPrefsHelper.getSugarRestriction();
        sugarNumber = mSharedPrefsHelper.getSugarNumber();
        speechStatus = mSharedPrefsHelper.getSpeechStatus();
        diet = mSharedPrefsHelper.getDiet();

        mView.setSettingSelections(ingredientRestrict, ingredientNumber, calorieRestrict, calorieNumber,
                fatRestrict, fatNumber, sugarRestrict, sugarNumber, speechStatus, diet);
    }

    /**
     * Calls the SharedPreferencesHelper to save the various user settings to SharedPreferences
     * The various parameters are the restriction types and respective values to saved to SharedPreferences
     * @param ingredientRestrict type of restriction for recipe search based on chosen ingredients
     * @param ingredientNumber maximum number of extra ingredients to allow when performing a recipe search
     * @param calorieRestrict type of restriction for calories
     * @param calorieNumber maximum number of calories for restriction
     * @param fatRestrict type of restriction for fat
     * @param fatNumber maximum number of fat for restriction
     * @param sugarRestrict type of restriction for sugar
     * @param sugarNumber maximum number of sugar for restriction
     * @param speechStatus whether speech is enabled or disabled
     * @param diet type of diet to restrict recipe searches to
     */
    public void saveButtonClicked(String ingredientRestrict, int ingredientNumber, String calorieRestrict,
                                  String calorieNumber, String fatRestrict, String fatNumber,
                                  String sugarRestrict, String sugarNumber, String speechStatus, String diet) {
        mSharedPrefsHelper.savePreferences(ingredientRestrict, ingredientNumber, calorieRestrict, calorieNumber,
                fatRestrict, fatNumber, sugarRestrict, sugarNumber, speechStatus, diet);
    }

    //Basic button/voice handlers that only call view methods
    public void resetButtonClicked() {
        mView.resetSettings();
    }

    public void hintRequested() {
        mView.showVoiceHint();
    }

    public void upButtonClicked() {
        mView.navigateUp();
    }
}
