/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.model;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/** Provides interactions with the API for searching for ingredients and returning any results */
public class AddIngredientsModel {
    private final String INGREDIENTS_SEARCH_URL = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/food/ingredients/autocomplete";
    private final String INGREDIENT_QUERY_PARAM = "query";
    private final String NUMBER_QUERY_PARAM = "number";
    private final String INTOLERANCES_QUERY_PARAM = "intolerances";
    private final String NUMBER_OF_RESULTS = "10";
    private final String KEY_HEADER = "X-Mashape-Key";
    private final String KEY = "Wvy0xo6LMLmshG6ak4YecvHKzuhap1WlveFjsn5I0kVosY52BB";
    private final String HOST_HEADER = "X-Mashape-Host";
    private final String HOST = "spoonacular-recipe-food-nutrition-v1.p.mashape.com";
    private final String INGREDIENT_NAME_FIELD = "name";


    /**
     * Queries the API to search for ingredients based on an autocompletion of given text
     * @param ingredientToSearch text to autocomplete when searching for ingredients
     * @return any ingredients returned by the API search
     */
    public String[] queryIngredients(String ingredientToSearch) {
        StringBuilder result = new StringBuilder();
        try {
            Uri uri = Uri.parse(INGREDIENTS_SEARCH_URL)
                    .buildUpon()
                    .appendQueryParameter(INGREDIENT_QUERY_PARAM, ingredientToSearch)
                    .appendQueryParameter(NUMBER_QUERY_PARAM, NUMBER_OF_RESULTS)
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
            JSONArray ingredientsArray = new JSONArray(result.toString());
            String[] ingredients = new String[ingredientsArray.length()];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = ingredientsArray.getJSONObject(i).getString(INGREDIENT_NAME_FIELD);
                Log.d("test", ingredients[i]);
            }
            return ingredients;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new String[0];
    }

}
