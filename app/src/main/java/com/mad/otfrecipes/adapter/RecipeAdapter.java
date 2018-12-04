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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mad.otfrecipes.R;
import com.mad.otfrecipes.activity.RecipeListActivity;
import com.mad.otfrecipes.model.Recipe;

import java.util.ArrayList;

/** Adapter for the RecyclerView for the recipes in the recipes search results in RecipeListActivity */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private ArrayList<Recipe> mRecipesList;
    private Context mContext;

    /** Stores the different UI elements used to display a recipe item **/
    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout recipeLayout;
        public TextView recipeNameText, recipeServingsText, recipeCookingTimeText;
        public ImageView recipeImage;

        //Constructor method
        public ViewHolder(View v) {
            super(v);
            recipeLayout = itemView.findViewById(R.id.linear_layout_recipe);
            recipeNameText = itemView.findViewById(R.id.text_recipe_name);
            recipeServingsText = itemView.findViewById(R.id.text_servings);
            recipeCookingTimeText = itemView.findViewById(R.id.text_cooking_time);
            recipeImage = itemView.findViewById(R.id.image_recipe);
        }
    }

    //Constructor method
    public RecipeAdapter(ArrayList<Recipe> recipes, Context context) {
        this.mRecipesList = recipes;
        this.mContext = context;
    }

    /**
     * Gets and provides the newly created ViewHolder to the onBindViewHolder so that data can
     * be displayed
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View recipeItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);

        return new ViewHolder(recipeItemView);
    }

    /**
     * Sets up the display of the data for the ViewHolder passed in by onCreateViewHolder i.e. text
     * and activity to call when clicking on an item
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Recipe recipe = mRecipesList.get(position);
        holder.recipeNameText.setText(recipe.getRecipeName());
        holder.recipeServingsText.setText(recipe.getRecipeServings());
        holder.recipeCookingTimeText.setText(recipe.getRecipeCookingTime());
        holder.recipeImage.setImageBitmap(recipe.getRecipeImage());
        holder.recipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RecipeListActivity)mContext).recipeClickedHandler(recipe);
            }
        });
    }

    /** Returns the amount of recipe items that are being held **/
    @Override
    public int getItemCount() {
        return mRecipesList.size();
    }
}
