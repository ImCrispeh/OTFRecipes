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
import com.mad.otfrecipes.model.Method;

import java.util.ArrayList;

/** Adapter for the RecyclerView for the method items in RecipeActivity */
public class RecipeMethodAdapter extends RecyclerView.Adapter<RecipeMethodAdapter.ViewHolder> {
    private ArrayList<Method> mMethodList;
    private Context mContext;

    /** Stores the different UI elements used to display a recipe ingredient or nutrient item **/
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView methodHeadingText, methodInstructionText;

        //Constructor method
        public ViewHolder(View v) {
            super(v);
            methodHeadingText = itemView.findViewById(R.id.text_recipe_method_heading);
            methodInstructionText = itemView.findViewById(R.id.text_recipe_instruction);
        }
    }

    //Constructor method
    public RecipeMethodAdapter(ArrayList<Method> methodItems, Context context) {
        this.mMethodList = methodItems;
        this.mContext = context;
    }

    /**
     * Gets and provides the newly created ViewHolder to the onBindViewHolder so that data can
     * be displayed
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ingredientItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_method_item, parent, false);

        return new ViewHolder(ingredientItemView);
    }

    /** Sets up the display of the data for the ViewHolder passed in by onCreateViewHolder i.e. text */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Method methodItem = mMethodList.get(position);
        holder.methodHeadingText.setText(methodItem.getHeading());
        holder.methodInstructionText.setText(methodItem.getInstruction());
    }

    /** Returns the amount of recipe method items that are being held **/
    @Override
    public int getItemCount() {
        return mMethodList.size();
    }
}
