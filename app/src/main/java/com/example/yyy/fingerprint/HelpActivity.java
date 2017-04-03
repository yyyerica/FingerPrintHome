package com.example.yyy.fingerprint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window win = getWindow();
//            WindowManager.LayoutParams winParams = win.getAttributes();
//            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//            winParams.flags |= bits;
//            win.setAttributes(winParams);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(R.color.colorPrimary);
//        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.WHITE);

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        settoolbar();//工具栏



        //侧滑栏
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);//默认选中
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true); // 改变item选中状态

                int id = item.getItemId();

                if (id == R.id.home) {
                    Intent intent1 = new Intent(HelpActivity.this,MainActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (id == R.id.gerenziliao) {
                    Intent intent1 = new Intent(HelpActivity.this,PersonalDataActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (id == R.id.help) {

                } else if (id == R.id.wenjianguanli) {
                    Intent intent1 = new Intent(HelpActivity.this,FolderManageActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (id == R.id.kehuduanshezhi) {
                    Intent intent = new Intent(HelpActivity.this,ClientSettingActivity.class);
                    startActivity(intent);
                    finish();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        SharedPreferences userSettings= getSharedPreferences("settingid", 0);
        LinearLayout layout = (LinearLayout)navigationView.inflateHeaderView(R.layout.nav_header_main);
        if (userSettings!=null){
            String name = userSettings.getString("userid","默认值");
            //注意每个activity的navigationView是不一样的
            TextView headernametext = (TextView)layout.findViewById(R.id.headerNameText);
            headernametext.setText(name);
        }
        //取头像图片
        Bitmap bitmap = SharedPreferUtils.getBitmap(this, "pic", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        ImageView imageview = (ImageView)layout.findViewById(R.id.imageView);
        if(bitmap!=null)
            imageview.setImageBitmap(bitmap);
    }


    public void settoolbar(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.e_lock);//设置app logo


        toolbar.setTitleTextColor(Color.parseColor("#000000"));//设置标题颜色
        getSupportActionBar().setTitle("帮助");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

}
