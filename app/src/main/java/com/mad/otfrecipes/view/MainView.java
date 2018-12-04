/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.view;

/** View for MVP, holds interface methods referenced by presenter for MainActivity */
public interface MainView {
    void activateSpeech();

    void navigateToAddIngredients();

    void navigateToSettings();

    void searchForRecipes();

    void deleteIngredient(int pos);

    void clearIngredients();

    void showVoiceHint();

    void closeApp();
}
