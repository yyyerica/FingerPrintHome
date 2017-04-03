package com.example.yyy.fingerprint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yyy.fingerprint.FolderManage.Authority;
import com.example.yyy.fingerprint.FolderManage.GetAuthorityThread;
import com.example.yyy.fingerprint.LoginRegister.AddressUtil;
import com.example.yyy.fingerprint.LoginRegister.Keys;
import com.example.yyy.fingerprint.RequestService.NickNameThread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClientSettingActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    ListView listview;
    ClientSettingAdapter arrayAdapter;
//    String arr1 = "c://bbb";
//    String arr2 = "d:ccc";
//    ArrayList<String> strs = new ArrayList<String>(){{add(arr1);add(arr1);add(arr1);add(arr2);add(arr1);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);}};
    ArrayList<String> strs = new ArrayList<String>(){};

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;
    ImageView imageview;//头像
    Toolbar toolbar2;
    String guid;
    EditText editText;

    FrameLayout frameLayout;

    List<Authority> authorityLis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_clientsetting);

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

        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        toolbar2 = (Toolbar)findViewById(R.id.toolbar2);
        settoolbar();//工具栏
        new GetAuthorityThread(Keys.USER_ID, Keys.IMEI, AddressUtil.LOGIN_URL, ClientSettingActivity.this).start();




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
        Bitmap bitmap = SharedPreferUtils.getBitmap(this, "pic", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        imageview = (ImageView)layout.findViewById(R.id.imageView);
        if(bitmap!=null)
            imageview.setImageBitmap(bitmap);

        arrayAdapter = new ClientSettingAdapter(this,R.layout.clientsetting_item,strs);
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
//                }

                if(authorityLis!=null){
                    guid = authorityLis.get(position).getGuid();
                }


                editText = new EditText(ClientSettingActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(ClientSettingActivity.this,4);
                builder.setTitle("更改客户端名称").
                            setIcon(android.R.drawable.ic_dialog_info).
                            setView(editText).
                        setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                // TODO Auto-generated method stub
                                new NickNameThread(Keys.USER_ID, Keys.IMEI, guid, editText.getText().toString(), AddressUtil.LOGIN_URL, ClientSettingActivity.this).start();
                            }
                        }).setNegativeButton("取消", null).show();


            }
        });
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

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
        toolbar.setLogo(R.drawable.e_lock);//设置app logo


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

    void setListViewHeight() {

        //设置listview高度
        int totalHeight = 0;
        for (int i = 0, len = arrayAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = arrayAdapter.getView(i, null, listview);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
// 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.height = totalHeight + (listview.getDividerHeight() *
                (arrayAdapter.getCount() )) + toolbar2.getHeight() + listview.getPaddingBottom()*2 ;
// listView.getDividerHeight()获取子项间分隔符占用的高度
// params.height最后得到整个ListView完整显示需要的高度
        listview.setLayoutParams(params);
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    arrayAdapter.clear();
                    //strs.clear();
                    authorityLis= (List<Authority>) msg.obj;




                    for(int i = 0;i < authorityLis.size();i++) {
                        Authority authority1 = authorityLis.get(i);
//                        if(strs.size()==0)
//                            strs.add(authority1.getGuid());
//                        else {
//                            for (int a = 0; a < strs.size(); a++) {
//                                if (!authority1.getGuid().equals(strs.get(a)))
//                                    strs.add(authority1.getGuid());
//                            }
//                        }
                        if(!contentGuid(strs,authority1)){
                            if(authority1.getNickname().equals("defaultcomputer")) {
                                strs.add(authority1.getGuid());
                            } else if(!contentNickName(strs,authority1)) {
                                strs.add(authority1.getNickname());
                                Log.e("ClientSettingActivity","name:" + authority1.getNickname());
                            }
                        }
                    }

                    if(strs.size() == 0) {
                        frameLayout.setVisibility(View.VISIBLE);
                    } else frameLayout.setVisibility(View.GONE);

                    //arrayAdapter.notifyDataSetChanged();
                    listview.setAdapter(arrayAdapter);
                    setListViewHeight();
                    Log.e("ClientSettingActivity","arg1");
                    break;
            }
        }
    };

    public boolean contentGuid(List a,Authority authority){
        for (int i=0;i<a.size();i++){
            if(authority.getGuid().equals(a.get(i))){
                return true;
            }
        }
        return false;
    }

    public boolean contentNickName(List a,Authority authority){
        for (int i=0;i<a.size();i++){
            if(authority.getNickname().equals(a.get(i))){
                return true;
            }
        }
        return false;
    }

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
        final android.support.v7.app.AlertDialog.Builder normalDialog =
                new android.support.v7.app.AlertDialog.Builder(ClientSettingActivity.this);
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
    }

}
