/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.presenter;

import com.mad.otfrecipes.constants.PreferencesConstants;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.view.MainView;

/** Provides functionality for handling user interaction in the MainActivity */
public class MainPresenter {
    private MainView mView;

    //Uses SharedPreferencesHelperImpl since there is no need for a MainModel and default preferences should be
    //set when the application is started
    private SharedPreferencesHelperImpl mSharedPrefsHelper;

    //Constructor method
    public MainPresenter(MainView mView, SharedPreferencesHelperImpl sharedPrefsHelper) {
        this.mView = mView;
        this.mSharedPrefsHelper = sharedPrefsHelper;
    }

    /** Enables speech recognition in Activity if it is set as on */
    public void setSpeechRecognition() {
        if (mSharedPrefsHelper.getSpeechStatus() == PreferencesConstants.SpeechRecognition.SPEECH_ON) {
            mView.activateSpeech();
        }
    }

    //Basic button handlers that only call view methods
    public void addButtonClicked() {
        mView.navigateToAddIngredients();
    }

    public void settingsButtonClicked() {
        mView.navigateToSettings();
    }

    public void searchButtonClicked() {
        mView.searchForRecipes();
    }

    public void ingredientDeleteButtonClicked(int pos) {
        mView.deleteIngredient(pos);
    }

    public void clearButtonClicked() {
        mView.clearIngredients();
    }

    //Basic voice handler that only call a view method
    public void hintRequested() {
        mView.showVoiceHint();
    }

    public void closeRequested() {
        mView.closeApp();
    }

    /** Sets the default values for settings */
    public void setInitialSettings() {
        mSharedPrefsHelper.setDefaultPreferences();
    }
}
