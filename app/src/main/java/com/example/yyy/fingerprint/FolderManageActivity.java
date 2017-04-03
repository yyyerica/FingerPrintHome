package com.example.yyy.fingerprint;

import android.content.Context;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yyy.fingerprint.FolderManage.Authority;
import com.example.yyy.fingerprint.FolderManage.GetAuthorityThread;
import com.example.yyy.fingerprint.LoginRegister.AddressUtil;
import com.example.yyy.fingerprint.LoginRegister.Keys;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FolderManageActivity extends AppCompatActivity {

    NavigationView navigationView;
    Toolbar toolbar2,toolbartop;
    private ActionBarDrawerToggle mDrawerToggle;
    ListView lv;

    FolderManageAdapter arrayAdapter;

//    String[] arr1 = {"dota","c://bbb"};
//    String[] arr2 = {"lol","d:ccc"};
//    ArrayList<String[]> strs = new ArrayList<String[]>(){{add(arr1);add(arr1);add(arr1);add(arr2);add(arr1);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);}};
    ArrayList<String[]> strs = new ArrayList<String[]>(){};
//    private ClientDatabaseHelper mClientDatabaseHelper;

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

    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foldermanage);


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

        lv = (ListView)findViewById(R.id.folderlistview);
        //for (int i = 0;i<10;i++) strs.add(new String[]{"123123","123123123"});
        arrayAdapter = new FolderManageAdapter(FolderManageActivity.this,R.layout.foldermanage_item,strs);
        lv.setAdapter(arrayAdapter);
        //setListViewHeightBasedOnChildren(lv);

        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);
        settoolbar();//工具栏

        request();


        //侧滑栏
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(3).getSubMenu().getItem(0).setChecked(true);//默认选中
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true); // 改变item选中状态

                int id = item.getItemId();

                if (id == R.id.home) {
                    Intent intent1 = new Intent(FolderManageActivity.this,MainActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (id == R.id.gerenziliao) {
                    Intent intent = new Intent(FolderManageActivity.this,PersonalDataActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.help) {
                    Intent intent1 = new Intent(FolderManageActivity.this,HelpActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (id == R.id.wenjianguanli) {

                } else if (id == R.id.kehuduanshezhi) {
                    Intent intent = new Intent(FolderManageActivity.this,ClientSettingActivity.class);
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
        Bitmap bitmap = SharedPreferUtils.getBitmap(this, "pic", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        imageview = (ImageView)layout.findViewById(R.id.imageView);
        if(bitmap!=null)
            imageview.setImageBitmap(bitmap);

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Toolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        if (toolbar2 != null) {

            toolbar2.setTitle("所管理文件");
            //toolbar2.setTitleTextColor(Color.parseColor("#c86d6d"));
            //toolbar.setSubtitle("微信安全支付");
        }
    }

    public void settoolbar(){
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbartop = (Toolbar) findViewById(R.id.toolbar);
        //声明后，用setSupportActionBar 设定，Toolbar即能取代原本的 actionbar 了
        setSupportActionBar(toolbartop);
        toolbartop.setLogo(R.drawable.e_lock);//设置app logo
        getSupportActionBar().setTitle("文件管理");
        toolbartop.setTitleTextColor(Color.parseColor("#000000"));//设置标题颜色
//        toolbar.setTitleTextAppearance(this,"30sp");//修改标题的字体大小
        //getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbartop, R.string.open, R.string.close) {
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

        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }
    }

//
//    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        //获取ListView对应的Adapter
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            // pre-condition
//            return;
//        }
//
//        int totalHeight = 0;
//        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(0, 0);  //计算子项View 的宽高
//            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        //listView.getDividerHeight()获取子项间分隔符占用的高度
//        //params.height最后得到整个ListView完整显示需要的高度
//        Log.e("FolderManageActivity",params.height+"");
//
//        listView.setLayoutParams(params);
//
//        Log.e("FolderManageActivity",listView.getHeight()+"");
//        Log.e("FolderManageActivity",params.height+"");
//    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            //listItem.measure( View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + listView.getDividerHeight() * (listAdapter.getCount() - 1) + listView.getPaddingBottom()*2;
        listView.setLayoutParams(params);
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度

        //listView.setLayoutParams(params);
        //LinearLayout layout = (LinearLayout)findViewById(R.id.customLayout);
        //listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));

//        layout.updateViewLayout(listView, params);
//        listView.requestLayout();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void request() {
        new GetAuthorityThread(Keys.USER_ID, Keys.IMEI, AddressUtil.LOGIN_URL, FolderManageActivity.this).start();
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1: //从数据库拿到全体文件列表
                    List<Authority> synchros = (List<Authority>) msg.obj;
                    for(int i = 0;i < synchros.size();i++) {
                        Authority Authority1 = synchros.get(i);
                        if(Authority1.getNickname().equals("defaultcomputer")) {
                            strs.add(new String[]{Authority1.getFile_path(),Authority1.getGuid()});
                        } else strs.add(new String[]{Authority1.getFile_path(),Authority1.getNickname()});
                    }

                    if(strs.size()==0) {
                        frameLayout.setVisibility(View.VISIBLE);
                    } else frameLayout.setVisibility(View.GONE);

                    arrayAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(lv);
//                    lv.post(new Runnable() {
//                        @Override
//                        public void run() {
////                            lv.setLayoutParams(new android.support.v4.widget.NestedScrollView.LayoutParams(android.support.v4.widget.NestedScrollView.LayoutParams.MATCH_PARENT, android.support.v4.widget.NestedScrollView.LayoutParams.MATCH_PARENT));
//                            lv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                            setListViewHeightBasedOnChildren(lv);
// }
//                    });
                    break;
            }

        }
    };



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
                new AlertDialog.Builder(FolderManageActivity.this);
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
