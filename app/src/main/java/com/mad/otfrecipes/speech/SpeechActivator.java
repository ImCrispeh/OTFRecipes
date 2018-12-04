/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.speech;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mad.otfrecipes.activity.SettingsActivity;
import com.mad.otfrecipes.constants.PermissionsConstants;

import java.util.ArrayList;

/**
 * Provides the functionality for initialising the SpeechRecognizer (including asking for permissions),
 * calling it to start and stop listening, and calling the activity (which implement SpeechProcessor)
 * it was created in to process the results of the speech recognition
 */
public class SpeechActivator {
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;

    private Context mContext;
    private AppCompatActivity mActivity;
    private SpeechProcessor mSpeechProcessor;
    private boolean mIsListening;
    private boolean isAvailable;

    /**
     * Constructor method, also derives context from passed in activity
     *
     * @param activity        activity that is creating the SpeechActivatorObject
     * @param speechProcessor class that implements the SpeechProcessor interface
     */
    public SpeechActivator(AppCompatActivity activity, SpeechProcessor speechProcessor) {
        this.mActivity = activity;
        this.mContext = mActivity.getApplicationContext();
        this.mSpeechProcessor = speechProcessor;
        mSpeechRecognizer = null;
        mIsListening = true;
        isAvailable = SpeechRecognizer.isRecognitionAvailable(mContext);
    }

    //Getter methods
    public boolean getIsListening() {
        return mIsListening;
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    /** Requests audio recording permissions */
    public void RequestAudioPermissions() {
        Log.d("test", "permissions asked");
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.RECORD_AUDIO}, PermissionsConstants.RECORD_AUDIO_CODE);
        } else {
            activateSpeech();
            if (mContext instanceof SettingsActivity) {
                ((SettingsActivity)mContext).showInitialHint();
            }
        }
    }

    /** Sets up SpeechRecognizer object */
    public void activateSpeech() {
        if (mIsListening) {
            if (isAvailable) {
                mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
                mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle params) {
                        Log.d("test", "speech ready");
                    }

                    @Override
                    public void onBeginningOfSpeech() {

                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {

                    }

                    @Override
                    public void onBufferReceived(byte[] buffer) {

                    }

                    @Override
                    public void onEndOfSpeech() {

                    }

                    @Override
                    public void onError(int error) {
                    }

                    @Override
                    public void onResults(Bundle results) {
                        ArrayList<String> speechResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        if (speechResults != null) {
                            if (speechResults.size() > 0) {
                                mSpeechProcessor.processSpeechResults(speechResults.get(0));
                                Log.d("test", speechResults.get(0));
                            }
                        }
                    }

                    @Override
                    public void onPartialResults(Bundle partialResults) {

                    }

                    @Override
                    public void onEvent(int eventType, Bundle params) {

                    }
                });

                if (mIsListening) {
                    startListeningForSpeech();
                }
            }
        }
    }

    /**
     * Sets up the corresponding intent for the speechRecognizer object and calls it to start
     * listening for speech
     */
    public void startListeningForSpeech() {
        mIsListening = true;
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mActivity.getApplication().getPackageName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        Log.d("test", mActivity.getClass().toString() + ": start listening");
    }

    /** Calls for the speechRecognizer object to stop listening and destroys it */
    public void stopListeningForSpeech() {
        Log.d("test", mActivity.getClass().toString() + ": stop listening");
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }
        mIsListening = false;
    }

    /** Calls for the speechRecognizer object to stop listening */
    public void pauseListening() {
        mSpeechRecognizer.stopListening();
    }


}
