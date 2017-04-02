package com.example.yyy.fingerprint.RequestService;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.yyy.fingerprint.ClientSettingActivity;
import com.example.yyy.fingerprint.FolderManageActivity;
import com.example.yyy.fingerprint.MainActivity;
import com.example.yyy.fingerprint.PersonalDataActivity;
import com.example.yyy.fingerprint.LoginRegister.PostOptions;
import com.example.yyy.fingerprint.LoginRegister.PostThread;
import com.example.yyy.fingerprint.MainlistFragment;
import com.example.yyy.fingerprint.R;

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

import static android.content.Context.NOTIFICATION_SERVICE;


public class SynchroThread extends Thread {
    private String IMEI;
    private String user_id;
    private String url = "";
    private MainlistFragment mainlistfragment;
    private PersonalDataActivity personalDataActivity;
    private ClientSettingActivity clientSettingActivity;
    private FolderManageActivity folderManageActivity;
    private BootService bootService;

//Mainactivity将context设为mainlistfragment,作为bootservice启动该线程的参数，所以由该构造函数启动的线程能让mainlistfragment更改list信息
    public SynchroThread(String user_id, String IMEI, String url, MainlistFragment mainlistfragment) {
        this.user_id = user_id;
        this.IMEI = IMEI;
        this.url = url;
        this.mainlistfragment = mainlistfragment;
    }

    public SynchroThread(String user_id, String IMEI, String url, MainlistFragment mainlistfragment,BootService bootService) {
        this.user_id = user_id;
        this.IMEI = IMEI;
        this.url = url;
        this.mainlistfragment = mainlistfragment;
        this.bootService = bootService;

        Log.e("SynchroThread","bootservice.create()");
    }


    public SynchroThread(String user_id, String IMEI, String url, PersonalDataActivity personalDataActivity) {
        this.user_id = user_id;
        this.IMEI = IMEI;
        this.url = url;
        this.personalDataActivity = personalDataActivity;
    }

    public SynchroThread(String user_id, String IMEI, String url, ClientSettingActivity clientSettingActivity) {
        this.user_id = user_id;
        this.IMEI = IMEI;
        this.url = url;
        this.clientSettingActivity = clientSettingActivity;
    }

    public SynchroThread(String user_id, String IMEI, String url, FolderManageActivity folderManageActivity) {
        this.user_id = user_id;
        this.IMEI = IMEI;
        this.url = url;
        this.folderManageActivity = folderManageActivity;
    }

    @Override
    public void run() {
        super.run();
        getRun();
    }

    private void getRun() {

        if (TextUtils.isEmpty(url)) {
            Log.e("SynchroURL","EMPTY");
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
            String params = "model=" + PostOptions.SYNCHRO
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
            List<Synchro> synchros = new ArrayList<>();
            String[] sourceStrArray = response.split("&");
            Log.d("length", sourceStrArray.length+"");
            boolean isSendbool = true;
            if(sourceStrArray.length > 1) {
                for (int i = 0; i < sourceStrArray.length; i+=7) {
                    String guid = sourceStrArray[i].split("=")[1];
                    Log.d("guid", guid);
                    String file_path = sourceStrArray[i+1].split("=")[1];
                    Log.d("file_path", file_path);
                    String authority_number = sourceStrArray[i+2].split("=")[1];
                    Log.d("authority_number", authority_number);
                    String operate_date = sourceStrArray[i+3].split("=")[1];
                    Log.d("operate_date", operate_date);
                    String operate_time = sourceStrArray[i+4].split("=")[1];
                    Log.d("operate_time", operate_time);
                    String isPermit = sourceStrArray[i+5].split("=")[1];
                    Log.d("isPermit", isPermit);
                    String isSend = sourceStrArray[i+6].split("=")[1];
                    Log.d("isSend", isSend);

                    Synchro synchro = new Synchro(guid, file_path, authority_number, operate_date, operate_time, isPermit,isSend);
                    synchros.add(synchro);
                    if (isSend.equals("NO")) {
                        isSendbool = false;
                    }
                }

            }

            Message message = new Message();
            message.arg1 = 1;
            message.obj = synchros;
//            if (personalDataActivity!=null)
//                personalDataActivity.handler.sendMessage(message);
            if(mainlistfragment!=null){
                mainlistfragment.handler.sendMessage(message);
                Log.e("SynchroThread","mainlistfragment!=null");
            }
            else if (clientSettingActivity!=null)
                clientSettingActivity.handler.sendMessage(message);
            else if(folderManageActivity!=null)
                folderManageActivity.handler.sendMessage(message);


            Log.e("SynchroThread","isSendbool"+isSendbool+"");
            Log.e("SynchroThread","synchros.size()"+synchros.size()+"");
            boolean verify = bootService!=null && synchros.size()>0 && !isSendbool;
            Log.e("SynchroThread","verify"+verify);
            if(bootService!=null && synchros.size()>0 && !isSendbool) {
                bootService.handler.sendMessage(message);
                Log.e("SynchroThread","bootService");
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
