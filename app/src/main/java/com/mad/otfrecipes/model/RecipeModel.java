/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.model;

import android.net.Uri;
import android.util.Log;

import com.mad.otfrecipes.BuildConfig;

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

/** Provides interactions with the API for retrieving information about a specific recipe */
public class RecipeModel {
    private final String RECIPE_SEARCH_URL = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes";
    private final String INFORMATION_PATH = "information";
    private final String INSTRUCTIONS_PATH = "analyzedInstructions";
    private final String KEY_HEADER = "X-Mashape-Key";
    private final String KEY = BuildConfig.SPOONACULAR_API_KEY;
    private final String HOST_HEADER = "X-Mashape-Host";
    private final String HOST = "spoonacular-recipe-food-nutrition-v1.p.mashape.com";
    private final String INCLUDE_NUTRITION_PARAM = "includeNutrition";
    private final String URL_FIELD = "sourceUrl";
    private final String INGREDIENTS_FIELD = "extendedIngredients";
    private final String INGREDIENT_FIELD = "originalString";
    private final String NAME_FIELD = "name";
    private final String STEPS_FIELD = "steps";
    private final String NUMBER_FIELD = "number";
    private final String STEP_FIELD = "step";
    private final String NUMBER_STEP_SPLITTER = ". ";
    private final String NUTRITION_FIELD = "nutrition";
    private final String NUTRIENTS_FIELD = "nutrients";
    private final String TITLE_FIELD = "title";
    private final String AMOUNT_FIELD = "amount";
    private final String UNIT_FIELD = "unit";

    public ArrayList<ArrayList<String>> queryRecipeUrlAndIngredients(String recipeId) {
        StringBuilder result = new StringBuilder();

        try {
            Uri uri = Uri.parse(RECIPE_SEARCH_URL)
                    .buildUpon()
                    .appendPath(recipeId)
                    .appendPath(INFORMATION_PATH)
                    .appendQueryParameter(INCLUDE_NUTRITION_PARAM, "true")
                    .build();
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

        try {
            JSONObject obj = new JSONObject(result.toString());
            ArrayList<ArrayList<String>> recipeInfo = new ArrayList<ArrayList<String>>();
            ArrayList<String> urlIngredients = new ArrayList<String>();
            //Add url first
            String recipeUrlString = obj.getString(URL_FIELD);
            urlIngredients.add(recipeUrlString);

            //Iterate through ingredients and add to list
            JSONArray ingredientsArray = obj.getJSONArray(INGREDIENTS_FIELD);
            for (int i = 0; i < ingredientsArray.length(); i++) {
                JSONObject ingredientObj = ingredientsArray.getJSONObject(i);
                urlIngredients.add(ingredientObj.getString(INGREDIENT_FIELD));
                Log.d("test", urlIngredients.toString());
            }
            recipeInfo.add(urlIngredients);

            ArrayList<String> nutrients = new ArrayList<String>();
            JSONArray nutrientsArray = obj.getJSONObject(NUTRITION_FIELD).getJSONArray(NUTRIENTS_FIELD);
            for (int i = 0; i < nutrientsArray.length(); i++) {
                JSONObject nutrient = nutrientsArray.getJSONObject(i);
                String nutrientString = nutrient.getString(TITLE_FIELD) + ": " + nutrient.getInt(AMOUNT_FIELD) + " " + nutrient.getString(UNIT_FIELD);
                nutrients.add(nutrientString);
            }
            recipeInfo.add(nutrients);

            return recipeInfo;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<ArrayList<String>> queryRecipeInstructions(String recipeId) {
        StringBuilder result = new StringBuilder();

        try {
            Uri uri = Uri.parse(RECIPE_SEARCH_URL)
                    .buildUpon()
                    .appendPath(recipeId)
                    .appendPath(INSTRUCTIONS_PATH)
                    .build();
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

        try {
            Log.d("test", result.toString());
            JSONArray baseArray = new JSONArray(result.toString());
            ArrayList<ArrayList<String>> allRecipeInstructions = new ArrayList<ArrayList<String>>();

            for (int i = 0; i < baseArray.length(); i++) {
                allRecipeInstructions.add(new ArrayList<String>());
                JSONObject baseInstructionsObj = baseArray.getJSONObject(i);
                Log.d("test", baseInstructionsObj.toString());

                //Set first thing to be the item that the instructions are for
                allRecipeInstructions.get(i).add(baseInstructionsObj.getString(NAME_FIELD));
                JSONArray stepsArray = baseInstructionsObj.getJSONArray(STEPS_FIELD);

                //Iterate through instructions and add them after formatting into "<step number>. <instruction>"
                for (int j = 0; j < stepsArray.length(); j++) {
                    JSONObject stepObj = stepsArray.getJSONObject(j);
                    String number = stepObj.getString(NUMBER_FIELD);
                    String step = stepObj.getString(STEP_FIELD);

                    String instruction = number + NUMBER_STEP_SPLITTER + step;
                    Log.d("test", instruction);
                    allRecipeInstructions.get(i).add(instruction);
                }
                Log.d("test", String.valueOf(allRecipeInstructions.size()));
            }

            return allRecipeInstructions;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
