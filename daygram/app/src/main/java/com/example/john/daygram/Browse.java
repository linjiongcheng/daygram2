package com.example.john.daygram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Browse extends AppCompatActivity {
    private List<Data> listData = new ArrayList<>();
    private String browse = "";
    private Data tempData;
    private TextView textView;
    private ImageView imageView;
    private GridView grid;
    private Spinner spinnerYear;
    private String year;
    private String month;
    private Map<Integer,String> mEngMonth = new HashMap<Integer,String>(){
        {
            put(1,"JANUARY");put(2,"FEBRUARY");put(3,"MARCH");put(4,"APRIL");
            put(5,"MAY");put(6,"JUNE");put(7,"JULY");put(8,"AUGUST");
            put(9,"SEPTEMBER");put(10,"OCTOBER");put(11,"NOVEMBER");put(12,"DECEMBER");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        //显示浏览页面
        textView = (TextView)findViewById(R.id.browse_string);
        spinnerYear = (Spinner)findViewById(R.id.browseyear);
        imageView = (ImageView)findViewById(R.id.browse2);
        grid = (GridView)findViewById(R.id.horilist);
        year = (String)this.getIntent().getExtras().get("year");
        month = (String)this.getIntent().getExtras().get("month");

        int yearOrder = Integer.parseInt(year) - 2016;
        spinnerYear.setSelection(yearOrder);                //设置默认显示的是当前年份
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = getApplicationContext().getResources().getStringArray(R.array.ctype1)[position];
                listData = DataSupport.where("year = ? and month = ?",year,month).find(Data.class);
                refresh(listData);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listData = DataSupport.where("year = ? and month = ?",year,month).find(Data.class);
        refresh(listData);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listData.clear();
                month = mEngMonth.get(position+1);
                listData = DataSupport.where("year = ? and month = ?",year,month).find(Data.class);
                refresh(listData);
            }
        });
    }
    public void refresh(List<Data> listData){
        browse = "";
        for(int i = 0;i < listData.size();i++){
            String str = listData.get(i).getDetail();
            if(!str.equals("")){
                tempData = listData.get(i);
                browse += tempData.getDay()+" "+tempData.getWeek()+" / "+tempData.getDetail()+"\n\n";
            }
        }
        textView.setText(browse);

        int size = 12;      //设置水平滚动选项个数为12个，代表12个月
        int length = 60;    //设置每个选项宽度为60
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);  //获得整个水平滚动条的长度
        int itemWidth = (int) (length * density);       //获得单个选项的宽度

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        grid.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        grid.setColumnWidth(itemWidth); // 设置列表项宽
        grid.setHorizontalSpacing(5); // 设置列表项水平间距
        grid.setStretchMode(GridView.NO_STRETCH);
        grid.setNumColumns(size); // 设置列数量=列表集合数

        ChoosingAdapter adapter = new ChoosingAdapter(this,year,month);
        grid.setAdapter(adapter);
    }
}
