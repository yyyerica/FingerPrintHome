package com.example.yyy.fingerprint.RequestService;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.example.yyy.fingerprint.LoginRegister.PostOptions;
import com.example.yyy.fingerprint.LoginRegister.PostThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by admin on 2017/2/26.
 */

public class VerifyThread extends Thread {
    private String user_id;
    private String IMEI;
    private Synchro synchro;
    private String url;
    private Activity activity;

    public VerifyThread(String user_id, String IMEI, Synchro synchro, String url, Activity activity) {
        this.user_id = user_id;
        this.IMEI = IMEI;
        this.synchro = synchro;
        this.url = url;
        this.activity = activity;
    }

    @Override
    public void run() {
        super.run();
        getRun();
    }

    private void getRun() {
        Log.e("getRun","start");

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

            if (!TextUtils.isEmpty(synchro.getAuthority_number()) && !TextUtils.isEmpty(synchro.getGuid())
            && !TextUtils.isEmpty(synchro.getFile_path()) && !TextUtils.isEmpty(synchro.getOperate_date()) &&
                    !TextUtils.isEmpty(synchro.getOperate_time()) && !TextUtils.isEmpty(synchro.getIsPermit())) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                String source = "guid=" + synchro.getGuid()
                        + "&file_path=" + synchro.getFile_path()
                        + "&authority_number=" + synchro.getAuthority_number()
                        + "&operate_date=" + synchro.getOperate_date()
                        + "&operate_time=" + synchro.getOperate_time()
                        + "&isPermit=" + synchro.getIsPermit()
                        + "&isSend=" + synchro.getIsSend();
                source = PostThread.encrypt(source);
                String params = "model=" + PostOptions.VERIFY
                        + "&user_id=" + URLEncoder.encode(user_id, "utf-8")
                        + "&IMEI=" + URLEncoder.encode(IMEI, "utf-8")
                        + source;
                outputStream.write(params.getBytes());
                Log.e("getRun","end");
            }

            //获取内容
            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            String response = stringBuffer.toString();
            response = PostThread.decrypt(response);
            Log.d("response", response);

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
