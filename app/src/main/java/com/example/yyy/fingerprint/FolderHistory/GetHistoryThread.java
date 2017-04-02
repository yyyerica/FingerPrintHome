package com.example.yyy.fingerprint.FolderHistory;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.yyy.fingerprint.CalendarFragment;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/2/25.
 */

public class GetHistoryThread extends Thread {

    private String IMEI;
    private String user_id;
    private String url = "";
    private CalendarFragment fragment;
    private String date;

    public GetHistoryThread(String user_id, String IMEI, String date, String url, CalendarFragment fragment) {
        this.user_id = user_id;
        this.IMEI = IMEI;
        this.date = date;
        this.url = url;
        this.fragment = fragment;
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
            String source = "date=" + date;
            source = PostThread.encrypt(source);
            String params = "model=" + PostOptions.GETHISTORY
                    + "&user_id=" + URLEncoder.encode(user_id, "utf-8")
                    + "&IMEI=" + URLEncoder.encode(IMEI, "utf-8")
                    + source;
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
            List<History> histories = new ArrayList<>();
            String[] sourceStrArray = response.split("&");
            Log.d("length", sourceStrArray.length + "");
            if (sourceStrArray.length > 1) {
                for (int i = 0; i < sourceStrArray.length; i += 7) {
                    String guid = sourceStrArray[i].split("=")[1];
                    Log.d("guid", guid);
                    String file_path = sourceStrArray[i + 1].split("=")[1];
                    Log.d("file_path", file_path);
                    String nickname = sourceStrArray[i + 2].split("=")[1];
                    Log.d("nickname", nickname);
                    String authority_number = sourceStrArray[i + 3].split("=")[1];
                    Log.d("authority_number", authority_number);
                    String operate_time = sourceStrArray[i + 4].split("=")[1];
                    Log.d("operate_time", operate_time);
                    String isPermit = sourceStrArray[i + 5].split("=")[1];
                    Log.d("isPermit", isPermit);
                    String isCheck = sourceStrArray[i + 6].split("=")[1];
                    Log.d("isCheck", isCheck);
                    History history = new History(guid, file_path, nickname, authority_number, operate_time, isPermit, isCheck);
                    histories.add(history);
                }
            }

            Message message = new Message();
            message.arg1 = 1;
            message.obj = histories;
            fragment.handler.sendMessage(message);

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
