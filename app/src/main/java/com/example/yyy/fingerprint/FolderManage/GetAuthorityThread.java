package com.example.yyy.fingerprint.FolderManage;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.yyy.fingerprint.LoginRegister.ClientDatabaseHelper;
import com.example.yyy.fingerprint.LoginRegister.PostOptions;
import com.example.yyy.fingerprint.LoginRegister.PostThread;
import com.example.yyy.fingerprint.FolderManageActivity;
import com.example.yyy.fingerprint.ClientSettingActivity;
import com.example.yyy.fingerprint.ThirdFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/2/25.
 */

public class GetAuthorityThread extends Thread {

    private String IMEI;
    private String user_id;
    private String url = "";
    private FolderManageActivity folderManageActivity;
    private ClientSettingActivity ClientSettingActivity;
    private ThirdFragment fragment;
    private ClientDatabaseHelper mClientDatabaseHelper;

    public GetAuthorityThread(String user_id, String IMEI, String url, FolderManageActivity activity) {
        this.user_id = user_id;
        this.IMEI = IMEI;
        this.url = url;
        Log.d("user_id", user_id);
        Log.d("IMEI", IMEI);
        Log.d("url", url);
        this.folderManageActivity = activity;
        mClientDatabaseHelper = ClientDatabaseHelper.getInstance(activity);
    }

    public GetAuthorityThread(String user_id, String IMEI, String url, ThirdFragment fragment) {
        this.user_id = user_id;
        this.IMEI = IMEI;
        this.url = url;
        Log.d("user_id", user_id);
        Log.d("IMEI", IMEI);
        Log.d("url", url);
        this.fragment = fragment;
        mClientDatabaseHelper = ClientDatabaseHelper.getInstance(fragment.getContext());
    }

    public GetAuthorityThread(String user_id, String IMEI, String url, ClientSettingActivity activity) {
        this.user_id = user_id;
        this.IMEI = IMEI;
        this.url = url;
        Log.d("user_id", user_id);
        Log.d("IMEI", IMEI);
        Log.d("url", url);
        this.ClientSettingActivity = activity;
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

            OutputStream outputStream = httpURLConnection.getOutputStream();
            String params = "model=" + PostOptions.GETAUTHORITY
                    + "&user_id=" + URLEncoder.encode(user_id, "utf-8")
                    + "&IMEI=" + URLEncoder.encode(IMEI, "utf-8");
            outputStream.write(params.getBytes());

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
            String[] sourceStrArray = response.split("&");
            Log.d("length", sourceStrArray.length + "");
            List<Authority> authorities = new ArrayList<>();
            if (sourceStrArray.length > 1) {
                Log.d("GetAuthorityThread", "GetAuthorityThread");
                for (int i = 0; i < sourceStrArray.length; i += 4) {
                    String guid = sourceStrArray[i].split("=")[1];
                    Log.d("guid", guid);
                    String file_path = sourceStrArray[i + 1].split("=")[1];
                    Log.d("file_path", file_path);
                    String nickname = sourceStrArray[i + 2].split("=")[1];
                    Log.d("nickname", nickname);
                    String authority_number = sourceStrArray[i + 3].split("=")[1];
                    Log.d("authority_number", authority_number);
                    Authority authority = new Authority(guid, file_path, nickname, authority_number);
                    authorities.add(authority);
                    //mClientDatabaseHelper.insertAuthority(authority);
                }
            }

            Message message = new Message();
            message.arg1 = 1;
            message.obj = authorities;
            if (folderManageActivity != null)
                folderManageActivity.handler.sendMessage(message);
            else if (fragment != null)
                fragment.handler.sendMessage(message);
            else if (ClientSettingActivity != null)
                ClientSettingActivity.handler.sendMessage(message);


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
