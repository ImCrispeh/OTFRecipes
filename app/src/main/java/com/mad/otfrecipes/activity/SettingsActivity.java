/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.mad.otfrecipes.R;
import com.mad.otfrecipes.constants.PermissionsConstants;
import com.mad.otfrecipes.constants.PreferencesConstants;
import com.mad.otfrecipes.model.SharedPreferencesHelperImpl;
import com.mad.otfrecipes.presenter.SettingsPresenter;
import com.mad.otfrecipes.speech.SpeechActivator;
import com.mad.otfrecipes.speech.SpeechProcessor;
import com.mad.otfrecipes.view.SettingsView;

import java.util.Random;

/**
 * Provides the user with various settings that they can configure for the recipe search e.g.
 * how restricted the recipes shown are based on the passed in ingredients and whether to filter by
 * a certain amount of different nutritional values such as calories
 */
public class SettingsActivity extends AppCompatActivity implements SettingsView, SpeechProcessor {
    private Button mResetBtn;
    private Button mSaveBtn;

    private RadioGroup mSpeechRadioGroup;
    private int mSpeechOnBtnId, mSpeechOffBtnId;

    private Spinner mDietSpinner;

    private RadioGroup mIngredientsRadioGroup;
    private int mRestrictChosenIngredientsBtnId, mAllowIngredientsBtnId, mNoRestrictIngredientsBtnId;
    private EditText mIngredientsNumberEditText;

    private RadioGroup mCaloriesRadioGroup;
    private int mRestrictCaloriesBtnId, mNoRestrictCaloriesBtnId;
    private EditText mCaloriesEditText;

    private RadioGroup mFatRadioGroup;
    private int mRestrictFatBtnId, mNoRestrictFatBtnId;
    private EditText mFatEditText;

    private RadioGroup mSugarRadioGroup;
    private int mRestrictSugarBtnId, mNoRestrictSugarBtnId;
    private EditText mSugarEditText;

    private SettingsPresenter mPresenter;
    private SpeechActivator mSpeechActivator;
    private String[] mVoiceHints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPresenter = new SettingsPresenter(this, new SharedPreferencesHelperImpl(this.getApplicationContext()));

        mResetBtn = findViewById(R.id.btn_reset);
        mSaveBtn = findViewById(R.id.btn_save);

        //Speech setting
        mSpeechRadioGroup = findViewById(R.id.radio_group_speech);
        mSpeechOnBtnId = R.id.radio_button_speech_on;
        mSpeechOffBtnId = R.id.radio_button_speech_off;

        mDietSpinner = findViewById(R.id.diet_spinner);

        //Ingredients restriction view setting
        mIngredientsRadioGroup = findViewById(R.id.radio_group_ingredient_restriction);
        mRestrictChosenIngredientsBtnId = R.id.radio_button_complete_restrict_ingredients;
        mAllowIngredientsBtnId = R.id.radio_button_allow_other_ingredients;
        mNoRestrictIngredientsBtnId = R.id.radio_button_no_restriction_ingredients;
        mIngredientsNumberEditText = findViewById(R.id.edit_text_ingredients_number);

        //Calorie restriction view setting
        mCaloriesRadioGroup = findViewById(R.id.radio_group_calorie_restriction);
        mRestrictCaloriesBtnId = R.id.radio_button_restrict_calories;
        mNoRestrictCaloriesBtnId = R.id.radio_button_no_restriction_calories;
        mCaloriesEditText = findViewById(R.id.edit_text_calories_number);

        //Fat restriction view setting
        mFatRadioGroup = findViewById(R.id.radio_group_fat_restriction);
        mRestrictFatBtnId = R.id.radio_button_restrict_fat;
        mNoRestrictFatBtnId = R.id.radio_button_no_restriction_fat;
        mFatEditText = findViewById(R.id.edit_text_fat_number);

        //Sugar restriction view setting
        mSugarRadioGroup = findViewById(R.id.radio_group_sugar_restriction);
        mRestrictSugarBtnId = R.id.radio_button_restrict_sugar;
        mNoRestrictSugarBtnId = R.id.radio_button_no_restriction_sugar;
        mSugarEditText = findViewById(R.id.edit_text_sugar_number);

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.resetButtonClicked();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        mPresenter.setUpSelected();

        findViewById(mSpeechOnBtnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.enableSpeechSettings(true);
                activateSpeech();
            }
        });

        findViewById(mSpeechOffBtnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.enableSpeechSettings(false);
                if (mSpeechActivator != null) {
                    mSpeechActivator.stopListeningForSpeech();
                }
            }
        });

        mVoiceHints = getResources().getStringArray(R.array.settings_voice_hints);

        mPresenter.setSpeechRecognition();
    }

    public void showInitialHint() {
        Toast.makeText(this.getApplicationContext(), getString(R.string.settings_speech_toast_hint), Toast.LENGTH_LONG).show();
    }

    /** Creates object that holds functionality for speech recognition and calls required permissions to be checked */
    @Override
    public void activateSpeech() {
        mSpeechActivator = new SpeechActivator(this, this);
        mSpeechActivator.RequestAudioPermissions();
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

        //Command for deactivating voice command functionality
        else if (result.contains("voice") || result.contains("speech")) {
            if (result.contains("off")) {
                findViewById(mSpeechOffBtnId).performClick();
            }
        }

        //Command for setting diet
        if (result.contains("diet")) {
            for (int i = 0; i < mDietSpinner.getCount(); i++) {
                if (result.contains(mDietSpinner.getItemAtPosition(i).toString().toLowerCase())) {
                    mDietSpinner.setSelection(i);
                }
            }
        }

        //Command for resetting settings to their defaults
        else if (result.contains("reset")) {
            mPresenter.resetButtonClicked();
        }

        else if (result.contains("save")) {
            saveSettings();
        }

        //Commands for setting the different restrictions
        else if (result.contains("restrict")) {

            //Commands for ingredient restriction settings
            if (result.contains("ingredient")) {
                if (result.contains("only") || result.contains("chosen")) {
                    findViewById(mRestrictChosenIngredientsBtnId).performClick();
                } else if (result.contains("allow") || result.contains("extra")) {
                    findViewById(mAllowIngredientsBtnId).performClick();

                    //Remove all non-digit characters from result string
                    result = result.replaceAll("[\\D]", "");
                    if (!result.isEmpty()) {
                        mIngredientsNumberEditText.setText(result);
                    }
                } else if (result.contains("off") || result.contains("no")) {
                    findViewById(mNoRestrictIngredientsBtnId).performClick();
                }
            }

            //Commands for calorie restriction settings
            else if (result.contains("calorie")) {
                if (result.contains("off") || result.contains("no")) {
                    findViewById(mNoRestrictCaloriesBtnId).performClick();
                } else {
                    findViewById(mRestrictCaloriesBtnId).performClick();

                    //Remove all non-digit characters from result string
                    result = result.replaceAll("[\\D]", "");
                    if (!result.isEmpty()) {
                        mCaloriesEditText.setText(result);
                    }
                }
            }

            //Commands for sugar restriction settings
            else if (result.contains("sugar")) {
                if (result.contains("off") || result.contains("no")) {
                    findViewById(mNoRestrictSugarBtnId).performClick();
                } else {
                    findViewById(mRestrictSugarBtnId).performClick();

                    //Remove all non-digit characters from result string
                    result = result.replaceAll("[\\D]", "");
                    if (!result.isEmpty()) {
                        mSugarEditText.setText(result);
                    }
                }
            }

            //Commands for fat restriction settings
            else if (result.contains("fat")) {
                if (result.contains("off") || result.contains("no")) {
                    findViewById(mNoRestrictFatBtnId).performClick();
                } else {
                    findViewById(mRestrictFatBtnId).performClick();

                    //Remove all non-digit characters from result string
                    result = result.replaceAll("[\\D]", "");
                    if (!result.isEmpty()) {
                        mFatEditText.setText(result);
                    }
                }
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

    /**
     * Sets the values for settings based on passed in values from SharedPreferences
     */
    @Override
    public void setSettingSelections(PreferencesConstants.IngredientsRestriction ingredientRestrict, int ingredientNumber,
                                     PreferencesConstants.NutritionalRestriction calorieRestrict, String calorieNumber,
                                     PreferencesConstants.NutritionalRestriction fatRestrict, String fatNumber,
                                     PreferencesConstants.NutritionalRestriction sugarRestrict, String sugarNumber,
                                     PreferencesConstants.SpeechRecognition speechStatus, String diet) {
        switch (speechStatus) {
            case SPEECH_ON:
                mSpeechRadioGroup.check(mSpeechOnBtnId);
                break;
            case SPEECH_OFF:
                mSpeechRadioGroup.check(mSpeechOffBtnId);
                break;
            default:
                mSpeechRadioGroup.check(mSpeechOffBtnId);
                break;
        }

        mDietSpinner.setSelection(getItemIndex(diet));

        switch (ingredientRestrict) {
            case COMPLETE_RESTRICTION:
                mIngredientsRadioGroup.check(mRestrictChosenIngredientsBtnId);
                break;
            case ALLOW_OTHER_INGREDIENTS:
                mIngredientsRadioGroup.check(mAllowIngredientsBtnId);
                mIngredientsNumberEditText.setText(Integer.toString(ingredientNumber));
                break;
            case NO_RESTRICTION:
                mIngredientsRadioGroup.check(mNoRestrictIngredientsBtnId);
                break;
            default:
                mIngredientsRadioGroup.check(mNoRestrictIngredientsBtnId);
                break;
        }

        switch (calorieRestrict) {
            case RESTRICTION:
                mCaloriesRadioGroup.check(mRestrictCaloriesBtnId);
                mCaloriesEditText.setText(calorieNumber);
                break;
            case NO_RESTRICTION:
                mCaloriesRadioGroup.check(mNoRestrictCaloriesBtnId);
                break;
            default:
                mCaloriesRadioGroup.check(mNoRestrictCaloriesBtnId);
                break;
        }

        switch (fatRestrict) {
            case RESTRICTION:
                mFatRadioGroup.check(mRestrictFatBtnId);
                mFatEditText.setText(fatNumber);
                break;
            case NO_RESTRICTION:
                mFatRadioGroup.check(mNoRestrictFatBtnId);
                break;
            default:
                mFatRadioGroup.check(mNoRestrictFatBtnId);
                break;
        }

        switch (sugarRestrict) {
            case RESTRICTION:
                mSugarRadioGroup.check(mRestrictSugarBtnId);
                mSugarEditText.setText(sugarNumber);
                break;
            case NO_RESTRICTION:
                mSugarRadioGroup.check(mNoRestrictSugarBtnId);
                break;
            default:
                mSugarRadioGroup.check(mNoRestrictSugarBtnId);
                break;
        }
    }

    private int getItemIndex(String value) {
        for (int i = 0; i < mDietSpinner.getCount(); i++) {
            if (mDietSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Gets values of settings and passes them to presenter before finishing the activity
     */
    public void saveSettings() {
        String ingredientsRestrict;
        int ingredientNumber;
        String calorieRestrict;
        String calorieNumber = PreferencesConstants.DEFAULT_RESTRICTION_NUMBER;
        String fatRestrict;
        String fatNumber = PreferencesConstants.DEFAULT_RESTRICTION_NUMBER;
        String sugarRestrict;
        String sugarNumber = PreferencesConstants.DEFAULT_RESTRICTION_NUMBER;
        String speechStatus;
        String diet;

        if (mIngredientsNumberEditText.getText().toString().isEmpty()) {
            ingredientNumber = 0;
        } else {
            ingredientNumber = Integer.parseInt(mIngredientsNumberEditText.getText().toString());
        }

        calorieNumber = checkEmptyRestriction(mCaloriesEditText);
        fatNumber = checkEmptyRestriction(mFatEditText);
        sugarNumber = checkEmptyRestriction(mSugarEditText);

        switch (mSpeechRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_button_speech_on:
                speechStatus = PreferencesConstants.SpeechRecognition.SPEECH_ON.toString();
                break;
            case R.id.radio_button_speech_off:
                speechStatus = PreferencesConstants.SpeechRecognition.SPEECH_OFF.toString();
                break;
            default:
                speechStatus = PreferencesConstants.SpeechRecognition.SPEECH_OFF.toString();
                break;
        }

        diet = mDietSpinner.getSelectedItem().toString().toLowerCase();

        switch (mIngredientsRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_button_complete_restrict_ingredients:
                ingredientsRestrict = PreferencesConstants.IngredientsRestriction.COMPLETE_RESTRICTION.toString();
                break;
            case R.id.radio_button_allow_other_ingredients:
                ingredientsRestrict = PreferencesConstants.IngredientsRestriction.ALLOW_OTHER_INGREDIENTS.toString();
                break;
            case R.id.radio_button_no_restriction_ingredients:
                ingredientsRestrict = PreferencesConstants.IngredientsRestriction.NO_RESTRICTION.toString();
                break;
            default:
                ingredientsRestrict = PreferencesConstants.IngredientsRestriction.NO_RESTRICTION.toString();
                break;
        }

        switch (mCaloriesRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_button_restrict_calories:
                calorieRestrict = PreferencesConstants.NutritionalRestriction.RESTRICTION.toString();
                break;
            case R.id.radio_button_no_restriction_calories:
                calorieRestrict = PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString();
                break;
            default:
                calorieRestrict = PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString();
                break;
        }

        switch (mFatRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_button_restrict_fat:
                fatRestrict = PreferencesConstants.NutritionalRestriction.RESTRICTION.toString();
                break;
            case R.id.radio_button_no_restriction_fat:
                fatRestrict = PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString();
                break;
            default:
                fatRestrict = PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString();
                break;
        }

        switch (mSugarRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_button_restrict_sugar:
                sugarRestrict = PreferencesConstants.NutritionalRestriction.RESTRICTION.toString();
                break;
            case R.id.radio_button_no_restriction_sugar:
                sugarRestrict = PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString();
                break;
            default:
                sugarRestrict = PreferencesConstants.NutritionalRestriction.NO_RESTRICTION.toString();
                break;
        }

        mPresenter.saveButtonClicked(ingredientsRestrict, ingredientNumber, calorieRestrict, calorieNumber,
                fatRestrict, fatNumber, sugarRestrict, sugarNumber, speechStatus, diet);
        if (mSpeechActivator != null) {
            mSpeechActivator.stopListeningForSpeech();
        }
        Toast.makeText(this.getApplicationContext(), getString(R.string.settings_saved_toast), Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Checks whether the given restriction edit text is empty and sets the corresponding restriction
     * number accordingly
     * @param editText restriction edit text
     * @return value to set corresponding restriction number to
     */
    private String checkEmptyRestriction(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            return PreferencesConstants.DEFAULT_RESTRICTION_NUMBER;
        } else {
            return editText.getText().toString();
        }
    }

    /**
     * Resets settings to their default state but does not save them
     */
    @Override
    public void resetSettings() {
        mIngredientsRadioGroup.check(mNoRestrictIngredientsBtnId);
        mIngredientsNumberEditText.setText("");
        mDietSpinner.setSelection(0);
        mCaloriesRadioGroup.check(mNoRestrictCaloriesBtnId);
        mCaloriesEditText.setText("");
        mFatRadioGroup.check(mNoRestrictFatBtnId);
        mFatEditText.setText("");
        mSugarRadioGroup.check(mNoRestrictSugarBtnId);
        mSugarEditText.setText("");
        mSpeechRadioGroup.check(mSpeechOffBtnId);
        Toast.makeText(this.getApplicationContext(), getString(R.string.settings_reset_toast), Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigates to parent activity (MainActivity)
     */
    @Override
    public void navigateUp() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onPause() {
        if (mSpeechActivator != null) {
            mSpeechActivator.stopListeningForSpeech();
        }
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionsConstants.RECORD_AUDIO_CODE:
                //Show notification that speech recognition was activated
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    activateSpeech();
                    showInitialHint();
                } else {
                    mSpeechRadioGroup.check(mSpeechOffBtnId);
                }
        }
    }
}
