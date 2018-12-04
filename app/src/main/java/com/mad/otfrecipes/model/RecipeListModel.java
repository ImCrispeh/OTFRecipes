/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.model;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.mad.otfrecipes.constants.PreferencesConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/** Provides interactions with the API for searching for recipes and returning any results */
public class RecipeListModel {
    private final String RECIPE_SEARCH_URL = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/searchComplex";
    private final String INGREDIENTS_QUERY_PARAM = "includeIngredients";
    private final String NUMBER_QUERY_PARAM = "number";
    private final String RANKING_QUERY_PARAM = "ranking";
    private final String FILL_INGREDIENTS_QUERY_PARAM = "fillIngredients";
    private final String DIET_QUERY_PARAM = "diet";
    private final String RECIPE_INFORMATION_QUERY_PARAM = "addRecipeInformation";
    private final String CALORIE_RESTRICT_QUERY_PARAM = "maxCalories";
    private final String FAT_RESTRICT_QUERY_PARAM = "maxFat";
    private final String SUGAR_RESTRICT_QUERY_PARAM = "maxSugar";
    private final String OFFSET_QUERY_PARAM = "offset";
    private final String TRUE = "true";
    private final String NUMBER_OF_RESULTS = "5";
    private final String KEY_HEADER = "X-Mashape-Key";
    private final String KEY = "Wvy0xo6LMLmshG6ak4YecvHKzuhap1WlveFjsn5I0kVosY52BB";
    private final String HOST_HEADER = "X-Mashape-Host";
    private final String HOST = "spoonacular-recipe-food-nutrition-v1.p.mashape.com";
    private final String RESULTS_ARRAY = "results";
    private final String RECIPE_ID_FIELD = "id";
    private final String RECIPE_TITLE_FIELD = "title";
    private final String RECIPE_SERVINGS_FIELD = "servings";
    private final String RECIPE_COOKING_TIME_FIELD = "readyInMinutes";
    private final String RECIPE_IMAGE_FIELD = "image";
    private final String MISSING_INGREDIENTS_FIELD = "missedIngredientCount";
    private final String COOKING_TIME_UNIT = " mins";
    private PreferencesConstants.IngredientsRestriction mIngredientRestrict;
    private int mIngredientRestrictNumber;
    private boolean mIsIngredientRestricted;
    private PreferencesConstants.NutritionalRestriction mCalorieRestrict;
    private String mCalorieRestrictNumber;
    private PreferencesConstants.NutritionalRestriction mFatRestrict;
    private String mFatRestrictNumber;
    private PreferencesConstants.NutritionalRestriction mSugarRestrict;
    private String mSugarRestrictNumber;
    private String mDiet;

    //Setter methods for the various restrictions/filters to be used in the recipe search
    public void setIngredientRestrict(PreferencesConstants.IngredientsRestriction ingredientRestrict) {
        this.mIngredientRestrict = ingredientRestrict;
    }

    public void setIngredientRestrictNumber(int ingredientRestrictNumber) {
        this.mIngredientRestrictNumber = ingredientRestrictNumber;
    }

    public void setCalorieRestrict(PreferencesConstants.NutritionalRestriction calorieRestrict) {
        this.mCalorieRestrict = calorieRestrict;
    }

    public void setCalorieRestrictNumber(String calorieRestrictNumber) {
        this.mCalorieRestrictNumber = calorieRestrictNumber;
    }

    public void setFatRestrict(PreferencesConstants.NutritionalRestriction fatRestrict) {
        this.mFatRestrict = fatRestrict;
    }

    public void setFatRestrictNumber(String fatRestrictNumber) {
        this.mFatRestrictNumber = fatRestrictNumber;
    }

    public void setSugarRestrict(PreferencesConstants.NutritionalRestriction sugarRestrict) {
        this.mSugarRestrict = sugarRestrict;
    }

    public void setSugarRestrictNumber(String sugarRestrictNumber) {
        this.mSugarRestrictNumber = sugarRestrictNumber;
    }

    public void setDiet(String diet) {
        this.mDiet = diet;
    }

    /** Queries API for any recipes that match the chosen ingredients and any extra restrictions and
     * filters
     * @param ingredientsForFilter ingredients to use in the recipe search
     * @return list of any recipes returned by the search
     */
    public ArrayList<Recipe> queryRecipes(ArrayList<String> ingredientsForFilter) {
        for (String ingredient : ingredientsForFilter) {
            Log.d("test", ingredient);
        }

        String ingredientsForParam = "";
        int offset = 0;
        if (ingredientsForFilter.size() > 0) {
            ingredientsForParam = ingredientsForFilter.get(0);
            for (int i = 1; i < ingredientsForFilter.size(); i++) {
                ingredientsForParam = ingredientsForParam.concat(",").concat(ingredientsForFilter.get(i));
            }
        }

        StringBuilder result = sendQuery(ingredientsForParam, offset);

        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        extractRecipes(recipes, result);

        int numberOfExtraSearches = 0;
        while (recipes.size() < Integer.parseInt(NUMBER_OF_RESULTS) && numberOfExtraSearches < 3) {
            offset += Integer.parseInt(NUMBER_OF_RESULTS);
            result = sendQuery(ingredientsForParam, offset);
            extractRecipes(recipes, result);
            numberOfExtraSearches++;
        }

        retrieveRecipeBitmaps(recipes);
        return recipes;

    }

    /**
     * Extracts information to be added to the Recipe objects from the API's JSON response
     * @param recipes list to add the created Recipe objects to
     * @param result StringBuiler that contains the returned results of the API call
     */
    private void extractRecipes(ArrayList<Recipe> recipes, StringBuilder result) {
        try {
            JSONObject baseObj = new JSONObject(result.toString());
            JSONArray resultsArray = baseObj.getJSONArray(RESULTS_ARRAY);
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject recipeObj = resultsArray.getJSONObject(i);
                String id = recipeObj.getString(RECIPE_ID_FIELD);
                String name = recipeObj.getString(RECIPE_TITLE_FIELD);
                String servings = recipeObj.getString(RECIPE_SERVINGS_FIELD);
                String cookingTime = recipeObj.getString(RECIPE_COOKING_TIME_FIELD) + COOKING_TIME_UNIT;
                String imageUrl = recipeObj.getString(RECIPE_IMAGE_FIELD);

                if (recipes.size() < Integer.parseInt(NUMBER_OF_RESULTS)) {
                    Log.d("test", "recipe to be added");
                    if (mIsIngredientRestricted && Integer.parseInt(recipeObj.getString(MISSING_INGREDIENTS_FIELD)) <= mIngredientRestrictNumber) {
                        recipes.add(new Recipe(id, name, servings, cookingTime, imageUrl));
                        Log.d("test", "adding recipe (restricted)");
                    } else if (!mIsIngredientRestricted) {
                        recipes.add(new Recipe(id, name, servings, cookingTime, imageUrl));
                        Log.d("test", "adding recipe (not restricted)");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds and sends request to API that searches for any recipes matching the set restrictions
     * and filters
     * @param ingredientsForParam any ingredients to use for the ingredient search paramater
     * @param offset how many recipes to skip based on if multiple searches should be made
     * @return result of the API call
     */
    private StringBuilder sendQuery(String ingredientsForParam, int offset) {
        StringBuilder result = new StringBuilder();
        try {
            Uri.Builder uriBuilder = Uri.parse(RECIPE_SEARCH_URL)
                    .buildUpon()
                    .appendQueryParameter(RECIPE_INFORMATION_QUERY_PARAM, TRUE)
                    .appendQueryParameter(FILL_INGREDIENTS_QUERY_PARAM, TRUE)
                    .appendQueryParameter(INGREDIENTS_QUERY_PARAM, ingredientsForParam)
                    .appendQueryParameter(NUMBER_QUERY_PARAM, NUMBER_OF_RESULTS)
                    .appendQueryParameter(OFFSET_QUERY_PARAM, Integer.toString(offset));

            uriBuilder = setAnyRestrictions(uriBuilder);
            Uri uri = uriBuilder.build();

            Log.d("test", uri.toString());
            URL url = new URL(uri.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty(KEY_HEADER, KEY);
            urlConnection.setRequestProperty(HOST_HEADER, HOST);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Gets the images from the image URLs from the recipes returned by the API call and converts
     * them into bitmaps before setting them as part of the Recipe objects
     * @param recipes recipes returned from the API call
     */
    private void retrieveRecipeBitmaps(ArrayList<Recipe> recipes) {
        URL url;

        for (int i = 0; i < recipes.size(); i++) {
            try {
                url = new URL(recipes.get(i).getRecipeImageUrl());
                recipes.get(i).setRecipeImage(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
            } catch (MalformedURLException e) {
                Log.d("imageLoad", "failed to load image");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("imageLoad", "failed to load image");
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds any extra parameters to the request used for the API call based on user settings
     * @param uriBuilder base request for the API call that parameters are to be added to
     * @return modified request for API call
     */
    private Uri.Builder setAnyRestrictions(Uri.Builder uriBuilder) {
        uriBuilder = setIngredientRestrictionQuery(uriBuilder);
        uriBuilder = setDietRestrictionQuery(uriBuilder);
        uriBuilder = setCalorieRestrictionQuery(uriBuilder);
        uriBuilder = setFatRestrictionQuery(uriBuilder);
        uriBuilder = setSugarRestrictionQuery(uriBuilder);

        return uriBuilder;
    }

    /**
     * Adds any ingredient restriction parameter to the request used for the API call
     * and to trigger a custom restriction based on user settings
     * @param uriBuilder base request for the API call that parameters are to be added to
     * @return modified request for API call
     */
    private Uri.Builder setIngredientRestrictionQuery(Uri.Builder uriBuilder) {
        Log.d("test", mIngredientRestrict.toString());
        switch (mIngredientRestrict) {
            case COMPLETE_RESTRICTION:
                Log.d("test", "complete restriction detected");
                uriBuilder.appendQueryParameter(RANKING_QUERY_PARAM, "0");
                mIsIngredientRestricted = true;
                break;
            case ALLOW_OTHER_INGREDIENTS:
                Log.d("test", "partial restriction detected");
                uriBuilder.appendQueryParameter(RANKING_QUERY_PARAM, "0");
                mIsIngredientRestricted = true;
                break;
            case NO_RESTRICTION:
                Log.d("test", "no restriction detected");
                mIsIngredientRestricted = false;
                break;
            default:
                Log.d("test", "default restriction detected");
                mIsIngredientRestricted = false;
                break;

        }

        Log.d("test", Boolean.toString(mIsIngredientRestricted));
        Log.d("test", uriBuilder.toString());

        return uriBuilder;
    }

    private Uri.Builder setDietRestrictionQuery(Uri.Builder uriBuilder) {
        if (!mDiet.equalsIgnoreCase(PreferencesConstants.DEFAULT_DIET)) {
            uriBuilder.appendQueryParameter(DIET_QUERY_PARAM, mDiet);
        }

        return uriBuilder;
    }

    /**
     * Adds any calorie restriction parameter to the request used for the API call based on user settings
     * @param uriBuilder base request for the API call that parameters are to be added to
     * @return modified request for API call
     */
    private Uri.Builder setCalorieRestrictionQuery(Uri.Builder uriBuilder) {
        switch (mCalorieRestrict) {
            case RESTRICTION:
                uriBuilder.appendQueryParameter(CALORIE_RESTRICT_QUERY_PARAM, mCalorieRestrictNumber);
                break;
            case NO_RESTRICTION:
                break;
            default:
                break;
        }

        return uriBuilder;
    }

    /**
     * Adds any fat restriction parameter to the request used for the API call based on user settings
     * @param uriBuilder base request for the API call that parameters are to be added to
     * @return modified request for API call
     */
    private Uri.Builder setFatRestrictionQuery(Uri.Builder uriBuilder) {
        switch (mFatRestrict) {
            case RESTRICTION:
                uriBuilder.appendQueryParameter(FAT_RESTRICT_QUERY_PARAM, mFatRestrictNumber);
                break;
            case NO_RESTRICTION:
                break;
            default:
                break;
        }

        return uriBuilder;
    }

    /**
     * Adds any sugar restriction parameter to the request used for the API call based on user settings
     * @param uriBuilder base request for the API call that parameters are to be added to
     * @return modified request for API call
     */
    private Uri.Builder setSugarRestrictionQuery(Uri.Builder uriBuilder) {
        switch (mSugarRestrict) {
            case RESTRICTION:
                uriBuilder.appendQueryParameter(SUGAR_RESTRICT_QUERY_PARAM, mSugarRestrictNumber);
                break;
            case NO_RESTRICTION:
                break;
            default:
                break;
        }

        return uriBuilder;
    }
}
