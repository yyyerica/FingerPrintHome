package com.example.yyy.fingerprint;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yyy.fingerprint.LunxunService.BootService;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
// implements Parcelable

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    ViewPager myViewPager;

    MyFragmentAdapter myFragmentAdapter;

    LinearLayout backgroundlayout,top1,top2,top3,topmenu;

    ImageView imagetop1,imagetop2,imagetop3;

    FragmentTransaction fragmentTransaction;
    FragmentManager  fragmentManager = getSupportFragmentManager();

    String mainlistfragmentTag = "MainListFragmentTag";

    private BootService.MyBinder myBinder;
    //ServiceConnection匿名类

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) { //活动成功绑定时调用
            myBinder = (BootService.MyBinder) service;
            //有了该实例，可以再活动中根据具体的场景来调用myBinder中的任何public方法
            //即实现了指挥服务干什么，服务就干什么的功能
            BootService mService = myBinder.getService();

            mService.setContext((MainlistFragment)fragmentManager.findFragmentByTag("android:switcher:" + myViewPager.getId() + ":0"));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { //活动解除绑定时调用

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

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


        myViewPager = (ViewPager) new ViewPager(this);

        imagetop1 = (ImageView)findViewById(R.id.topimage1);
        imagetop2 = (ImageView)findViewById(R.id.topimage2);
        imagetop3 = (ImageView)findViewById(R.id.topimage3);
        top1 = (LinearLayout)findViewById(R.id.toplayout1);
        top2 = (LinearLayout)findViewById(R.id.toplayout2);
        top3 = (LinearLayout)findViewById(R.id.toplayout3);
        top1.setOnClickListener(this);
        top2.setOnClickListener(this);
        top3.setOnClickListener(this);
        topmenu = (LinearLayout)findViewById(R.id.topmenu);

        //ViewPager的过渡动画
        myViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                page.setRotationY(position * -30);
            }
        });


        //设定适配器
        myViewPager.setId("bb".hashCode());
        backgroundlayout = (LinearLayout)findViewById(R.id.backgroundlayout);
        backgroundlayout.addView(myViewPager);

        setviewPager();//滑动页面
        settoolbar();//工具栏
        colorTopMenu();


        // ViewPager滑动监听器
        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                colorTopMenu();
//                setBackgroundcolor();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //侧滑栏
        navigationView = (NavigationView) findViewById(R.id.mainnav_view);
        navigationView.getMenu().getItem(0).setChecked(true);//默认选中
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true); // 改变item选中状态

                int id = item.getItemId();

                if (id == R.id.home) {

                } else if (id == R.id.gerenziliao) {
                    Intent intent = new Intent(MainActivity.this,PersonalDataActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.help) {
                    Intent intent1 = new Intent(MainActivity.this,HelpActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (id == R.id.wenjianguanli) {
                    Intent intent = new Intent(MainActivity.this,FolderManageActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.kehuduanshezhi) {
                    Intent intent = new Intent(MainActivity.this,ClientSettingActivity.class);
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
        //取图片
        Bitmap bitmap = SharedPreferUtils.getBitmap(this, "pic", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        ImageView imageview = (ImageView)layout.findViewById(R.id.imageView);
        if(bitmap!=null)
            imageview.setImageBitmap(bitmap);

        //开启轮询服务
        Intent startIntent = new Intent(this,BootService.class);
        this.bindService(startIntent, connection, BIND_AUTO_CREATE);
        //绑定服务，BIND_AUTO_CREATE表示在活动和服务进行绑定后自动创建服务，使服务中的onCreate()方法得到执行，但onStartCommand()方法不会执行
        this.startService(startIntent);

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //不设菜单
//        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void settoolbar(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //声明后，用setSupportActionBar 设定，Toolbar即能取代原本的 actionbar 了
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.key);//设置app logo
//        getSupportActionBar().setTitle("setTitle");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#000000"));//设置标题颜色
//        toolbar.setTitleTextAppearance(this,"30sp");//修改标题的字体大小
        //getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        toolbar.inflateMenu(R.menu.base_toolbar_menu);//设置右上角的填充菜单
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_search) {
                    Toast.makeText(MainActivity.this , "点击了搜索按钮" , Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

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

    public void setviewPager() {


//        fragmentTransaction = fragmentManager
//                .beginTransaction();
        //构造适配器
        List<Fragment> fragmentList = new ArrayList<Fragment>();

        MainlistFragment mainlistFragment = new MainlistFragment();
//        mainlistFragment.setTargetFragment(mainlistFragment,0);
        //Fragment.setTargetFragment ，这个方法，一般就是用于当前fragment由别的fragment启动，在完成操作后返回数据
//        fragmentTransaction.add(mainlistFragment,mainlistfragmentTag).commit();
        fragmentList.add(mainlistFragment);
        fragmentList.add(new CalendarFragment());
        fragmentList.add(new ThirdFragment());

        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(),fragmentList);
        myViewPager.setAdapter(myFragmentAdapter);

    }

    public void colorTopMenu() {
        switch (myViewPager.getCurrentItem()) {
            case 0:

                imagetop1.setImageResource(R.drawable.homepressed);
                imagetop2.setImageResource(R.drawable.bellunpressed);
                imagetop3.setImageResource(R.drawable.messageunpressed);
//                top1.setBackgroundResource(R.drawable.bg_tab_selector);
//                top2.setBackgroundColor(Color.parseColor("#ff888888"));
//                top3.setBackgroundColor(Color.parseColor("#ff888888"));
                getSupportActionBar().setTitle("请求消息");
                break;
            case 1:

                imagetop1.setImageResource(R.drawable.homeunpressed);
                imagetop2.setImageResource(R.drawable.bellpressed);
                imagetop3.setImageResource(R.drawable.messageunpressed);
//                top2.setBackgroundResource(R.drawable.bg_tab_selector);
//                top1.setBackgroundColor(Color.parseColor("#ff888888"));
//                top3.setBackgroundColor(Color.parseColor("#ff888888"));
                getSupportActionBar().setTitle("历史操作");
                break;
            case 2:

                imagetop1.setImageResource(R.drawable.homeunpressed);
                imagetop2.setImageResource(R.drawable.bellunpressed);
                imagetop3.setImageResource(R.drawable.messagepressed);

//                top3.setBackgroundResource(R.drawable.bg_tab_selector);
//                top2.setBackgroundColor(Color.parseColor("#ff888888"));
//                top1.setBackgroundColor(Color.parseColor("#ff888888"));
                getSupportActionBar().setTitle("管理目录");
                break;

        }
    }

//    public void setBackgroundcolor() {
//        switch (myViewPager.getCurrentItem()) {
//            case 0:
//                //topmenu.setBackgroundResource(R.drawable.homebar_header);
//                backgroundlayout.setBackgroundColor(Color.parseColor("#d9d3bb"));
//                break;
//            case 1:
//                //topmenu.setBackgroundResource(R.drawable.messagebar_header);
//                backgroundlayout.setBackgroundColor(Color.parseColor("#804d76"));
//                break;
//            case 2:
//                //topmenu.setBackgroundResource(R.drawable.chatbar_header);
//                backgroundlayout.setBackgroundColor(Color.parseColor("#68d7c4"));
//                break;
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toplayout1:
                myViewPager.setCurrentItem(0);
                break;
            case R.id.toplayout2:
                myViewPager.setCurrentItem(1);
                break;
            case R.id.toplayout3:
                myViewPager.setCurrentItem(2);
                break;
        }
    }
}
