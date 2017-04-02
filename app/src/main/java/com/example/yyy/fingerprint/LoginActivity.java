package com.example.yyy.fingerprint;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yyy.fingerprint.LoginRegister.*;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;



import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private LoginThread mAuthTask = null;//UserLoginTask继承自AsyncTask

    // UI references.
    //AutoCompleteTextView在输入框中输入我们想要输入的信息就会出现其他与其相关的提示信息
    private AutoCompleteTextView mAccountView;
    private EditText mPasswordView;
    public View mProgressView;
    public View mLoginFormView;

    private ClientDatabaseHelper mClientDatabaseHelper;
    public static final int READ_PHONE_STATE = 10;

    LoginActivity loginactivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        loginactivity = this;

        mClientDatabaseHelper = ClientDatabaseHelper.getInstance(LoginActivity.this);
        initIMEI();
        initKeys();

        // Set up the login form.
        mAccountView = (AutoCompleteTextView) findViewById(R.id.AccountText);
        populateAutoComplete();//构造自动补全的列表

        mPasswordView = (EditText) findViewById(R.id.password);
        //onEditorAction在按下回车的时候执行
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        //用于获取显示的View，在登陆的时候可以进行登陆窗口gone，ProgressBar visible的操作。


        Button registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //进入注册界面按钮
                String url = AddressUtil.LOGIN_URL;

                new GetIdThread(Keys.IMEI, url).start();

                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    /**populateAutoComplete()构造自动补全的列表
    先是通过mayRequestContacts判断是否继续执行,
    若通过判断则初始化Loaders，通过Loaders后台异步读取用户的账户信息。*/
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }


    //用于请求用户以获取读取账户的权限，主要是为了适配6.0新的权限机制
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mAccountView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    /**
     * 初步判断输入的账户密码的合法性（是否为空，长度是否过小）,并给出错误提示。
     * 通过初步检验后，隐藏登陆框和按钮，显示进度条，
     * 并在AsyncTask中进行后台登陆，
     * 这个AsyncTask就是上面的变量中的mAuthTask，
     * 使用的时候改写doInBackground方法实现自己的业务逻辑
     */
    public void attemptLogin() {
        if (mAuthTask != null) { //初始化为null
            return;
        }


        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String account = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;//取消登陆
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("密码长度不足！");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(account)) {
            mAccountView.setError("账号为空！");
            focusView = mAccountView;
            cancel = true;
        }
//        else if (!isEmailValid(account)) {
//            mAccountView.setError("账号格式不正确");
//            focusView = mAccountView;
//            cancel = true;
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            String url = AddressUtil.LOGIN_URL;
            String id = Keys.USER_ID;
            mAuthTask = new LoginThread(id, account, password, Keys.IMEI, url, loginactivity);

            //三个被注释的地方！！！
            //mAuthTask = new LoginThread(id, "yyy", "yyyyy", Keys.IMEI, url, loginactivity);
            mAuthTask.start();
        }
    }

//    private boolean isEmailValid(String email) {
//        //TODO: Replace this with your own logic
//        return email.contains("@");
//    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        TextView logintext = (TextView)findViewById(R.id.loginText);
        logintext.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mAccountView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }



    public void initIMEI() {
        if (!mClientDatabaseHelper.isFindKeys("IMEI")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE);
            } else {
                TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                Keys.IMEI = TelephonyMgr.getDeviceId();
                mClientDatabaseHelper.insertKeys("IMEI", Keys.IMEI);
            }
        } else {
            Keys.IMEI = mClientDatabaseHelper.getValue("IMEI");
        }

    }

    public void initKeys() {
        if (mClientDatabaseHelper.isFindKeys("server_publicKey")) {
            Keys.SERVER_PUBLIC_KEY = mClientDatabaseHelper.getValue("server_publicKey");
            Keys.CLIENT_PUBLIC_KEY = mClientDatabaseHelper.getValue("client_publicKey");
            Keys.CLIENT_PRIVATE_KEY = mClientDatabaseHelper.getValue("client_privateKey");
            Keys.USER_ID = mClientDatabaseHelper.getValue("user_id");
            Keys.USER_NAME = mClientDatabaseHelper.getValue("user_name");
            Keys.PASSWORD = mClientDatabaseHelper.getValue("password");
            Keys.REGISTER = true;
        }

        SharedPreferences userSettings = getSharedPreferences("isLoginsetting", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putBoolean("isLogin",true);
        editor.commit();
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }

        if (requestCode == READ_PHONE_STATE && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            Keys.IMEI = TelephonyMgr.getDeviceId();
            mClientDatabaseHelper.insertKeys("IMEI", Keys.IMEI);

        }
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1: //登陆成功
                    //Toast.makeText(activity, "TOAST", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtra("userID",mAccountView.getText());

                    SharedPreferences useridSettings = getSharedPreferences("settingid", 0);
                    SharedPreferences.Editor editor = useridSettings.edit();
                    editor.putString("userid",mAccountView.getText().toString());
                    editor.commit();

                    startActivity(intent);
                    finish();
                    break;

                case 0: //登录失败
                    showProgress(false);
                    mAuthTask = null;
                    EditText mPasswordView = (EditText)findViewById(R.id.password);
                    mPasswordView.setError("账号或密码不符！");
                    mPasswordView.requestFocus();
                    break;
            }
        }
    };

}

