package com.example.yyy.fingerprint;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yyy.fingerprint.FolderHistory.GetHistoryThread;
import com.example.yyy.fingerprint.FolderHistory.History;
import com.example.yyy.fingerprint.LoginRegister.AddressUtil;
import com.example.yyy.fingerprint.LoginRegister.Keys;

import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment {
    String date = null;// 设置默认选中的日期  格式为 “2014-04-05” 标准DATE格式
    View view;
    Toolbar toolbarbottom;
    TextView toobarTitleText;
    ListView calendarlistview;
    ListView lv;
    CalendarListAdapter arrayAdapter;
//    String[] arr1 = {"16:45","dota"};
//    String[] arr2 = {"23:09","lol"};
//    ArrayList<String[]> strs = new ArrayList<String[]>(){{add(arr1); add(arr1); add(arr1); add(arr1); add(arr1); add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);add(arr2);}};
    ArrayList<String[]> strs = new ArrayList<String[]>(){};

    AlphaAnimation appearAnimation,disappearAnimation;
    TextView imageView2, tag;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.activity_calendar,
                null);
        view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.fade_in));


        toolbarbottom = (Toolbar)view.findViewById(R.id.toolbarbottom);
        calendarlistview = (ListView) view.findViewById(R.id.calendarListview);
        toobarTitleText = (TextView) view.findViewById(R.id.toobarTitleText);
        tag = (TextView)view.findViewById(R.id.tag);

        arrayAdapter = new CalendarListAdapter(getActivity(),R.layout.calendar_item,strs);
        lv = (ListView)view.findViewById(R.id.calendarListview) ;
        lv.setAdapter(arrayAdapter);

        LinearLayout ll_popup = (LinearLayout) view
                .findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.push_bottom_in_1));
        final TextView popupwindow_calendar_month = (TextView) view
                .findViewById(R.id.popupwindow_calendar_month);
        final KCalendar calendar = (KCalendar) view
                .findViewById(R.id.popupwindow_calendar);
//        Button popupwindow_calendar_bt_enter = (Button) view
//                .findViewById(R.id.popupwindow_calendar_bt_enter);

        popupwindow_calendar_month.setText(calendar.getCalendarYear() + "年"
                + calendar.getCalendarMonth() + "月");

        if (null != date) {

            int years = Integer.parseInt(date.substring(0,
                    date.indexOf("-")));
            int month = Integer.parseInt(date.substring(
                    date.indexOf("-") + 1, date.lastIndexOf("-")));
            popupwindow_calendar_month.setText(years + "年" + month + "月");

            calendar.showCalendar(years, month);
            calendar.setCalendarDayBgColor(date,
                    R.drawable.calendar_date_focused);
        }

        List<String> list = new ArrayList<String>(); //设置标记列表
        list.add("2014-04-01");
        list.add("2014-04-02");
        calendar.addMarks(list, 0);

        //监听所选中的日期
        calendar.setOnCalendarClickListener(new KCalendar.OnCalendarClickListener() {

            public void onCalendarClick(int row, int col, String dateFormat) {
                int month = Integer.parseInt(dateFormat.substring(
                        dateFormat.indexOf("-") + 1,
                        dateFormat.lastIndexOf("-")));

                if (calendar.getCalendarMonth() - month == 1//跨年跳转
                        || calendar.getCalendarMonth() - month == -11) {
                    calendar.lastMonth();

                } else if (month - calendar.getCalendarMonth() == 1 //跨年跳转
                        || month - calendar.getCalendarMonth() == -11) {
                    calendar.nextMonth();

                } else {
                    calendar.removeAllBgColor();
                    calendar.setCalendarDayBgColor(dateFormat,
                            R.drawable.calendar_date_focused);
                    date = dateFormat;//最后返回给全局 date
                }

                if(date!=null)
                    operateDate();
            }
        });

        //监听当前月份
        calendar.setOnCalendarDateChangedListener(new KCalendar.OnCalendarDateChangedListener() {
            public void onCalendarDateChanged(int year, int month) {
                popupwindow_calendar_month
                        .setText(year + "年" + month + "月");
            }
        });

        //上月监听按钮
        RelativeLayout popupwindow_calendar_last_month = (RelativeLayout) view
                .findViewById(R.id.popupwindow_calendar_last_month);
        popupwindow_calendar_last_month
                .setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        calendar.lastMonth();
                    }

                });

        //下月监听按钮
        RelativeLayout popupwindow_calendar_next_month = (RelativeLayout) view
                .findViewById(R.id.popupwindow_calendar_next_month);
        popupwindow_calendar_next_month
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //Log.e("CalendarFragment","calendar.nextMonth();");
                        calendar.nextMonth();
                    }
                });

//        //关闭窗口
//        popupwindow_calendar_bt_enter
//                .setOnClickListener(new View.OnClickListener() {
//
//                    public void onClick(View v) {
//
//                    }
//                });


        return view;
    }


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

    public void operateDate() {

        //String[] thedate = date.split("-");
        toobarTitleText.setText(date);

//动画
        appearAnimation = new AlphaAnimation(0, 1);
        appearAnimation.setDuration(1000);
        appearAnimation.setRepeatMode(Animation.REVERSE);
        appearAnimation.setRepeatCount(3);
//        disappearAnimation = new AlphaAnimation(1, 0);
//        disappearAnimation.setDuration(5000);

        imageView2 = (TextView)view.findViewById(R.id.imageView2);
            //想让控件出现时
        if (imageView2.getVisibility() == View.GONE) {
            imageView2.startAnimation(appearAnimation);
            imageView2.setVisibility(View.VISIBLE);
//        } else {
//            //想让控件消失时
//            imageView2.startAnimation(disappearAnimation);
//            disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
//
//                @Override
//                public void onAnimationStart(Animation animation) {}
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {}
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    imageView2.setVisibility(View.GONE);
//                }
//            });
        }
        new GetHistoryThread(Keys.USER_ID, Keys.IMEI,date,AddressUtil.LOGIN_URL,this).start();
    }


    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    strs.clear();Log.e("CalendarFragment","0");
                    List<History> histories = (List<History>)msg.obj;
                    for(int i=0;i<histories.size();i++) {
                        History history = histories.get(i);
//                        strs.add(new String[]{history.getFile_path(),history.getOperate_time(),"客户端 : "+history.getGuid()});
                        strs.add(new String[]{history.getFile_path(),history.getOperate_time()});
                        //Log.e("getFile_path",history.getFile_path());
                    }

                    Log.e("CalendarFragment","strs.size"+strs.size());
                    if(strs.size() == 0)
                    {Log.e("CalendarFragment","1");
                        tag.setVisibility(View.VISIBLE);
                        tag.setText("该日期没有历史记录");
                    } else  {
                        Log.e("CalendarFragment","2");
                        tag.setVisibility(View.GONE);
                        calendarlistview.setVisibility(View.VISIBLE);
                        tag.setText("该日期没有历史记录");
                    }

                    lv.setAdapter(arrayAdapter);
                    setListViewHeightBasedOnChildren(lv);

                    arrayAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
}
