package com.example.yyy.fingerprint.LoginRegister;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by admin on 2017/2/18.
 */
public class GetIdThread extends Thread {
    private String IMEI;
    private String url = "";

    public GetIdThread(String IMEI, String url) {
        this.IMEI = IMEI;
        this.url = url;
    }

    @Override
    public void run() {
        super.run();
        getRun();
    }

    private void getRun() {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("please ensure url is not equals  null ");
        }
        BufferedReader bufferedReader = null;
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
            //设置请求头header
            httpURLConnection.setRequestProperty("test-header", "post-header-value");
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setReadTimeout(5000);

            if (!TextUtils.isEmpty(IMEI)) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                String params = "model=" + PostOptions.GET_ID
                        + "&IMEI=" + URLEncoder.encode(IMEI, "utf-8");
                outputStream.write(params.getBytes());
            }

            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            String response = stringBuffer.toString().replaceAll("\r\n","").replaceAll(" ", "").trim();
            JSONObject jsonObject = new JSONObject(response);
            Keys.USER_ID = jsonObject.getString("user_id");
            Keys.SERVER_PUBLIC_KEY = jsonObject.getString("server_publicKey");
            Keys.CLIENT_PUBLIC_KEY = jsonObject.getString("client_publicKey");
            Keys.CLIENT_PRIVATE_KEY = jsonObject.getString("client_privateKey");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
