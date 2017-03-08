package com.example.yyy.fingerprint.LoginRegister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.yyy.fingerprint.MainActivity;
import com.example.yyy.fingerprint.RegisterActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by admin on 2017/2/17.
 */
public class RegisterThread extends Thread {
    private String user_id;
    private String name;
    private String password;
    private String IMEI;
    private String url = "";
    private RegisterActivity activity;
    private ClientDatabaseHelper mClientDatabaseHelper;

    public RegisterThread(String user_id, String name, String password, String IMEI, String url, RegisterActivity activity) {
        this.user_id = user_id;
        this.name = name;
        this.password = password;
        this.IMEI = IMEI;
        this.url = url;
        this.activity = activity;
        mClientDatabaseHelper = ClientDatabaseHelper.getInstance(activity);
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

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                String source = "name=" + name
                        + "&password=" + password;
                source = PostThread.encrypt(source);
                String params = "model=" + PostOptions.REGISTER
                        + "&user_id=" + URLEncoder.encode(user_id, "utf-8")
                        + "&IMEI=" + URLEncoder.encode(IMEI, "utf-8")
                        + source;
                outputStream.write(params.getBytes());
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
            String[] sourceStrArray = response.split("&");
            String status = sourceStrArray[0].split("=")[1];
            String content = sourceStrArray[1].split("=")[1];
            if(status.equals("OK")) {
                insertSQL();

                Message message = new Message();
                message.arg1 = 1;//注册成功
                activity.handler.sendMessage(message);

            } else {
                Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

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

    private void insertSQL() {
        mClientDatabaseHelper.insertKeys("server_publicKey", Keys.SERVER_PUBLIC_KEY);
        mClientDatabaseHelper.insertKeys("client_publicKey", Keys.CLIENT_PUBLIC_KEY);
        mClientDatabaseHelper.insertKeys("client_privateKey", Keys.CLIENT_PRIVATE_KEY);
        mClientDatabaseHelper.insertKeys("user_id", user_id);
        mClientDatabaseHelper.insertKeys("user_name", name);
        mClientDatabaseHelper.insertKeys("password", password);

        Keys.USER_ID = user_id;
        Keys.USER_NAME = name;
        Keys.PASSWORD = password;
        Keys.REGISTER = true;
    }

}
