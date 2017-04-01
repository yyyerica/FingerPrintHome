package com.example.yyy.fingerprint.RequestService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootBroadcastReceiver extends BroadcastReceiver { //广播接收器

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        //构建了一个Intent对象去启动BootService这个服务

//        if (intent.getAction().equals(ACTION)) {
//            Intent service = new Intent(context, Main2Activity.class);
            Intent service = new Intent(context, BootService.class);
            service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(service);
//            Log.e("BootBroadcastReceiver","test");
//            Toast.makeText(context, "BootBroadcastReceiver", Toast.LENGTH_SHORT).show();
//        }
    }
}
