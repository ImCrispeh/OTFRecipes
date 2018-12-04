/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.view;

/** View for MVP, holds interface methods referenced by presenter for RecipeWebActivity */
public interface RecipeWebView {
    void activateSpeech();

    void showVoiceHint();

    void navigateUp();
}
