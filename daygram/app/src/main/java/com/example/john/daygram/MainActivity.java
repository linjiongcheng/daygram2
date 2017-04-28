package com.example.john.daygram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ImageView addDiary;
    private ImageView browse;
    private ListView list;
    private String selectYear;
    private String selectMonth;
    private List<Data> listData = new ArrayList<>();                            //记录同一月的数据
//    private Map<String,String> mEngMonth = new HashMap<String,String>(){
//        {
//            put("1","JANUARY");put("2","FEBRUARY");put("3","MARCH");put("4","APRIL");
//            put("5","MAY");put("6","JUNE");put("7","JULY");put("8","AUGUST");
//            put("9","SEPTEMBER");put("10","OCTOBER");put("11","NOVEMBER");put("12","DECEMBER");
//        }
//    };
//    private Map<String,String> mEngWeek = new HashMap<String, String>(){
//        {
//            put("1","SUNDAY");put("2","MONDAY");put("3","TUESDAY");put("4","WEDNESDAY");
//            put("5","THURSDAY");put("6","FRIDAY");put("7","SATURDAY");
//        }
//    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    refresh(selectYear,selectMonth);
                }
                break;
            case 2:
                if(requestCode == RESULT_OK){}
                break;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //第一次登录preference的“firststart”为“true”
        preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
        Log.i("判断是否为第一次登录",preferences.getBoolean("firststart", true)+"");
        //判断是不是首次登录
        if (preferences.getBoolean("firststart", true)) {
            editor = preferences.edit();
            //将登录标志位设置为false，即将preference的“firststart”修改为“false”
            editor.putBoolean("firststart", false);
            //提交修改
            editor.commit();
            //第一次登录，初始化数据
            try{
                //创建数据库
                Connector.getDatabase();
                //初始化所有数据
                Initialization initialization = new Initialization();
                List<Data> allListData = initialization.init();
                for(Data data : allListData){
                    //将数据添加到数据库中
                    data.save();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);

        //获取系统当前日期
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);

        //实现年份的选择
        final Spinner spinner = (Spinner)findViewById(R.id.year);
        spinner.getSelectedItem();
        int yearOrder = year - 2016;
        spinner.setSelection(yearOrder);                //设置默认显示的是当前年份
        //实现月份的选择
        final Spinner spinner2 = (Spinner)findViewById(R.id.month);
        spinner2.getSelectedItem();
        spinner2.setSelection(month);               //设置默认显示的是当前月份

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String year = MainActivity.this.getResources().getStringArray(R.array.ctype1)[position];
                selectMonth = spinner2.getSelectedItem().toString();
                refresh(year,selectMonth);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String month = MainActivity.this.getResources().getStringArray(R.array.ctype2)[position];
                selectYear = spinner.getSelectedItem().toString();
                refresh(selectYear,month);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //添加新日记操作，获取当前系统时间并传递给下个activity
        addDiary = (ImageView) findViewById(R.id.add);
        addDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                //获取当前日期
                Date date = c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String formatDate = sdf.format(date);

                //从数据库中找到对应日期的数据并传递给显示详情的Display
                intent.putExtra("data", DataSupport.where("date = ?",formatDate).find(Data.class).get(0));
                intent.setClass(MainActivity.this, Display.class);
                startActivityForResult(intent,1);
            }
        });
        browse = (ImageView)findViewById(R.id.browse);
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
//                intent.putExtra("allyear",(Serializable)listOfYear);
//                intent.putExtra("browse",(Serializable)listData);
                intent.putExtra("year",selectYear);
                intent.putExtra("month",selectMonth);
                intent.setClass(MainActivity.this,Browse.class);
                startActivityForResult(intent,2);
            }
        });

    }
    //根据选择的月份和年份刷新列表内容
    private void refresh(String year,String month){

        //查询选中年份和月份的相关数据
        listData.clear();
        listData = DataSupport.where("year = ? and month = ?",year,month).find(Data.class);

        try{
            ABAdapter adapter = new ABAdapter(this,(ArrayList<Data>)listData);
            list.setAdapter(adapter);
            //设置列表选项监听功能
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra("data", listData.get(position));
                    intent.setClass(MainActivity.this,Display.class);
                    startActivityForResult(intent,1);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

