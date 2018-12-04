/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.constants;

/**
 * Provides constants and enums for different states to use for the different values for settings in
 * SharedPreferences
 */
public class PreferencesConstants {
    public final static String DEFAULT_RESTRICTION_NUMBER = "0";
    public enum IngredientsRestriction { COMPLETE_RESTRICTION, ALLOW_OTHER_INGREDIENTS, NO_RESTRICTION };
    public enum NutritionalRestriction {RESTRICTION, NO_RESTRICTION };
    public enum SpeechRecognition { SPEECH_ON, SPEECH_OFF };
    public final static String DEFAULT_DIET = "none";
}
