package com.example.john.daygram;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoosingAdapter extends BaseAdapter {
    private String year;
    private String month;

    private Map<String,Integer> monthToNum = new HashMap<String,Integer>(){
        {
            put("JANUARY",1);put("FEBRUARY",2);put("MARCH",3);put("APRIL",4);
            put("MAY",5);put("JUNE",6);put("JULY",7);put("AUGUST",8);
            put("SEPTEMBER",9);put("OCTOBER",10);put("NOVEMBER",11);put("DECEMBER",12);
        }
    };
    private Map<Integer,String> numToMonth = new HashMap<Integer,String>(){
        {
            put(1,"JANUARY");put(2,"FEBRUARY");put(3,"MARCH");put(4,"APRIL");
            put(5,"MAY");put(6,"JUNE");put(7,"JULY");put(8,"AUGUST");
            put(9,"SEPTEMBER");put(10,"OCTOBER");put(11,"NOVEMBER");put(12,"DECEMBER");
        }
    };
    private static final int TYPE_A = 0;            //代表该月
    private static final int TYPE_B = 1;            //代表不是该月

    private Context context;
    private List<Data> listData = new ArrayList<>();

    public ChoosingAdapter(Context context, String year, String month) {
        this.context = context;
        this.year = year;
        this.month = month;
        listData = DataSupport.where("year = ? and month = ?",year,month).find(Data.class);
    }


    @Override
    public int getItemViewType(int position) {
        int result = 0;
        if (position == monthToNum.get(month) - 1) {
            result = TYPE_A;
        } else{
            result = TYPE_B;
        }
        return result;
    }

    //获得有多少中view type
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public List<Data> getItem(int position) {
        listData.clear();
        listData = DataSupport.where("year = ? and month = ?",year,numToMonth.get(position+1)).find(Data.class);
        return listData;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建两种不同种类的viewHolder变量
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        //根据position获得View的type
        int type = getItemViewType(position);
        if (convertView == null) {
            //实例化
            holder1 = new ViewHolder1();
            holder2 = new ViewHolder2();
            //根据不同的type 来inflate不同的item layout
            //然后设置不同的tag
            //这里的tag设置是用的资源ID作为Key
            switch (type) {
                case TYPE_A:
                    convertView = View.inflate(context, R.layout.chosen_month, null);
                    holder1.month = (TextView) convertView.findViewById(R.id.chosen);
                    convertView.setTag(R.id.tag_chosen, holder1);
                    break;
                case TYPE_B:
                    convertView = View.inflate(context, R.layout.not_chosen_month, null);
                    holder2.month = (TextView) convertView.findViewById(R.id.not_chosen);
                    convertView.setTag(R.id.tag_not_chosen, holder2);
                    break;
            }
        } else {
            //根据不同的type来获得tag
            switch (type) {
                case TYPE_A:
                    holder1 = (ViewHolder1) convertView.getTag(R.id.tag_chosen);
                    break;
                case TYPE_B:
                    holder2 = (ViewHolder2) convertView.getTag(R.id.tag_not_chosen);
                    break;
            }
        }

        listData.clear();
        listData = DataSupport.where("year = ? and month = ?",year,numToMonth.get(position+1)).find(Data.class);
        //根据不同的type设置数据
        switch (type) {
            case TYPE_A:
                String A = listData.get(0).getMonth().substring(0,3).toUpperCase();
                holder1.month.setText(A);
                break;

            case TYPE_B:
                String B = listData.get(0).getMonth().substring(0,3).toUpperCase();
                holder2.month.setText(B);
                break;
        }
        return convertView;
    }

    private static class ViewHolder1 {
        TextView month;
    }

    private static class ViewHolder2 {
        TextView month;
    }
}