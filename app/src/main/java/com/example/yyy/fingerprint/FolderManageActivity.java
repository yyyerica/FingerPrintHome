package com.example.yyy.fingerprint;

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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yyy.fingerprint.FolderManage.Authority;
import com.example.yyy.fingerprint.FolderManage.GetAuthorityThread;
import com.example.yyy.fingerprint.LoginRegister.AddressUtil;
import com.example.yyy.fingerprint.LoginRegister.Keys;
import com.example.yyy.fingerprint.LunxunService.Synchro;
import com.example.yyy.fingerprint.LunxunService.SynchroThread;

import java.util.ArrayList;
import java.util.List;

public class FolderManageActivity extends AppCompatActivity {

    NavigationView navigationView;
    Toolbar toolbar2,toolbartop;
    private ActionBarDrawerToggle mDrawerToggle;

    private ListView lv;

    FolderManageAdapter arrayAdapter;

//    String[] arr1 = {"dota","c://bbb"};
//    String[] arr2 = {"lol","d:ccc"};
//    ArrayList<String[]> strs = new ArrayList<String[]>(){{add(arr1);add(arr1);add(arr1);add(arr2);add(arr1);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);}};
    ArrayList<String[]> strs = new ArrayList<String[]>(){};
//    private ClientDatabaseHelper mClientDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wenjianguanli);

//        mClientDatabaseHelper = ClientDatabaseHelper.getInstance(this);

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

        request();

        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.changeAccount);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               Intent intent = new Intent(FolderManageActivity.this,LoginActivity.class);
//                startActivity(intent);
//            }
//        });

        settoolbar();//工具栏


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


        //listview
        lv = (ListView)findViewById(R.id.ziliaolistview);
        arrayAdapter = new FolderManageAdapter(this,R.layout.wenjianguanli_item,strs);
        //lv.setAdapter(arrayAdapter);

//        SharedPreferences userSettings= getSharedPreferences("settingid", 0);
//        String name = userSettings.getString("userid","默认值");
//        TextView headernameText = (TextView)findViewById(R.id.headerNameText);
//        headernameText.setText(name);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Toolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        if (toolbar2 != null) {
            toolbar2.setTitle("所管理文件");
            //toolbar.setSubtitle("微信安全支付");
        }
    }

    public void settoolbar(){
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbartop = (Toolbar) findViewById(R.id.toolbar1);
        //声明后，用setSupportActionBar 设定，Toolbar即能取代原本的 actionbar 了
        setSupportActionBar(toolbartop);
        toolbartop.setLogo(R.drawable.key);//设置app logo
        getSupportActionBar().setTitle("文件管理");
        toolbartop.setTitleTextColor(Color.parseColor("#000000"));//设置标题颜色
//        toolbar.setTitleTextAppearance(this,"30sp");//修改标题的字体大小
        //getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        toolbartop.inflateMenu(R.menu.base_toolbar_menu);//设置右上角的填充菜单
        toolbartop.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_search) {
                    Toast.makeText(FolderManageActivity.this , "点击了搜索按钮" , Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });


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


    void setListViewHeight() {

        //设置listview高度
        int totalHeight = 0;
        for (int i = 0, len = arrayAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = arrayAdapter.getView(i, null, lv);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
// 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight() *
                (arrayAdapter.getCount()+1));
// listView.getDividerHeight()获取子项间分隔符占用的高度
// params.height最后得到整个ListView完整显示需要的高度
        lv.setLayoutParams(params);
    }

    public void request() {
        new SynchroThread(Keys.USER_ID, Keys.IMEI, AddressUtil.LOGIN_URL, FolderManageActivity.this).start();
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
                        strs.add(new String[]{synchros1.getFile_path(),synchros1.getCpu_id()});
                    }
                    arrayAdapter = new FolderManageAdapter(FolderManageActivity.this,R.layout.wenjianguanli_item,strs);
                    lv.setAdapter(arrayAdapter);
                    setListViewHeight();
                    arrayAdapter.notifyDataSetChanged();

                    break;
            }

        }
    };
}
