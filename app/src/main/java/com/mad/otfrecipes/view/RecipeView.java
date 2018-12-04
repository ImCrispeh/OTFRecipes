/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.view;

import java.util.ArrayList;

/** View for MVP, holds interface methods referenced by presenter for RecipeActivity */
public interface RecipeView {
    void activateSpeech();

    void showProgress();

    void hideProgress();

    void showRecipeInstructions(ArrayList<ArrayList<String>> instructions);

    void showRecipeIngredients(ArrayList<String> ingredients);

    void showRecipeNutrients(ArrayList<String> nutrients);

    void navigateToSource();

    void setUrl(String recipeUrl);

    void readIngredientText(int pos);

    void readMethodText(int pos);

    void readNutrientText(int pos);

    void showVoiceHint();

    void showError();

    void navigateUp();
}
