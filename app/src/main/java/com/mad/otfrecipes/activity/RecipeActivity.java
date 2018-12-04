/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.otfrecipes.R;
import com.mad.otfrecipes.adapter.RecipeIngredientNutrientAdapter;
import com.mad.otfrecipes.adapter.RecipeMethodAdapter;
import com.mad.otfrecipes.constants.ExtrasConstants;
import com.mad.otfrecipes.model.Method;
import com.mad.otfrecipes.model.RecipeModel;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.presenter.RecipePresenter;
import com.mad.otfrecipes.speech.SpeechActivator;
import com.mad.otfrecipes.speech.SpeechProcessor;
import com.mad.otfrecipes.view.RecipeView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

/**
 * Display recipe information (name, image, ingredients, recipe) to the user and provides the ability
 * to navigate to the RecipeWebActivity so that the recipe source URL can be opened in a WebView
 */
public class RecipeActivity extends AppCompatActivity implements RecipeView, SpeechProcessor {
    private RecipePresenter mPresenter;
    private RelativeLayout mRecipeInfoLayout;
    private ProgressBar mRecipePageProgress;

    private RecyclerView mIngredientsRecyclerView;
    private RecipeIngredientNutrientAdapter mIngredientAdapter;
    private ArrayList<String> mIngredientList;

    private RecyclerView mMethodRecyclerView;
    private RecipeMethodAdapter mMethodAdapter;
    private ArrayList<Method> mMethodList;

    private RecyclerView mNutrientRecyclerView;
    private RecipeIngredientNutrientAdapter mNutrientAdapter;
    private ArrayList<String> mNutrientList;

    private TextView mRecipeError;
    private TextView mRecipeName;
    private ImageView mRecipeImage;
    private TextView mRecipeImageError;
    private Button mOpenSourceBtn;

    private String mRecipeUrl;
    private TextToSpeech mSpeaker;
    private View mHighlightedView;
    private SpeechActivator mSpeechActivator;
    private String[] mVoiceHints;

    private static final String SPEAKING_ID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPresenter = new RecipePresenter(this, new RecipeModel(), new SharedPreferencesHelperImpl(this.getApplicationContext()));
        mRecipeInfoLayout = findViewById(R.id.relative_layout_recipe_info);
        mRecipePageProgress = findViewById(R.id.progress_recipe_page);

        //Set up RecyclerView for ingredients of the recipe
        mIngredientsRecyclerView = findViewById(R.id.recycler_view_recipe_ingredient);
        mIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mIngredientList = new ArrayList<String>();
        mIngredientAdapter = new RecipeIngredientNutrientAdapter(mIngredientList, this);
        mIngredientsRecyclerView.setAdapter(mIngredientAdapter);

        //Set up RecyclerView for method of the recipe
        mMethodRecyclerView = findViewById(R.id.recycler_view_recipe_method);
        mMethodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMethodList = new ArrayList<Method>();
        mMethodAdapter = new RecipeMethodAdapter(mMethodList, this);
        mMethodRecyclerView.setAdapter(mMethodAdapter);

        //Set up RecyclerView for nutrients of the recipe
        mNutrientRecyclerView = findViewById(R.id.recycler_view_recipe_nutrient);
        mNutrientRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNutrientList = new ArrayList<String>();
        mNutrientAdapter = new RecipeIngredientNutrientAdapter(mNutrientList, this);
        mNutrientRecyclerView.setAdapter(mNutrientAdapter);

        mRecipeError = findViewById(R.id.text_error);

        //Calls for recipe ingredients and method to be retrieved through API and displayed
        mPresenter.loadRecipeInfoHandler(getIntent().getExtras().getString(ExtrasConstants.RECIPE_ID));
        mPresenter.loadRecipeMethodHandler(getIntent().getExtras().getString(ExtrasConstants.RECIPE_ID));

        //Set up and display recipe name and image passed from RecipeListActivity
        Bundle intentExtras = getIntent().getExtras();
        mRecipeName = findViewById(R.id.text_recipe_heading);
        mRecipeName.setText(intentExtras.getString(ExtrasConstants.RECIPE_NAME));
        mRecipeImage = findViewById(R.id.image_recipe_full);
        mRecipeImageError = findViewById(R.id.text_image_error);
        new ImageLoadingTask(intentExtras).execute();

        //Set up button to open recipe URL in WebView
        mOpenSourceBtn = findViewById(R.id.btn_show_in_browser);
        mOpenSourceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.openWebPageButtonClicked();
            }
        });

        //Set up text to speech functions for buttons
        ImageButton readPreviousIngredientBtn = findViewById(R.id.btn_read_previous_ingredient);
        readPreviousIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.readPreviousIngredient();
            }
        });

        ImageButton readCurrentIngredientBtn = findViewById(R.id.btn_read_current_ingredient);
        readCurrentIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.readCurrentIngredient();
            }
        });

        ImageButton readNextIngredientBtn = findViewById(R.id.btn_read_next_ingredient);
        readNextIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.readNextIngredient();
            }
        });

        ImageButton readPreviousMethodBtn = findViewById(R.id.btn_read_previous_method);
        readPreviousMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.readPreviousMethod();
            }
        });

        ImageButton readCurrentMethodBtn = findViewById(R.id.btn_read_current_method);
        readCurrentMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.readCurrentMethod();
            }
        });

        ImageButton readNextMethodBtn = findViewById(R.id.btn_read_next_method);
        readNextMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.readNextMethod();
            }
        });

        ImageButton readPreviousNutrientBtn = findViewById(R.id.btn_read_previous_nutrient);
        readPreviousNutrientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.readPreviousNutrient();
            }
        });

        ImageButton readCurrentNutrientBtn = findViewById(R.id.btn_read_current_nutrient);
        readCurrentNutrientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.readCurrentNutrient();
            }
        });

        ImageButton readNextNutrientBtn = findViewById(R.id.btn_read_next_nutrient);
        readNextNutrientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.readNextNutrient();
            }
        });

        setTextSpeaker();

        mVoiceHints = getResources().getStringArray(R.array.recipe_voice_hints);

        mPresenter.setSpeechRecognition();
    }

    private void setTextSpeaker() {
        mSpeaker = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mSpeaker.setLanguage(Locale.ENGLISH);

                    mSpeaker.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {

                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Log.d("test", "speech done");
                            if (mSpeechActivator != null) {
                                Log.d("test", "not null");
                                if (mSpeechActivator.getIsListening()) {
                                    Log.d("test", "is listening");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mSpeechActivator.startListeningForSpeech();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        if (mSpeechActivator != null) {
            mSpeechActivator.stopListeningForSpeech();
        }

        if (mSpeaker != null) {
            mSpeaker.stop();
            mSpeaker.shutdown();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.setSpeechRecognition();
        setTextSpeaker();
    }

    /** Creates object that holds speech recognition functionality and starts recording */
    @Override
    public void activateSpeech() {
        mSpeechActivator = new SpeechActivator(this, this);
        mSpeechActivator.activateSpeech();
    }

    /**
     * Extracts voice commands from results of speech recognition
     * @param result speech recognition results
     */
    @Override
    public void processSpeechResults(String result) {
        result = result.toLowerCase();

        //Command for up navigation
        if (result.contains("back") || result.contains("return")) {
            mPresenter.upButtonClicked();
        }

        //Commands for initiating text-to-speech
        else if (result.contains("read")) {

            //Commands for reading out the recipe method
            if (result.contains("method") || result.contains("instruction")) {
                if (result.contains("current")) {
                    mPresenter.readCurrentMethod();
                } else if (result.contains("previous")) {
                    mPresenter.readPreviousMethod();
                } else {
                    mPresenter.readNextMethod();
                }
            }

            //Commands for reading out the recipe ingredients
            else if (result.contains("ingredient")) {
                if (result.contains("current")) {
                    mPresenter.readCurrentIngredient();
                } else if (result.contains("previous")) {
                    mPresenter.readPreviousIngredient();
                } else {
                    mPresenter.readNextIngredient();
                }
            }

            //Commands for reading out the recipe ingredients
            else if (result.contains("nutrient")) {
                if (result.contains("current")) {
                    mPresenter.readCurrentNutrient();
                } else if (result.contains("previous")) {
                    mPresenter.readPreviousNutrient();
                } else {
                    mPresenter.readNextNutrient();
                }
            }
        }

        //Command for opening the source page of the recipe
        else if (result.contains("open") || result.contains("view")) {
            if (result.contains("web") || result.contains("page")) {
                mPresenter.openWebPageButtonClicked();
            }
        }

        else if (result.contains("hint")) {
            mPresenter.hintRequested();
        }

        if (mSpeechActivator.getIsListening()) {
            mSpeechActivator.activateSpeech();
        }

    }

    /** Shows hint for possible voice command for the current activity */
    @Override
    public void showVoiceHint() {
        Random rand = new Random();
        int randInt = rand.nextInt(mVoiceHints.length);
        Toast.makeText(this.getApplicationContext(), mVoiceHints[randInt], Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Set call for up navigation
                mPresenter.upButtonClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Shows progress bar while loading in recipe information */
    @Override
    public void showProgress() {
        mRecipeInfoLayout.setVisibility(View.GONE);
        mRecipePageProgress.setVisibility(View.VISIBLE);
    }

    /** Hides progress bar and shows recipe information */
    @Override
    public void hideProgress() {
        mRecipePageProgress.setVisibility(View.GONE);
        mRecipeInfoLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Adds retrieved recipe instructions and displays them
     * @param instructions list of lists of instructions to display, first item in each sublist is
     *                     the heading of that section
     */
    @Override
    public void showRecipeInstructions(ArrayList<ArrayList<String>> instructions) {
        if (instructions != null) {
            Log.d("test", String.valueOf(instructions.size()));
            for (ArrayList<String> subList : instructions) {
                Log.d("test", String.valueOf(subList.size()));
                if (subList.size() > 0) {
                    if (!subList.get(0).equals("")) {
                        mMethodList.add(new Method(subList.get(0), ""));
                    }
                    for (int i = 1; i < subList.size(); i++) {
                        if (!subList.get(i).equals("")) {
                            mMethodList.add(new Method("", subList.get(i)));
                        }
                    }
                }
            }
            mMethodAdapter.notifyDataSetChanged();
            mPresenter.setMaxMethod(mMethodList.size() - 1);
        }
    }

    /**
     * Adds retrieved recipe ingredients and displays them
     * @param ingredients list of ingredients to display
     */
    @Override
    public void showRecipeIngredients(ArrayList<String> ingredients) {
        mIngredientList.addAll(ingredients);
        mIngredientAdapter.notifyDataSetChanged();
        mPresenter.setMaxIngredient(mIngredientList.size() - 1);
    }

    /**
     * Adds retrieved recipe nutritional information and displays them
     * @param nutrients list of nutritional information items to display
     */
    @Override
    public void showRecipeNutrients(ArrayList<String> nutrients) {
        mNutrientList.addAll(nutrients);
        mNutrientAdapter.notifyDataSetChanged();
        mPresenter.setMaxNutrient(mNutrientList.size() - 1);
    }

    /**
     * Reads out a displayed recipe ingredient
     * @param pos position of item to read in ingredient list
     */
    @Override
    public void readIngredientText(int pos) {
        if (mHighlightedView != null) {
            mHighlightedView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundIngredientsList));
        }

        mHighlightedView = mIngredientsRecyclerView.getLayoutManager().findViewByPosition(pos);
        mHighlightedView.setBackgroundColor(getResources().getColor(R.color.colorHighlight));

        readText(mIngredientList.get(pos));
    }

    /**
     * Reads out a displayed recipe instruction
     * @param pos position of item to read in instruction list
     */
    @Override
    public void readMethodText(int pos) {
        if (mHighlightedView != null) {
            mHighlightedView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundIngredientsList));
        }

        mHighlightedView = mMethodRecyclerView.getLayoutManager().findViewByPosition(pos);
        mHighlightedView.setBackgroundColor(getResources().getColor(R.color.colorHighlight));

        Method currMethod = mMethodList.get(pos);
        if (!currMethod.getHeading().equals("")) {
            readText(currMethod.getHeading());
        } else {
            readText(currMethod.getInstruction());
        }
    }

    /**
     * Reads out a displayed nutritional information item
     * @param pos position of item to read in nutrients list
     */
    @Override
    public void readNutrientText(int pos) {
        if (mHighlightedView != null) {
            mHighlightedView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundIngredientsList));
        }

        mHighlightedView = mNutrientRecyclerView.getLayoutManager().findViewByPosition(pos);
        mHighlightedView.setBackgroundColor(getResources().getColor(R.color.colorHighlight));

        readText(mNutrientList.get(pos));
    }

    /**
     * Reads out passed in text
     * @param textToSpeak text to read out
     */
    public void readText(String textToSpeak) {
        if (mSpeechActivator != null) {
            mSpeechActivator.pauseListening();
        }
        if (Build.VERSION.SDK_INT  >= 21) {
            mSpeaker.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, SPEAKING_ID);
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, SPEAKING_ID);
            mSpeaker.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, map);
        }
    }

    /**
     * Sets the URL to be passed to RecipeWebActivity when the BrowserBtn is pressed
     * @param recipeUrl URL to be passed to RecipeWebActivity
     */
    @Override
    public void setUrl(String recipeUrl) {
        mRecipeUrl = recipeUrl;
    }

    /** Navigates to the RecipeWebActivity and passes the URL to open in the WebView */
    @Override
    public void navigateToSource() {
        Intent intent = new Intent(this, RecipeWebActivity.class);
        intent.putExtra(ExtrasConstants.RECIPE_URL, mRecipeUrl);
        startActivity(intent);
    }

    /** Displays an error to the user if the recipe is not loaded correctly */
    @Override
    public void showError() {
        mRecipeError.setVisibility(View.VISIBLE);
    }

    /** Navigates to parent activity (RecipeListActivity) */
    @Override
    public void navigateUp() {
        NavUtils.navigateUpFromSameTask(this);
    }

    /** Calls presenter to stop any async tasks before destroying activity */
    @Override
    protected void onDestroy() {
        mPresenter.stopInfoTask();
        mPresenter.stopMethodTask();

        if (mSpeaker != null) {
            mSpeaker.stop();
            mSpeaker.shutdown();
        }
        super.onDestroy();
    }

    /**
     * AsyncTask for retrieving the image associated with the recipe from the internet and setting
     * it to display
     */
    private class ImageLoadingTask extends AsyncTask<Void, Void, Bitmap> {
        private Bundle mExtras;

        private ImageLoadingTask(Bundle extras) {
            this.mExtras = extras;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                URL imageUrl = new URL(mExtras.getString(ExtrasConstants.RECIPE_IMAGE));
                return (BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                mRecipeImage.setImageBitmap(bitmap);
            } else {
                mRecipeImage.setVisibility(View.GONE);
                mRecipeImageError.setVisibility(View.VISIBLE);
            }
        }
    }
}
