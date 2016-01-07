package com.blogspot.sontx.myapp.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.blogspot.sontx.myapp.R;

/**
 * Copyright by NE 2016.
 * Created by noem on 04/01/2016.
 */
public class InputBox implements View.OnClickListener {
    private Dialog mDialog;
    private TextView mTitleView;
    private TextView mMessageView;
    private EditText mInputView;
    private OnInputCompletedListener mOnInputCompletedListener;

    public void setOnInputCompletedListener(OnInputCompletedListener listener) {
        mOnInputCompletedListener = listener;
    }

    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setMessage(CharSequence message) {
        mMessageView.setText(message);
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public InputBox(Context context, String initValue) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_inputbox);
        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mTitleView = (TextView) mDialog.findViewById(R.id.inputbox_tv_title);
        mMessageView = (TextView) mDialog.findViewById(R.id.inputbox_tv_message);
        mInputView = (EditText) mDialog.findViewById(R.id.inputbox_et_enter);

        mDialog.findViewById(R.id.inputbox_btn_cancel).setOnClickListener(this);
        mDialog.findViewById(R.id.inputbox_btn_ok).setOnClickListener(this);

        mInputView.setText(initValue);
    }

    public InputBox(Context context) {
        this(context, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.inputbox_btn_cancel) {
            mDialog.dismiss();
        } else if (v.getId() == R.id.inputbox_btn_ok) {
            if (mOnInputCompletedListener != null)
                mOnInputCompletedListener.inputCompleted(this, mInputView.getText().toString());
        }
    }

    public interface OnInputCompletedListener {
        void inputCompleted(InputBox box, @NonNull String content);
    }
}
