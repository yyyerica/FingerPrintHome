package com.example.yyy.fingerprint;

import android.Manifest;
import android.app.Activity;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.yyy.fingerprint.LoginRegister.AddressUtil;
import com.example.yyy.fingerprint.LoginRegister.Keys;
import com.example.yyy.fingerprint.RequestService.Synchro;
import com.example.yyy.fingerprint.RequestService.SynchroThread;
import com.example.yyy.fingerprint.RequestService.VerifyThread;

import java.util.ArrayList;
import java.util.List;



public class MainlistFragment extends Fragment implements SlideCutListView.RemoveListener {

    FingerprintManager manager;
    KeyguardManager mKeyManager;
    Fragment me;

//    String[] arr1 = {"2-17","dota","16:45","guid"};
//    String[] arr2 = {"2-14","lol","23:09","guid"};
//    ArrayList<String[]> strs = new ArrayList<String[]>(){{add(arr1); add(arr1); add(arr1); add(arr1); add(arr1); add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);}};
    ArrayList<String[]> strs = new ArrayList<String[]>(){};
    ArrayList<String[]> deletestrs = new ArrayList<String[]>(){};

    private SlideCutListView lv;
    FrameLayout frameLayout;
    MainListAdapter arrayAdapter;

    List<Synchro> synchrosList;


    private final static int NOTIFICATION_ID = 0x0001;



    public static final int REUEST_CODE = 10;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayAdapter = new MainListAdapter(getActivity(),R.layout.mainarray_item,strs);
        //arrayAdapter.setDropDownViewResource();
        me = this;


        new SynchroThread(Keys.USER_ID, Keys.IMEI, AddressUtil.LOGIN_URL,MainlistFragment.this).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_bar_main, null);

        frameLayout = (FrameLayout) view.findViewById(R.id.framelayout);

        lv = (SlideCutListView) view.findViewById(R.id.listview);
        lv.setRemoveListener(this);
        lv.setAdapter(arrayAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isFinger()) {
                        //Toast.makeText(MainActivity.this, "请进行指纹识别", Toast.LENGTH_LONG).show();
                        FingerDialog fragment
                                = new FingerDialog();
                        fragment.setTargetFragment(me,REUEST_CODE);

                        Bundle args = new Bundle();
                        args.putInt("position", position);
                        fragment.setArguments(args);

                        fragment.show(getFragmentManager(),"yyy");

//                        arrayAdapter.notifyDataSetChanged();//更新
                    }
                }
            });
        } else Toast.makeText(getActivity(), "请将系统升级至安卓6.0以上版本", Toast.LENGTH_SHORT).show();


        manager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);



        return view;
    }

    //使用前先判断是否硬件支持，是否有指纹等：
    public boolean isFinger() {

        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            return false;
        }
        //Log(TAG, "有指纹权限");

        //判断硬件是否支持指纹识别
        if (!manager.isHardwareDetected()) {
            Toast.makeText(getActivity(), "没有指纹识别模块", Toast.LENGTH_SHORT).show();
            return false;
        }
        //Log(TAG, "有指纹模块");

        //判断用户是否开启锁屏密码
        if (!mKeyManager.isKeyguardSecure()) {
            Toast.makeText(getActivity(), "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Log(TAG, "已开启锁屏密码");

        //判断是否有指纹录入
        if (!manager.hasEnrolledFingerprints()) {
            Toast.makeText(getActivity(), "没有录入指纹", Toast.LENGTH_SHORT).show();
            return false;
        }
        //Log(TAG, "已录入指纹");

        return true;
    }


    void refrash(int position) {
        Synchro synchro = synchrosList.get(position);
        synchro.setIsPermit("YES");

        if (synchrosList!=null)
            new VerifyThread(Keys.USER_ID,Keys.IMEI,synchro,AddressUtil.LOGIN_URL,getActivity()).start();

        Toast.makeText(getActivity(),"refresh",Toast.LENGTH_SHORT);

        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REUEST_CODE & resultCode== Activity.RESULT_OK) {
            int position = data.getIntExtra("position",-1);
            refrash(position);
        }

    }



    public Handler handler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.arg1) {
            case 1:
                strs.clear();
                List<Synchro> synchros = (List<Synchro>) msg.obj;
                synchrosList = synchros;
                boolean isSend = true;
                for(int i = 0 ; i < synchros.size(); i++) {
                    Synchro synchros1 = synchros.get(i);
//                    if(synchros1.getAuthority_number()=="1")//打开
                        strs.add(new String[]{synchros1.getOperate_date(),synchros1.getFile_path(),synchros1.getOperate_time(),synchros1.getGuid()});
//                    else if(synchros1.getAuthority_number()=="0")//删除
//                        deletestrs.add(new String[]{synchros1.getOperate_date(),synchros1.getFile_path(),synchros1.getOperate_time(),synchros1.getGuid()});
                    if (synchros1.getIsSend().equals("NO"))
                        isSend = false;
                }
                if(synchros.size()>0&&!isSend) {
                        //推送
//                    Intent intent;
//                    PendingIntent pi;
//                    intent = new Intent(getActivity(),MainActivity.class);
//                    pi = PendingIntent.getActivity(getActivity(),0,intent,0);
//                    NotificationManager myNotificationManager;
//                        myNotificationManager = (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);
//                    Notification notification;
//                        notification = new Notification.Builder(getActivity()).
//                                setAutoCancel(true).//设置打开该通知，该通知自动消失
//                                setTicker("e-Locker").//设置显示在状态栏的通知提示消息
//                                setSmallIcon(R.drawable.horn).//设置通知的图标
//                                setContentTitle("你有新的请求消息").//设置通知内容的标题
//                                setContentText("点击查看").//设置通知内容
//                                setContentIntent(pi).//设置通知将要启动程序的Intent
//                                build();
//                        myNotificationManager.notify(NOTIFICATION_ID,notification);
                }
                Log.e("MainlistFragment","访问中");

//                    View view = inflater.inflate(R.layout.app_bar_main, null);
//                    lv = (ListView)view.findViewById(R.id.listview);
                lv.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();

                if(strs.size()==0) {
                    frameLayout.setVisibility(View.VISIBLE);
                } else frameLayout.setVisibility(View.GONE);

                break;
        }
        }
    };


    //滑动删除之后的回调方法
    @Override
    public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
        arrayAdapter.remove(arrayAdapter.getItem(position));

        Synchro synchro = synchrosList.get(position);
        synchro.setIsPermit("NO");

        new VerifyThread(Keys.USER_ID,Keys.IMEI,synchro,AddressUtil.LOGIN_URL,getActivity()).start();

//        switch (direction) {
//            case RIGHT:
//                Toast.makeText(getActivity(), "向右删除  "+ position, Toast.LENGTH_SHORT).show();
//                break;
//            case LEFT:
//                Toast.makeText(getActivity(), "向左删除  "+ position, Toast.LENGTH_SHORT).show();
//                break;
//
//            default:
//                break;
//        }

    }


}
