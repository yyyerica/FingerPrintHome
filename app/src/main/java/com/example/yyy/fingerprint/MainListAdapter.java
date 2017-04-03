package com.example.yyy.fingerprint;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yyy.fingerprint.FolderManage.Authority;
import com.example.yyy.fingerprint.RequestService.Synchro;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YYY on 2017/2/15.
 */

public class MainListAdapter extends BaseAdapter {

    private ArrayList<Synchro> objects;
    private TextView date,time,name,guid;
    private Context context;

    // 第一个参数是上下文，一般为this。
    // 第二个参数是自定义的布局文件，比如下面的就是R.layout.list_item。
    // 第三个参数是布局中用来显示文字的TextView的id，
    // 第四个参数是数据集合
//    public MainListAdapter(Context context, int resource, List<String[]> objects) {
//        super(context, resource, objects);
//        resourceId=resource;
//        this.objects = objects;
//    }

    @Override
     public Object getItem(int position) {
         return objects.get(position);
     }

    @Override
     public long getItemId(int position) {
         return position;
     }

    @Override
     public int getCount() {
         return objects.size();
     }

    @Override
    public int getItemViewType(int position) {

        Synchro synchro = objects.get(position);

        // TODO Auto-generated method stub
        if ("0".equals(synchro.getAuthority_number())) {
            return 0;
        } else if ("1".equals(synchro.getAuthority_number())) {
            return 1;
        } else return -1;
    }

    @Override
     public int getViewTypeCount() {
         return 2;
     } //打开，删除，批量删除

    public MainListAdapter(Context context, ArrayList<Synchro> objects) {
        this.objects = objects;
        this.context = context;
    }

    // 系统显示列表时，首先实例化一个适配器（这里将实例化自定义的适配器）。
    // 当手动完成适配时，必须手动映射数据，这需要重写getView（）方法。
    // 系统在绘制列表的每一行的时候将调用此方法。
    // getView()有三个参数，
    // position表示将显示的是第几行，
    // covertView是从布局文件中inflate来的布局。
    // 我们用LayoutInflater的方法将定义好的image_item.xml文件提取成View实例用来显示。
    // 然后将xml文件中的各个组件实例化（简单的findViewById()方法）。
    // 这样便可以将数据对应到各个组件上了。
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        //String[] map = getItem(position);
        //list里有三个string分别存放name(0）time(1) date(2)

        // 获取数据
        if (convertView == null) {

            int type = getItemViewType(position);

            switch (type) {
                case 0:
                    convertView = LayoutInflater.from(context).inflate(R.layout.maindelete_item, null);
                    break;
                case 1:
                    convertView = LayoutInflater.from(context).inflate(R.layout.mainarray_item, null);
                    break;
            }


            time = (TextView)convertView.findViewById(R.id.timeText);
            name = (TextView)convertView.findViewById(R.id.nameText);
            date = (TextView)convertView.findViewById(R.id.dateText);
            guid = (TextView)convertView.findViewById(R.id.guidText);
        } else {
            view = convertView;
        }


        time.setText(objects.get(position).getOperate_time());
        name.setText(objects.get(position).getFile_path());
        date.setText(objects.get(position).getOperate_date());
        if(objects.get(position).getNickname().equals("defaultcomputer"))
            guid.setText(objects.get(position).getGuid());
        else guid.setText(objects.get(position).getNickname());

        return convertView;

    }



}
