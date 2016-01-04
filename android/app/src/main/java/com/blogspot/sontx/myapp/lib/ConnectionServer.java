package com.blogspot.sontx.myapp.lib;

import android.support.annotation.Nullable;

import com.blogspot.sontx.iot.shared.CrossFlatform;
import com.blogspot.sontx.iot.shared.model.bean.Account;
import com.blogspot.sontx.iot.shared.model.bean.Device;
import com.blogspot.sontx.iot.shared.model.bean.RealTime;
import com.blogspot.sontx.iot.shared.model.bean.TransmissionObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Copyright by NE 2016.
 * Created by noem on 03/01/2016.
 */
public final class ConnectionServer {
    private static ConnectionServer instance;
    private static final int BUFFER_SIZE = 2048;
    private String mIPv4;
    private int mPort;
    private final String mServerName;
    private Account mAccount;

    public boolean ready() {
        return (mIPv4 != null && mIPv4.length() > 0) && (mPort > -1);
    }

    private ConnectionServer() {
        mPort = 8080;
        mServerName = "MyWS";
        mIPv4 = "192.168.1.111";
    }

    public static ConnectionServer getInstance() {
        if (instance == null)
            instance = new ConnectionServer();
        return instance;
    }

    public void setAccount(Account account) {
        mAccount = account;
    }

    public void setIPv4(String addr) {
        this.mIPv4 = addr;
    }

    public void setPort(int port) {
        mPort = port;
    }

    private String getHttpString(String servletName, String params) {
        String http = String.format("http://%s:%d/%s/%s", mIPv4, mPort, mServerName, servletName);
        if (params != null && params.length() > 0)
            return String.format("%s?%s", http, params);
        return http;
    }

    private TransmissionObject sendForResult(String httpUrl) {
        TransmissionObject content = null;
        HttpURLConnection cnn = null;
        try {
            URL url = new URL(httpUrl);
            cnn = (HttpURLConnection) url.openConnection();
            byte[] buff = new byte[BUFFER_SIZE];
            int ret = cnn.getInputStream().read(buff);
            byte[] data = new byte[ret];
            System.arraycopy(buff, 0, data, 0, data.length);
            content = CrossFlatform.fromBytes(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (cnn != null)
                cnn.disconnect();
        }
        return content;
    }

    private TransmissionObject sendForResult(String servletName, String params) {
        String httpUrl = getHttpString(servletName, params);
        return sendForResult(httpUrl);
    }

    @Nullable
    public Account checkLogin(Account account) {
        String params = String.format("req=check&username=%s&passhash=%s",
                                      account.getUserName(), account.getPasswordHash());
        TransmissionObject obj = sendForResult("AccountServlet", params);
        if (obj == null)
            return null;
        int code = obj.getCode();
        if (code == TransmissionObject.CODE_AUTH_ERR || code == TransmissionObject.CODE_DATA_NULL)
            return null;
        return (Account) obj.getData();
    }

    private TransmissionObject sendWithAuthentication(String servletName, String params) {
        params = String.format("username=%s&passhash=%s&%s",
                mAccount.getUserName(), mAccount.getPasswordHash(), params);
        return sendForResult(servletName, params);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public List<Device> getDevices() {
        String params = "req=all";
        TransmissionObject obj = sendWithAuthentication("DeviceServlet", params);
        if (obj == null)
            return null;
        if (obj.getCode() != TransmissionObject.CODE_DATA_OK)
            return null;
        return (List<Device>) obj.getData();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public RealTime getRealtime(int deviceId) {
        String params = String.format("id=%d", deviceId);
        TransmissionObject obj = sendWithAuthentication("RealTimeServlet", params);
        if (obj == null)
            return null;
        if (obj.getCode() != TransmissionObject.CODE_DATA_OK)
            return null;
        return (RealTime) obj.getData();
    }
}
