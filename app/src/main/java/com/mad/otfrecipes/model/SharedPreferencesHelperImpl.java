/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mad.otfrecipes.constants.PreferencesConstants;
import com.mad.otfrecipes.view.SharedPreferencesHelper;

/** Provides the ability to set and get SharedPreferences */
public class SharedPreferencesHelperImpl implements SharedPreferencesHelper {

    //Key constants
    private static final String INGREDIENT_RESTRICTION = "INGREDIENT_RESTRICTION";
    private static final String INGREDIENT_RESTRICTION_NUMBER = "INGREDIENT_RESTRICTION_NUMBER";
    private static final String CALORIE_RESTRICTION = "CALORIE_RESTRICTION";
    private static final String CALORIE_RESTRICTION_NUMBER = "CALORIE_RESTRICTION_NUMBER";
    private static final String FAT_RESTRICTION = "FAT_RESTRICTION";
    private static final String FAT_RESTRICTION_NUMBER = "FAT_RESTRICTION_NUMBER";
    private static final String SUGAR_RESTRICTION = "SUGAR_RESTRICTION";
    private static final String SUGAR_RESTRICTION_NUMBER = "SUGAR_RESTRICTION_NUMBER";
    private static final String SPEECH_RECOGNITION_STATUS = "SPEECH_RECOGNITION_STATUS";
    private static final String DIET = "DIET";
    private static final String INITIAL_SETTINGS_DONE = "INITIAL_SETTINGS_DONE";

    private SharedPreferences mSharedPrefs;

    public SharedPreferencesHelperImpl(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void setSpeechSettings(boolean isEnabled) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        if (isEnabled) {
            editor.putString(SPEECH_RECOGNITION_STATUS, PreferencesConstants.SpeechRecognition.SPEECH_ON.toString());
        } else {
            editor.putString(SPEECH_RECOGNITION_STATUS, PreferencesConstants.SpeechRecognition.SPEECH_OFF.toString());
        }
        editor.apply();
    }

    /** Sets the default state of SharedPreferences if they have not already been set */
    @Override
    public void setDefaultPreferences() {
        if (!mSharedPrefs.contains(INITIAL_SETTINGS_DONE)) {
            SharedPreferences.Editor editor = mSharedPrefs.edit();
            editor.putBoolean(INITIAL_SETTINGS_DONE, true);
            editor.putString(INGREDIENT_RESTRICTION, PreferencesConstants.IngredientsRestriction.NO_RESTRICTION.toString());
            editor.putString(CALORIE_RESTRICTION, PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString());
            editor.putString(FAT_RESTRICTION, PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString());
            editor.putString(SUGAR_RESTRICTION, PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString());
            editor.putString(SPEECH_RECOGNITION_STATUS, PreferencesConstants.SpeechRecognition.SPEECH_OFF.toString());
            editor.putString(DIET, PreferencesConstants.DEFAULT_DIET);
            editor.apply();
        }
    }

    /** Saves passed in values of settings to respective SharedPreference */
    @Override
    public void savePreferences(String ingredientRestrict, int ingredientNumber, String calorieRestrict,
                                String calorieNumber, String fatRestrict, String fatNumber,
                                String sugarRestrict, String sugarNumber, String speechStatus, String diet) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString(INGREDIENT_RESTRICTION, ingredientRestrict);
        editor.putInt(INGREDIENT_RESTRICTION_NUMBER, ingredientNumber);
        editor.putString(CALORIE_RESTRICTION, calorieRestrict);
        editor.putString(CALORIE_RESTRICTION_NUMBER, calorieNumber);
        editor.putString(FAT_RESTRICTION, fatRestrict);
        editor.putString(FAT_RESTRICTION_NUMBER, fatNumber);
        editor.putString(SUGAR_RESTRICTION, sugarRestrict);
        editor.putString(SUGAR_RESTRICTION_NUMBER, sugarNumber);
        editor.putString(SPEECH_RECOGNITION_STATUS, speechStatus);
        editor.putString(DIET, diet);
        editor.apply();
    }

    //Getters methods for SharedPreferences
    @Override
    public PreferencesConstants.IngredientsRestriction getIngredientRestriction() {
        String ingredientRestrict = mSharedPrefs.getString(INGREDIENT_RESTRICTION, PreferencesConstants.IngredientsRestriction.NO_RESTRICTION.toString());
        return PreferencesConstants.IngredientsRestriction.valueOf(ingredientRestrict);
    }

    @Override
    public int getIngredientNumber() {
        return mSharedPrefs.getInt(INGREDIENT_RESTRICTION_NUMBER, Integer.parseInt(PreferencesConstants.DEFAULT_RESTRICTION_NUMBER));
    }

    @Override
    public PreferencesConstants.NutritionalRestriction getCalorieRestriction() {
        String calorieRestrict = mSharedPrefs.getString(CALORIE_RESTRICTION, PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString());
        return PreferencesConstants.NutritionalRestriction.valueOf(calorieRestrict);
    }

    @Override
    public String getCalorieNumber() {
        return mSharedPrefs.getString(CALORIE_RESTRICTION_NUMBER, PreferencesConstants.DEFAULT_RESTRICTION_NUMBER);
    }

    @Override
    public PreferencesConstants.NutritionalRestriction getFatRestriction() {
        String fatRestrict = mSharedPrefs.getString(FAT_RESTRICTION, PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString());
        return PreferencesConstants.NutritionalRestriction.valueOf(fatRestrict);
    }

    @Override
    public String getFatNumber() {
        return mSharedPrefs.getString(FAT_RESTRICTION_NUMBER, PreferencesConstants.DEFAULT_RESTRICTION_NUMBER);
    }

    @Override
    public PreferencesConstants.NutritionalRestriction getSugarRestriction() {
        String sugarRestrict = mSharedPrefs.getString(SUGAR_RESTRICTION, PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString());
        return PreferencesConstants.NutritionalRestriction.valueOf(sugarRestrict);
    }

    @Override
    public String getSugarNumber() {
        return mSharedPrefs.getString(SUGAR_RESTRICTION_NUMBER, PreferencesConstants.DEFAULT_RESTRICTION_NUMBER);
    }

    @Override
    public PreferencesConstants.SpeechRecognition getSpeechStatus() {
        String speechStatus = mSharedPrefs.getString(SPEECH_RECOGNITION_STATUS, PreferencesConstants.SpeechRecognition.SPEECH_OFF.toString());
        return PreferencesConstants.SpeechRecognition.valueOf(speechStatus);
    }

    @Override
    public String getDiet() {
        return mSharedPrefs.getString(DIET, PreferencesConstants.DEFAULT_DIET);
    }
}
