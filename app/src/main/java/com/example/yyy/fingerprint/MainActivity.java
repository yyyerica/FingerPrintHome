package com.example.yyy.fingerprint;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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

import com.example.yyy.fingerprint.RequestService.BootService;


import java.io.File;
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

// 头像
    Bitmap bitmap;
    ImageView imageview;
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;

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

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changheadportrait();
            }
        });

        if (userSettings!=null){
            String name = userSettings.getString("userid","默认值");
            //注意每个activity的navigationView是不一样的
            TextView headernametext = (TextView)layout.findViewById(R.id.headerNameText);
            headernametext.setText(name);
        }
        //取图片
        bitmap = SharedPreferUtils.getBitmap(this, "pic", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        imageview = (ImageView)layout.findViewById(R.id.imageView);
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
        toolbar.setLogo(R.drawable.e_lock);//设置app logo
//        getSupportActionBar().setTitle("setTitle");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#000000"));//设置标题颜色

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
                getSupportActionBar().setTitle("请求消息");
                break;
            case 1:

                imagetop1.setImageResource(R.drawable.homeunpressed);
                imagetop2.setImageResource(R.drawable.bellpressed);
                imagetop3.setImageResource(R.drawable.messageunpressed);
                getSupportActionBar().setTitle("历史操作");
                break;
            case 2:

                imagetop1.setImageResource(R.drawable.homeunpressed);
                imagetop2.setImageResource(R.drawable.bellunpressed);
                imagetop3.setImageResource(R.drawable.messagepressed);
                getSupportActionBar().setTitle("管理目录");
                break;

        }
    }


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
/////////////////////////////////////////////////////////////头像
    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");

            imageview.setImageBitmap(photo);

            // 存图片
            SharedPreferUtils.putBitmap(this, "pic", photo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                cropRawPhoto(intent.getData());
                break;

            case CODE_CAMERA_REQUEST:
                if (hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory(),
                            IMAGE_FILE_NAME);
                    cropRawPhoto(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG)
                            .show();
                }

                break;

            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    setImageToHeadView(intent);
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }


    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }



    public void changheadportrait() {
        /* @setIcon 设置对话框图标
                 * @setTitle 设置对话框标题
                 * @setMessage 设置对话框消息提示
                 * setXXX方法返回Dialog对象，因此可以链式设置属性
                 */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
//                        normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("更换头像");
        normalDialog.setMessage("选择方式");
        normalDialog.setPositiveButton("本地相册选取头像",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 从本地相册选取图片作为头像

                        Intent intentFromGallery = new Intent();
                        // 设置文件类型
                        intentFromGallery.setType("image/*");
                        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
                    }
                });
        normalDialog.setNegativeButton("手机拍照选取头像",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        // 判断存储卡是否可用，存储照片文件
                        if (hasSdcard()) {
                            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                                    .fromFile(new File(Environment
                                            .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                        }

                        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
                    }
                });
// 显示
        normalDialog.show();

//        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//        alertDialog.show();
//        Window win = alertDialog.getWindow();
//        //设置自定义的对话框布局
//        win.setContentView(R.layout.alertdialog_layout);
//
//        ImageButton game_btn = (ImageButton)win.findViewById(R.id.camera);
//        game_btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                // 判断存储卡是否可用，存储照片文件
//                if (hasSdcard()) {
//                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
//                            .fromFile(new File(Environment
//                                    .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                }
//
//                startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
//            }
//        });
//
//        ImageButton browser_btn = (ImageButton)win.findViewById(R.id.album);
//        browser_btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
// //从本地相册选取图片作为头像
//
//                        Intent intentFromGallery = new Intent();
//                        // 设置文件类型
//                        intentFromGallery.setType("image/*");
//                        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
//                        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
//            }
//        });



    }
}
