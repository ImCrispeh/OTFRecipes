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
import android.widget.ImageButton;
import android.widget.TextView;

import com.mad.otfrecipes.R;
import com.mad.otfrecipes.activity.AddIngredientsActivity;
import com.mad.otfrecipes.activity.MainActivity;

import java.util.ArrayList;

/**
 * Adapter for the RecyclerView for the ingredients in ingredients to add list in
 * AddIngredientsActivity and the recipe filter list in MainActivity
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private ArrayList<String> mIngredientsList;
    private Context mContext;

    /** Stores the different UI elements used to display an ingredient item **/
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ingredientNameText;
        public ImageButton ingredientDeleteButton;

        //Constructor method
        public ViewHolder(View v) {
            super(v);
            ingredientNameText = itemView.findViewById(R.id.text_ingredient);
            ingredientDeleteButton = itemView.findViewById(R.id.btn_delete_ingredient);
        }
    }

    //Constructor method
    public IngredientAdapter(ArrayList<String> ingredients, Context context) {
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
                .inflate(R.layout.ingredient_item, parent, false);

        return new ViewHolder(ingredientItemView);
    }

    /**
     * Sets up the display of the data for the ViewHolder passed in by onCreateViewHolder i.e. text
     * and activity to call when deleting an item
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String ingredient = mIngredientsList.get(position);
        holder.ingredientNameText.setText(ingredient);

        final int holderPos = holder.getAdapterPosition();
        holder.ingredientDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof MainActivity) {
                    ((MainActivity)mContext).deleteIngredientHandler(holderPos);
                } else {
                    ((AddIngredientsActivity)mContext).deleteIngredientHandler(holderPos);
                }
            }
        });
    }

    /** Returns the amount of ingredient items that are being held **/
    @Override
    public int getItemCount() {
        return mIngredientsList.size();
    }
}
