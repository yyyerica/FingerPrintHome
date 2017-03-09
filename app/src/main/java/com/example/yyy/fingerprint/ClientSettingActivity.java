package com.example.yyy.fingerprint;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yyy.fingerprint.LoginRegister.AddressUtil;
import com.example.yyy.fingerprint.LoginRegister.Keys;
import com.example.yyy.fingerprint.LunxunService.Synchro;
import com.example.yyy.fingerprint.LunxunService.SynchroThread;

import java.util.ArrayList;
import java.util.List;

public class ClientSettingActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    ListView listview;
    ClientSettingAdapter arrayAdapter;
//    String[] arr1 = {"dota","c://bbb"};
//    String[] arr2 = {"lol","d:ccc"};
//    ArrayList<String[]> strs = new ArrayList<String[]>(){{add(arr1);add(arr1);add(arr1);add(arr2);add(arr1);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);}};
    ArrayList<String> strs = new ArrayList<String>(){};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_kehuduanshezhi);

        //状态栏颜色
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
        new SynchroThread(Keys.USER_ID, Keys.IMEI, AddressUtil.LOGIN_URL, ClientSettingActivity.this).start();

        //侧滑栏
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(3).getSubMenu().getItem(1).setChecked(true);//默认选中
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true); // 改变item选中状态

                int id = item.getItemId();

                if (id == R.id.home) {
                    Intent intent = new Intent(ClientSettingActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.gerenziliao) {
                    Intent intent = new Intent(ClientSettingActivity.this,PersonalDataActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.help) {
                    Intent intent1 = new Intent(ClientSettingActivity.this,HelpActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (id == R.id.wenjianguanli) {
                    Intent intent = new Intent(ClientSettingActivity.this,FolderManageActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.kehuduanshezhi) {

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
        //取图片
        Bitmap bitmap = SharedPreferUtils.getBitmap(this, "pic", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        ImageView imageview = (ImageView)layout.findViewById(R.id.imageView);
        if(bitmap!=null)
            imageview.setImageBitmap(bitmap);

        arrayAdapter = new ClientSettingAdapter(this,R.layout.kehuduanshezhi_item,strs);
        listview = (ListView)findViewById(R.id.kehuduanshezhiListView);
        listview.setAdapter(arrayAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:
//                        new AlertDialog.Builder(ClientSettingActivity.this).setTitle("更改用户名").
//                                setIcon(android.R.drawable.ic_dialog_info).
//                                setView(new EditText(ClientSettingActivity.this)).setPositiveButton("确定", null).
//                                setNegativeButton("取消", null).show();
//                        break;
//                    case 1:
//                        Intent intent = new Intent(ClientSettingActivity.this,ClientSettingActivity.class);
//                        startActivity(intent);
//                        break;
                AlertDialog.Builder builder = new AlertDialog.Builder(ClientSettingActivity.this,4);
                builder.setTitle("更改客户端名称").
                            setIcon(android.R.drawable.ic_dialog_info).
                            setView(new EditText(ClientSettingActivity.this)).setPositiveButton("确定", null).
                            setNegativeButton("取消", null).show();

//                }
            }
        });


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addcomputer);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(ClientSettingActivity.this,4);//传统主题
//
//                builder.setTitle("添加客户端").
//                        setIcon(android.R.drawable.ic_dialog_info).
//                        setView(new EditText(ClientSettingActivity.this)).setPositiveButton("确定", null).
//                        setNegativeButton("取消", null).show();
//            }
//        });

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar toolbar2 = (Toolbar)findViewById(R.id.toolbar2);
        //Toolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        if (toolbar2 != null) {
            toolbar2.setTitle("所管理客户端");
            //toolbar.setSubtitle("微信安全支付");
        }
    }


    public void settoolbar(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.key);//设置app logo

        toolbar.setTitleTextColor(Color.parseColor("#000000"));//设置标题颜色
        getSupportActionBar().setTitle("客户端设置");

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

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1: //从数据库拿到全体文件列表
                    List<Synchro> synchros = (List<Synchro>) msg.obj;
                    for(int i = 0;i < synchros.size();i++) {
                        Synchro synchros1 = synchros.get(i);
                        strs.add(synchros1.getGuid());
                    }

                    arrayAdapter.notifyDataSetChanged();

                    break;
            }

        }
    };


}
