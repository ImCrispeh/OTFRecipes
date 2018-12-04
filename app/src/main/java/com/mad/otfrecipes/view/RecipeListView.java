/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.view;

import com.mad.otfrecipes.model.Recipe;

import java.util.ArrayList;

/** View for MVP, holds interface methods referenced by presenter for RecipeListActivity */
public interface RecipeListView {
    void activateSpeech();

    void showProgress();

    void hideProgress();

    void showRecipes(ArrayList<Recipe> recipes);

    void viewRecipe(Recipe recipe);

    void showVoiceHint();

    void navigateUp();
}
