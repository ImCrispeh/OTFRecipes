/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.view;

import android.view.View;

/** View for MVP, holds interface methods referenced by presenter for AddIngredientsActivity */
public interface AddIngredientsView {
    void activateSpeech();

    void loseSearchFocus();

    void closeSearchResults(View v);

    void showIngredientResultsProgress();

    void hideIngredientResultsProgress();

    void showIngredientResults(String[] foundIngredients);

    void addIngredient(int pos);

    void deleteIngredient(int pos);

    void saveIngredients();

    void clearIngredients();

    void showVoiceHint();

    void navigateUp();
}
