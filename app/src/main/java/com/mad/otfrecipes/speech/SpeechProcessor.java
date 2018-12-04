/*
 * Copyright (c) 2018 Christopher Fung.
 * All rights reserved.
 */

package com.mad.otfrecipes.speech;

/**
 * Interface implemented by Activities that allows a SpeechActivator object to call them without
 * having to check the type of activity
 */
public interface SpeechProcessor {
    public void processSpeechResults(String result);
}
