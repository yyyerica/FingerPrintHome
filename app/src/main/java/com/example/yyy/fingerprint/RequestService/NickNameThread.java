package com.example.yyy.fingerprint.RequestService;

import android.app.Activity;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.yyy.fingerprint.ClientSettingActivity;
import com.example.yyy.fingerprint.FolderManage.GetAuthorityThread;
import com.example.yyy.fingerprint.LoginRegister.AddressUtil;
import com.example.yyy.fingerprint.LoginRegister.Keys;
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
 * Created by YYY on 2017/4/2.
 */

public class NickNameThread extends Thread {
    private String IMEI;
    private String user_id;
    private String guid;
    private String nickname;
    private String url = "";
    private ClientSettingActivity activity;

    public NickNameThread(String user_id, String IMEI, String guid, String nickname, String url, ClientSettingActivity activity) {
        this.user_id = user_id;
        this.IMEI = IMEI;
        this.guid = guid;
        this.nickname = nickname;
        this.url = url;
        this.activity = activity;
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

            if (!TextUtils.isEmpty(guid) && !TextUtils.isEmpty(nickname)) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                String source = "guid=" + guid
                        + "&nickname=" + nickname;
                source = PostThread.encrypt(source);
                String params = "model=" + PostOptions.SENDNICKNAME
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
            Log.d("response", response);
            String[] sourceStrArray = response.split("&");
            String status = sourceStrArray[0].split("=")[1];
            String content = sourceStrArray[1].split("=")[1];
            if(status.equals("OK")) {
                new GetAuthorityThread(Keys.USER_ID, Keys.IMEI, AddressUtil.LOGIN_URL, activity).start();
                Toast.makeText(activity,"修改成功",Toast.LENGTH_LONG);
            } else {
                Log.d("NickName", "失败");
                Toast.makeText(activity,"修改失败",Toast.LENGTH_LONG);
            }
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
