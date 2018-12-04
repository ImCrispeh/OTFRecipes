/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.presenter;

import com.mad.otfrecipes.constants.PreferencesConstants;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.view.RecipeWebView;

/** Provides functionality for handling user interaction in the RecipeWebActivity */
public class RecipeWebPresenter {
    private RecipeWebView mView;
    private SharedPreferencesHelperImpl mSharedPrefsHelper;

    //Constructor method
    public RecipeWebPresenter(RecipeWebView mView, SharedPreferencesHelperImpl sharedPrefsHelper) {
        this.mView = mView;
        this.mSharedPrefsHelper = sharedPrefsHelper;
    }

    /** Enables speech recognition in Activity if it is set as on */
    public void setSpeechRecognition() {
        if (mSharedPrefsHelper.getSpeechStatus() == PreferencesConstants.SpeechRecognition.SPEECH_ON) {
            mView.activateSpeech();
        }
    }

    //Basic button/voice handlers that only call view methods
    public void hintRequested() {
        mView.showVoiceHint();
    }

    public void upButtonClicked() {
        mView.navigateUp();
    }
}
