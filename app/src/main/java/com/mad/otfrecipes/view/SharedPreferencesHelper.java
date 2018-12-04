/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.view;

import com.mad.otfrecipes.constants.PreferencesConstants;

/** Holds interface methods that are referenced by presenters that need to access SharedPreferences */
public interface SharedPreferencesHelper {

    void setDefaultPreferences();

    void savePreferences(String ingredientRestrict, int ingredientNumber, String calorieRestrict, String calorieNumber,
                         String speechStatus, String fatRestrict, String fatNumber, String sugarRestrict,
                         String sugarNumber, String diet);

    void setSpeechSettings(boolean isEnabled);

    PreferencesConstants.IngredientsRestriction getIngredientRestriction();

    int getIngredientNumber();

    PreferencesConstants.NutritionalRestriction getCalorieRestriction();

    String getCalorieNumber();

    PreferencesConstants.NutritionalRestriction getFatRestriction();

    String getFatNumber();

    PreferencesConstants.NutritionalRestriction getSugarRestriction();

    String getSugarNumber();

    PreferencesConstants.SpeechRecognition getSpeechStatus();

    String getDiet();
}
