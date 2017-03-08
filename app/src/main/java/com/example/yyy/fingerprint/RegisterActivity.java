package com.example.yyy.fingerprint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yyy.fingerprint.LoginRegister.AddressUtil;
import com.example.yyy.fingerprint.LoginRegister.GetIdThread;
import com.example.yyy.fingerprint.LoginRegister.Keys;
import com.example.yyy.fingerprint.LoginRegister.RegisterThread;

public class RegisterActivity extends AppCompatActivity {

    Button registerButton;
    AutoCompleteTextView mAccountView;
    EditText mPasswordView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);

        registerButton = (Button)findViewById(R.id.registerButton);
        mAccountView = (AutoCompleteTextView) findViewById(R.id.AccountText);
        mPasswordView = (EditText) findViewById(R.id.password);

        while(Keys.CLIENT_PUBLIC_KEY.equals("")) ;

        //注册按钮
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = AddressUtil.LOGIN_URL;
                String id = Keys.USER_ID;
                String name = mAccountView.getText().toString().trim();
                String password = mPasswordView.getText().toString().trim();
                if (!Keys.REGISTER) {
                    new RegisterThread(id, name, password, Keys.IMEI, url, RegisterActivity.this).start();
                } else {
                    Toast.makeText(RegisterActivity.this, "已经注册过了", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1: //注册成功
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                    intent.putExtra("userID",mAccountView.getText());

                    SharedPreferences useridSettings = getSharedPreferences("settingid", 0);
                    SharedPreferences.Editor editor = useridSettings.edit();
                    editor.putString("userid",mAccountView.getText().toString());
                    editor.commit();

                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };


}
