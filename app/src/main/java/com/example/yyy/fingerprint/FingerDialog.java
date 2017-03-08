package com.example.yyy.fingerprint;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FingerDialog extends DialogFragment  { //implements TextView.OnEditorActionListener
    private Button mCancelButton;
    private Button mSecondDialogButton;
    private View mFingerprintContent;
//    private View mBackupContent;
//    private EditText mPassword;
//    private CheckBox mUseFingerprintFutureCheckBox;
//    private TextView mPasswordDescriptionTextView;
//    private TextView mNewFingerprintEnrolledTextView;
    private ImageView mIcon;
    private TextView textView;


    FingerprintManager manager;
    KeyguardManager mKeyManager;
    private final static int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 0;
    private final static String TAG = "finger_log";

    private static final long ERROR_TIMEOUT_MILLIS = 1600;
    private static final long SUCCESS_DELAY_MILLIS = 1300;

    public int position = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt("position");

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);

        manager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isFinger()) {
                //Toast.makeText(context, "请进行指纹识别", Toast.LENGTH_LONG).show();
                startListening(null);
            }
        } else Toast.makeText(getActivity(), "请将系统升级至安卓6.0以上版本", Toast.LENGTH_SHORT).show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)  {
        getDialog().setTitle("请进行指纹认证");
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View v = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mSecondDialogButton = (Button) v.findViewById(R.id.second_dialog_button);
        mSecondDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAuthenticationScreen();//跳转到手势密码校验界面
            }
        });

        mFingerprintContent = v.findViewById(R.id.fingerprint_container);
//        mBackupContent = v.findViewById(R.id.backup_container);
//        mPassword = (EditText) v.findViewById(R.id.password);
//        mPassword.setOnEditorActionListener(this);
//        mPasswordDescriptionTextView = (TextView) v.findViewById(R.id.password_description);
//        mUseFingerprintFutureCheckBox = (CheckBox)
//                v.findViewById(R.id.use_fingerprint_in_future_check);
//        mNewFingerprintEnrolledTextView = (TextView)
//                v.findViewById(R.id.new_fingerprint_enrolled_description);
        mIcon = (ImageView)v.findViewById(R.id.fingerprint_icon);
        textView = (TextView)v.findViewById(R.id.fingerprint_status);
        return v;
    }

//    @Override
//    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//        return false;
//    }

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
        //Log(TAG, "已开启锁屏密码");

        //判断是否有指纹录入
        if (!manager.hasEnrolledFingerprints()) {
            Toast.makeText(getActivity(), "没有录入指纹", Toast.LENGTH_SHORT).show();
            return false;
        }
       // Log(TAG, "已录入指纹");

        return true;
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();


    //使用callback定义回调：(mSelfCancelled为authenticate()方法中的一个参数)
    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
            if(getActivity()!=null) {
                Toast.makeText(getActivity(), errString, Toast.LENGTH_SHORT).show();
            }
            //showAuthenticationScreen();//跳转到手势密码校验界面
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            if(getActivity()!=null) {
                Toast.makeText(getActivity(), helpString, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            if(getActivity()!=null) {
                Toast.makeText(getActivity(), "指纹识别成功", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                if(position!=-1){
                    intent.putExtra("position",position);
                }

                Fragment fragment = getTargetFragment();
                fragment.onActivityResult(MainlistFragment.REUEST_CODE, Activity.RESULT_OK, intent);//传参数给回调
                dismiss();
            }
        }

        @Override
        public void onAuthenticationFailed() {
            if(getActivity()!=null){
                Toast.makeText(getActivity(), "指纹识别失败", Toast.LENGTH_SHORT).show();
                Fragment fragment = getTargetFragment();
                fragment.onActivityResult(MainlistFragment.REUEST_CODE,Activity.RESULT_CANCELED,null);//传参数给回调
                showError(mIcon.getResources().getString(R.string.fingerprint_not_recognized));
            }
        }
    };

    private void showError(CharSequence error) {
        mIcon.setImageResource(R.drawable.ic_fingerprint_error);
        textView.setText(error);
        textView.setTextColor(
                textView.getResources().getColor(R.color.warning_color, null));
        textView.removeCallbacks(mResetErrorTextRunnable);
        textView.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }

    private Runnable mResetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            textView.setTextColor(
                    textView.getResources().getColor(R.color.hint_color, null));
            textView.setText(
                    textView.getResources().getString(R.string.fingerprint_hint));
            mIcon.setImageResource(R.mipmap.ic_fp_40px);
        }
    };

    //使用startListening()打开指纹传感器：
    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            return;
        }
        manager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);
    }

    /**
     * 锁屏密码，跳转到手势密码校验界面
     * 在Activity中唤起系统认证页面
     *
     * Confirm Credential 核心是提供了一个系统的解锁界面Activity，
     使用的时候首先用mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);方法获取到一个Intent，
     然后 startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS)的方式拉起Activity，
     解锁是否成功则在onActivityResult回调中体现。
     这个系统的解锁界面类似锁屏界面，可以用锁屏密码或者指纹解锁。
     */
    private void showAuthenticationScreen() {
        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger", "测试指纹识别");
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
            //startActivityForResult是Activity中启动活动的方法，该方法期望在活动销毁的时候能够返回一个结果给上一个活动
            //第二个参数是请求码，用于在之后的回调中判断数据的来源
        }
    }

    //在onActivityResult()中处理回调：
    //在SecondActivity被销毁之后会回调上一个活动的该方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            // Challenge completed, proceed with using cipher
            if (resultCode == -1) {//resultCode 由第二个Activity的setResult方法设置
                Toast.makeText(getActivity(), "识别成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                if(position!=-1){
                    intent.putExtra("position",position);
                }

                Fragment fragment = getTargetFragment();
                fragment.onActivityResult(MainlistFragment.REUEST_CODE, Activity.RESULT_OK, intent);//传参数给回调
                dismiss();
            } else {
                Toast.makeText(getActivity(), "识别失败", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
