/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mad.otfrecipes.R;
import com.mad.otfrecipes.constants.ExtrasConstants;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.presenter.RecipeWebPresenter;
import com.mad.otfrecipes.speech.SpeechActivator;
import com.mad.otfrecipes.speech.SpeechProcessor;
import com.mad.otfrecipes.view.RecipeWebView;

import java.util.Random;

/** Opens up the passed in recipe source URL in a WebView to display to the user */
public class RecipeWebActivity extends AppCompatActivity implements RecipeWebView, SpeechProcessor {
    private WebView mRecipeWebView;
    private RecipeWebPresenter mPresenter;
    private SpeechActivator mSpeechActivator;
    private String[] mVoiceHints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_web);

        mPresenter = new RecipeWebPresenter(this, new SharedPreferencesHelperImpl(this.getApplicationContext()));
        mRecipeWebView = findViewById(R.id.web_view_recipe);

        //Load URL passed in from RecipeActivity into the WebView
        String recipeUrl = getIntent().getExtras().getString(ExtrasConstants.RECIPE_URL);
        mRecipeWebView.setWebViewClient(new WebViewClient());
        mRecipeWebView.loadUrl(recipeUrl);

        mVoiceHints = getResources().getStringArray(R.array.recipe_web_voice_hints);

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

    @Override
    protected void onPause() {
        if (mSpeechActivator != null) {
            mSpeechActivator.stopListeningForSpeech();
        }
        super.onPause();
    }

    /** Navigates to parent activity (RecipeActivity) */
    @Override
    public void navigateUp() {
        NavUtils.navigateUpFromSameTask(this);
    }
}
