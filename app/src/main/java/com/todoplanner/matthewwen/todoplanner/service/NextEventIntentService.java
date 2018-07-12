package com.todoplanner.matthewwen.todoplanner.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class NextEventIntentService extends IntentService{

    private static final String name = NextEventIntentService.class.getSimpleName();

    public NextEventIntentService() {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
