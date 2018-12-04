/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.model;

/** Model for the method item displayed in the method RecyclerView in RecipeActivity */
public class Method {
    private String mHeading;
    private String mInstruction;

    //Constructor method
    public Method(String heading, String instruction) {
        mHeading = heading;
        mInstruction = instruction;
    }

    //Getters and setters
    public String getHeading() {
        return mHeading;
    }

    public void setHeading(String heading) {
        mHeading = heading;
    }

    public String getInstruction() {
        return mInstruction;
    }

    public void setInstruction(String instruction) {
        mInstruction = instruction;
    }
}
