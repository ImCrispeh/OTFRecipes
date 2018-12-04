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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mad.otfrecipes.R;
import com.mad.otfrecipes.activity.AddIngredientsActivity;

import java.util.ArrayList;

/**
 * Adapter for the RecyclerView for the ingredients in the ingredients search results in
 * AddIngredientsActivity
 */
public class IngredientResultAdapter extends RecyclerView.Adapter<IngredientResultAdapter.ViewHolder> {
    private ArrayList<String> mIngredientResultsList;
    private Context mContext;

    /** Stores the different UI elements used to display an ingredient item **/
    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout ingredientResultLayout;
        public TextView ingredientNameText;

        //Constructor method
        public ViewHolder(View v) {
            super(v);
            ingredientResultLayout = itemView.findViewById(R.id.relative_layout_ingredient_result);
            ingredientNameText = itemView.findViewById(R.id.text_ingredient);
        }
    }

    //Constructor method
    public IngredientResultAdapter(ArrayList<String> ingredients, Context context) {
        this.mIngredientResultsList = ingredients;
        this.mContext = context;
    }

    /**
     * Gets and provides the newly created ViewHolder to the onBindViewHolder so that data can
     * be displayed
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ingredientItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_results_item, parent, false);

        return new ViewHolder(ingredientItemView);
    }

    /**
     * Sets up the display of the data for the ViewHolder passed in by onCreateViewHolder i.e. text
     * and activity to call when clicking on an item
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String ingredient = mIngredientResultsList.get(position);
        final int holderPos = holder.getAdapterPosition();
        holder.ingredientResultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AddIngredientsActivity)mContext).ingredientResultHandler(holderPos);
            }
        });
        holder.ingredientNameText.setText(ingredient);
    }

    /** Returns the amount of ingredient items that are being held **/
    @Override
    public int getItemCount() {
        return mIngredientResultsList.size();
    }
}
