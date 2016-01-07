package com.blogspot.sontx.myapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.sontx.iot.shared.Security;
import com.blogspot.sontx.iot.shared.model.bean.Account;
import com.blogspot.sontx.iot.shared.utils.Convert;
import com.blogspot.sontx.myapp.R;
import com.blogspot.sontx.myapp.lib.Config;
import com.blogspot.sontx.myapp.lib.ConnectionServer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CODE_LOGIN = 1;
    private boolean mLogged = false;
    private TextView mUserNameView;
    private TextView mIPView;
    private TextView mPortView;

    private SharedPreferences getSharedPref() {
        return getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, MODE_PRIVATE);
    }

    private void checkLogin() {
        if (mLogged)
            return;
        SharedPreferences sharedPref = getSharedPref();
        int id = sharedPref.getInt(Config.SHARED_PREF_ACC_ID, -1);
        if (id < 0) {
            startActivityForResult(new Intent(this, LoginActivity.class), CODE_LOGIN);
        }
        else {
            mLogged = true;
            setupConnectionAuthentication();
        }
    }

    private void reloadDisplayInfo() {
        SharedPreferences sharedPref = getSharedPref();
        mUserNameView.setText(sharedPref.getString(Config.SHARED_PREF_ACC_USERNAME, null));
        mIPView.setText(sharedPref.getString(Config.SHARED_PREF_SRV_IP, ""));
        mPortView.setText(String.format("%d", sharedPref.getInt(Config.SHARED_PREF_SRV_PORT, 8080)));
    }

    private void setupConnectionAuthentication() {
        Account account = new Account();
        SharedPreferences sharedPref = getSharedPref();
        account.setUserName(sharedPref.getString(Config.SHARED_PREF_ACC_USERNAME, null));
        account.setPasswordHash(sharedPref.getString(Config.SHARED_PREF_ACC_PASSWORDHASH, null));
        account.setId(sharedPref.getInt(Config.SHARED_PREF_ACC_ID, -1));
        ConnectionServer.getInstance().setAccount(account);
    }

    private void settingConnection() {
        ConnectionServer connectionServer = ConnectionServer.getInstance();
        SharedPreferences sharedPref = getSharedPref();
        connectionServer.setIPv4(sharedPref.getString(Config.SHARED_PREF_SRV_IP, ""));
        connectionServer.setPort(sharedPref.getInt(Config.SHARED_PREF_SRV_PORT, 8080));

        if (connectionServer.ready())
            checkLogin();
        else
            changeIP();

        reloadDisplayInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserNameView = (TextView) findViewById(R.id.main_tv_username);
        mIPView = (TextView) findViewById(R.id.main_tv_ip);
        mPortView = (TextView) findViewById(R.id.main_tv_port);

        findViewById(R.id.main_btn_change_username).setOnClickListener(this);
        findViewById(R.id.main_btn_change_password).setOnClickListener(this);
        findViewById(R.id.main_btn_change_ip).setOnClickListener(this);
        findViewById(R.id.main_btn_change_port).setOnClickListener(this);
        findViewById(R.id.main_btn_devices).setOnClickListener(this);
        findViewById(R.id.main_btn_go_su).setOnClickListener(this);

        settingConnection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_LOGIN && resultCode == RESULT_OK) {
            reloadDisplayInfo();
            setupConnectionAuthentication();
            mLogged = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_btn_change_username) {
            changeUserName();
        } else if (v.getId() == R.id.main_btn_change_password) {
            changePassword();
        } else if (v.getId() == R.id.main_btn_change_ip) {
            changeIP();
        } else if (v.getId() == R.id.main_btn_change_port) {
            changePort();
        } else if (v.getId() == R.id.main_btn_devices) {
            displayDevices();
        } else if (v.getId() == R.id.main_btn_go_su) {
            gotoSuperUser();
        }
    }

    private void changePassword() {
        if (!mLogged)
            return;
        InputBox box = new InputBox(this);
        box.setMessage("Change your password here:");
        box.setOnInputCompletedListener(new InputBox.OnInputCompletedListener() {
            @Override
            public void inputCompleted(InputBox box, @NonNull final String content) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String passwordHash = Security.getPasswordHash(content);
                        final boolean ok = ConnectionServer.getInstance().changePassword(passwordHash);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ok) {
                                    SharedPreferences.Editor editor = getSharedPref().edit();
                                    editor.putString(Config.SHARED_PREF_ACC_PASSWORDHASH, passwordHash);
                                    editor.apply();
                                    setupConnectionAuthentication();
                                    Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).start();


                box.dismiss();
            }
        });
        box.show();
    }

    private void displayDevices() {
        startActivity(new Intent(this, DevicesActivity.class));
    }

    private void changeUserName() {
        SharedPreferences sharedPref = getSharedPref();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Config.SHARED_PREF_ACC_ID);
        editor.apply();
        mLogged = false;
        checkLogin();
    }

    private void gotoSuperUser() {
        SharedPreferences sharedPref = getSharedPref();
        String link = String.format(
                "http://%s:%d/MyWS/SuperuserServlet",
                sharedPref.getString(Config.SHARED_PREF_SRV_IP, ""),
                sharedPref.getInt(Config.SHARED_PREF_SRV_PORT, 8080));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    private void changeIP() {
        InputBox box = new InputBox(this, getSharedPref().getString(Config.SHARED_PREF_SRV_IP, ""));
        box.setOnInputCompletedListener(new InputBox.OnInputCompletedListener() {
            @Override
            public void inputCompleted(InputBox box, @NonNull String content) {
                if (content.length() > 0) {
                    SharedPreferences sharedPref = getSharedPref();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(Config.SHARED_PREF_SRV_IP, content);
                    editor.apply();
                    settingConnection();
                    box.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "IP not empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        box.setMessage("Enter new server IP:");
        box.show();
    }

    private void changePort() {
        InputBox box = new InputBox(this, String.format("%d", getSharedPref().getInt(Config.SHARED_PREF_SRV_PORT, 2512)));
        box.setOnInputCompletedListener(new InputBox.OnInputCompletedListener() {
            @Override
            public void inputCompleted(InputBox box, @NonNull String content) {
                if (content.length() > 0) {
                    int port = Convert.parseInt(content, -1);
                    if (port > -1) {
                        SharedPreferences sharedPref = getSharedPref();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(Config.SHARED_PREF_SRV_PORT, port);
                        editor.apply();
                        settingConnection();
                        box.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "Port must is numbers!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Port not empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        box.setMessage("Enter new server Port:");
        box.show();
    }
}
