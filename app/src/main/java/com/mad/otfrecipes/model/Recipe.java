/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.model;

import android.graphics.Bitmap;

/** Model for the recipes displayed in the recipe RecyclerView in RecipeListActivity */
public class Recipe {
    private String mRecipeId;
    private String mRecipeName;
    private String mRecipeServings;
    private String mRecipeCookingTime;
    private String mRecipeImageUrl;
    private Bitmap mRecipeImage;

    //Constructor method
    public Recipe(String recipeId, String recipeName, String recipeServings, String recipeCookingTime, String recipeImageUrl) {
        this.mRecipeId = recipeId;
        this.mRecipeName = recipeName;
        this.mRecipeServings = recipeServings;
        this.mRecipeCookingTime = recipeCookingTime;
        this.mRecipeImageUrl = recipeImageUrl;
        this.mRecipeImage = null;
    }

    //Getters and setters
    public String getRecipeId() {
        return mRecipeId;
    }

    public void setRecipeId(String recipeId) {
        this.mRecipeId = recipeId;
    }

    public String getRecipeName() {
        return mRecipeName;
    }

    public void setRecipeName(String recipeName) {
        this.mRecipeName = recipeName;
    }

    public String getRecipeServings() {
        return mRecipeServings;
    }

    public void setRecipeServings(String recipeServings) {
        this.mRecipeServings = recipeServings;
    }

    public String getRecipeCookingTime() {
        return mRecipeCookingTime;
    }

    public void setRecipeCookingTime(String recipeCookingTime) {
        this.mRecipeCookingTime = recipeCookingTime;
    }

    public String getRecipeImageUrl() {
        return mRecipeImageUrl;
    }

    public void setRecipeImageUrl(String recipeImageUrl) {
        this.mRecipeImageUrl = recipeImageUrl;
    }

    public Bitmap getRecipeImage() {
        return mRecipeImage;
    }

    public void setRecipeImage(Bitmap recipeImage) {
        this.mRecipeImage = recipeImage;
    }
}
