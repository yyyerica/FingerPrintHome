package com.example.yyy.fingerprint;

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
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PersonalDataActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    ListView listView,headportraitListView;
    String[] arr1 = {"用户名","default"};
    String[] arr2 = {"客户端",""};
    String[] arr3 = {"管理文件",""};
//    String[] arr4 = {""}
    ArrayList<String[]> strs = new ArrayList<String[]>(){{add(arr1);add(arr2);add(arr3);}};

    String name;
    ImageView headImageView;
    RelativeLayout relativeLayout;

    PersonalDataAdapter adapter;

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_personaldata);

        //new SynchroThread(Keys.USER_ID, Keys.IMEI, AddressUtil.LOGIN_URL,PersonalDataActivity.this).start();

        headImageView = (ImageView)findViewById(R.id.headportraitImageView);
        relativeLayout = (RelativeLayout)findViewById(R.id.headportraitlayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changheadportrait();
            }
        });

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
        navigationView.getMenu().getItem(1).setChecked(true);//默认选中
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true); // 改变item选中状态

                int id = item.getItemId();

                if (id == R.id.home) {
                    Intent intent1 = new Intent(PersonalDataActivity.this,MainActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (id == R.id.gerenziliao) {

                } else if (id == R.id.help) {
                    Intent intent1 = new Intent(PersonalDataActivity.this,HelpActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (id == R.id.wenjianguanli) {
                    Intent intent1 = new Intent(PersonalDataActivity.this,FolderManageActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (id == R.id.kehuduanshezhi) {
                    Intent intent = new Intent(PersonalDataActivity.this,ClientSettingActivity.class);
                    startActivity(intent);
                    finish();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        SharedPreferences userSettings= getSharedPreferences("settingid", 0);
        layout = (LinearLayout)navigationView.inflateHeaderView(R.layout.nav_header_main);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changheadportrait();
            }
        });


        if (userSettings!=null){
            name = userSettings.getString("userid","默认值");
            //注意每个activity的navigationView是不一样的
            TextView headernametext = (TextView)layout.findViewById(R.id.headerNameText);
            headernametext.setText(name);
        }
        //取图片
        Bitmap bitmap = SharedPreferUtils.getBitmap(this, "pic", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        ImageView imageview = (ImageView)layout.findViewById(R.id.imageView);
        if(bitmap!=null) {
            imageview.setImageBitmap(bitmap);
            headImageView.setImageBitmap(bitmap);
        }

        CreateListView();//设置listview
    }

    public void CreateListView() {
        listView = (ListView)findViewById(R.id.gerenziliaoListView);
        strs.get(0)[1] = name;
        adapter = new PersonalDataAdapter(this,R.layout.gerenziliao_item,strs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
//                    case 0:
//                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PersonalDataActivity.this,4);
//                        builder.setTitle("更改用户名").
//                                setIcon(android.R.drawable.ic_dialog_info).
//                                setView(new EditText(PersonalDataActivity.this)).setPositiveButton("确定", null).
//                                setNegativeButton("取消", null).show();
//                        break;
                    case 1:
                        Intent intent = new Intent(PersonalDataActivity.this,ClientSettingActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        Intent intent1 = new Intent(PersonalDataActivity.this,FolderManageActivity.class);
                        startActivity(intent1);
                        break;

                }
            }
        });
//        headportraitListView = (ListView)findViewById(R.id.headportraitListView);
//        adapter = new SimpleAdapter(this, headportraitarrayList, R.layout.headportrait_item,
//                new String[]{"image"}, new int[]{R.id.headportraitImageView});
//        headportraitListView.setAdapter(adapter);
//
//        headportraitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:
//                /* @setIcon 设置对话框图标
//                 * @setTitle 设置对话框标题
//                 * @setMessage 设置对话框消息提示
//                 * setXXX方法返回Dialog对象，因此可以链式设置属性
//                 */
//                        final AlertDialog.Builder normalDialog =
//                                new AlertDialog.Builder(PersonalDataActivity.this);
////                        normalDialog.setIcon(R.drawable.icon_dialog);
//                        normalDialog.setTitle("更换头像");
//                        normalDialog.setMessage("选择方式");
//                        normalDialog.setPositiveButton("本地相册选取头像",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // 从本地相册选取图片作为头像
//
//                                        Intent intentFromGallery = new Intent();
//                                        // 设置文件类型
//                                        intentFromGallery.setType("image/*");
//                                        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
//                                        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
//                                    }
//                                });
//                        normalDialog.setNegativeButton("手机拍照选取头像",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                                        // 判断存储卡是否可用，存储照片文件
//                                        if (hasSdcard()) {
//                                            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
//                                                    .fromFile(new File(Environment
//                                                            .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//                                        }
//
//                                        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
//                                    }
//                                });
//                        // 显示
//                        normalDialog.show();
//                        break;
//                }
//            }
//        });

    }

//    public Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.arg1) {
//                case 1:
//                    List<Synchro> synchros = (List<Synchro>) msg.obj;
//
//                    for(int i = 0 ; i < synchros.size(); i++) {
//                        Synchro synchros1 = synchros.get(i);
//                        arr2[1]+=synchros1.getGuid();
//                        if(i<synchros.size()-1)
//                            arr2[1]+="\n";
//                    }
//
//                    listView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                    break;
//            }
//        }
//    };


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Toolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        if (toolbar != null) {
            toolbar.setTitle("个人资料");
            //toolbar.setSubtitle("微信安全支付");
        }
    }

    public void settoolbar(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.e_lock);//设置app logo

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

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");

//            tempHashMap = new HashMap<String, Object>();
//            tempHashMap.put("image", photo);
//            headportraitarrayList.clear();
//            headportraitarrayList.add(tempHashMap);
//            adapter.notifyDataSetChanged();
            headImageView.setImageBitmap(photo);
            ImageView leftimageview = (ImageView)layout.findViewById(R.id.imageView);
            leftimageview.setImageBitmap(photo);

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
                                new AlertDialog.Builder(PersonalDataActivity.this);
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
