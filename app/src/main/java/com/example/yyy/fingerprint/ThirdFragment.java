package com.example.yyy.fingerprint;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yyy.fingerprint.FolderManage.Authority;
import com.example.yyy.fingerprint.FolderManage.GetAuthorityThread;
import com.example.yyy.fingerprint.LoginRegister.AddressUtil;
import com.example.yyy.fingerprint.LoginRegister.Keys;

import java.util.ArrayList;
import java.util.List;


public class ThirdFragment extends Fragment {

    //Map map = new HashMap();
    private ExpandableListView expandableListView;
    private ArrayList<String> group_list;
    private ArrayList<List<String>> item_list;
//    private List<List<Integer>> item_list2;
    MyExpandableListViewAdapter myExpandableListViewAdapter;
    FrameLayout frameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.third_fragment,null);
        frameLayout = (FrameLayout) view.findViewById(R.id.framelayout);
//        expandableListView_one =(ExpandableListView)view.findViewById(R.id.expandableListView);
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        myExpandableListViewAdapter = new MyExpandableListViewAdapter(getActivity());
        expandableListView.setAdapter(myExpandableListViewAdapter);
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //map.put("2017-2-14","mtdesktop");

        request();

        //随便一堆测试数据
        group_list = new ArrayList<String>();

        item_list = new ArrayList<List<String>>();
//        item_list.add(group_list);//小项group1
//        item_list.add(group_list);
//        item_list.add(group_list);

//        List<Integer> tmp_list = new ArrayList<Integer>();
//        tmp_list.add(R.drawable.keyedi);
//        tmp_list.add(R.drawable.keyedi);
//        tmp_list.add(R.drawable.keyedi);

//        item_list2 = new ArrayList<List<Integer>>();
//        item_list2.add(tmp_list);
//        item_list2.add(tmp_list);
//        item_list2.add(tmp_list);


    }

    public void request() {
        new GetAuthorityThread(Keys.USER_ID, Keys.IMEI, AddressUtil.LOGIN_URL,ThirdFragment.this).start();
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1: //从数据库拿到全体文件列表
                    group_list.clear();
                    item_list.clear();
                    List<Authority> authorityList = (List<Authority>) msg.obj;

                    for(int i = 0;i < authorityList.size();i++) {
                        Authority authority = authorityList.get(i);
                        if(authority.getNickname().equals("defaultcomputer")) {
                            if(!contentGuid(group_list,authority)) {//group没有item没有
                                group_list.add(authority.getGuid());
                                ArrayList<String> itemlistitem = new ArrayList<>();
                                itemlistitem.add(authority.getFile_path());
                                item_list.add(itemlistitem);
                            } else { //有group,添加item子项
                                for (int a=0;a<group_list.size();a++) {
                                    if (group_list.get(a).equals(authority.getGuid())){
                                        item_list.get(a).add(authority.getFile_path());
                                    }

                                }
                            }
                        } else {
                            if(!contentNickName(group_list,authority)) {//group没有item没有
                                group_list.add(authority.getNickname());
                                ArrayList<String> itemlistitem = new ArrayList<>();
                                itemlistitem.add(authority.getFile_path());
                                item_list.add(itemlistitem);
                            } else { //有group,添加item子项
                                for (int a=0;a<group_list.size();a++) {
                                    if (group_list.get(a).equals(authority.getNickname())){
                                        item_list.get(a).add(authority.getFile_path());
                                    }

                                }
                            }
                        }

                    }

                    if (group_list.size() == 0) {
                        frameLayout.setVisibility(View.VISIBLE);
                    } else frameLayout.setVisibility(View.GONE);

                    myExpandableListViewAdapter.notifyDataSetChanged();
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

    //用过ListView的人一定很熟悉，只不过这里是BaseExpandableListAdapter
    class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        private Context context;

        public MyExpandableListViewAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getGroupCount() {
            return group_list.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return item_list.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return group_list.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return item_list.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupHolder groupHolder = null;
            if (convertView == null) {
                convertView = (View) getActivity().getLayoutInflater().from(context).inflate(
                        R.layout.expendlist_group, null);
                groupHolder = new GroupHolder();
                groupHolder.txt = (TextView) convertView.findViewById(R.id.txt);
//                 groupHolder.img = (ImageView) convertView
//                 .findViewById(R.id.img);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            groupHolder.txt.setText("客户端 : "+group_list.get(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            ItemHolder itemHolder = null;
            if (convertView == null) {
                convertView = (View) getActivity().getLayoutInflater().from(context).inflate(
                        R.layout.expendlist_item, null);
                itemHolder = new ItemHolder();
                itemHolder.txt = (TextView) convertView.findViewById(R.id.txt);
                itemHolder.img = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder) convertView.getTag();
            }
            itemHolder.txt.setText(item_list.get(groupPosition).get(
                    childPosition));
//            itemHolder.img.setBackgroundResource(item_list2.get(groupPosition).get(
//                    childPosition));

            //itemHolder.img.setBackgroundResource(R.drawable.keyedi);//子项背景
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    class GroupHolder {
        public TextView txt;
        public ImageView img;
    }

    class ItemHolder {
        public ImageView img;
        public TextView txt;
    }
}
