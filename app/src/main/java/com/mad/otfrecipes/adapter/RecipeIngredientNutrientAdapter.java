/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mad.otfrecipes.R;

import java.util.ArrayList;

/** Adapter for the RecyclerViews for the ingredients and nutrients in RecipeActivity */
public class RecipeIngredientNutrientAdapter extends RecyclerView.Adapter<RecipeIngredientNutrientAdapter.ViewHolder> {
    private ArrayList<String> mIngredientsList;
    private Context mContext;

    /** Stores the different UI elements used to display a recipe ingredient or nutrient item **/
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ingredientText;

        //Constructor method
        public ViewHolder(View v) {
            super(v);
            ingredientText = itemView.findViewById(R.id.text_recipe_ingredient_nutrient);
        }
    }

    //Constructor method
    public RecipeIngredientNutrientAdapter(ArrayList<String> ingredients, Context context) {
        this.mIngredientsList = ingredients;
        this.mContext = context;
    }

    /**
     * Gets and provides the newly created ViewHolder to the onBindViewHolder so that data can
     * be displayed
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ingredientItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_ingredient_nutrient_item, parent, false);

        return new ViewHolder(ingredientItemView);
    }

    /** Sets up the display of the data for the ViewHolder passed in by onCreateViewHolder i.e. text */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String ingredient = mIngredientsList.get(position);
        holder.ingredientText.setText(ingredient);
    }

    /** Returns the amount of recipe ingredient or nutrient items that are being held **/
    @Override
    public int getItemCount() {
        return mIngredientsList.size();
    }
}
