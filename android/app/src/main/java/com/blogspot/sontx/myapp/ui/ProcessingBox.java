package com.blogspot.sontx.myapp.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.blogspot.sontx.myapp.R;

/**
 * Copyright by NE 2016.
 * Created by noem on 04/01/2016.
 */
public class ProcessingBox {
    private Dialog mDialog;
    public ProcessingBox(Context context) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_processingbox);
        mDialog.setCancelable(false);
    }
    public void show() {
        mDialog.show();
    }
    public void hide() {
        mDialog.hide();
    }
    public void dismiss() {
        mDialog.dismiss();
    }
}
