package com.blogspot.sontx.myapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blogspot.sontx.iot.shared.model.bean.Device;

/**
 * Copyright by NE 2016.
 * Created by noem on 06/01/2016.
 */
public abstract class TaskActivity extends AppCompatActivity {
    public static final String INTENT_DEVICE_ID = "device_id";

    protected int getDeviceId() {
        Intent intent = getIntent();
        return intent.getExtras().getInt(INTENT_DEVICE_ID);
    }
}
