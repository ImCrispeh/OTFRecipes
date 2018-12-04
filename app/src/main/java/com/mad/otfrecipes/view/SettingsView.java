/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.view;

import com.mad.otfrecipes.constants.PreferencesConstants;

/** View for MVP, holds interface methods referenced by presenter for SettingsActivity */
public interface SettingsView {
    void activateSpeech();

    void setSettingSelections(PreferencesConstants.IngredientsRestriction ingredientRestrict, int ingredientNumber,
                              PreferencesConstants.NutritionalRestriction calorieRestrict, String calorieNumber,
                              PreferencesConstants.NutritionalRestriction fatRestrict, String fatNumber,
                              PreferencesConstants.NutritionalRestriction sugarRestrict, String sugarNumber,
                              PreferencesConstants.SpeechRecognition speechStatus, String diet);

    void resetSettings();

    void showVoiceHint();

    void navigateUp();
}
