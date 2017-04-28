package com.example.john.daygram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Display extends AppCompatActivity {
    private TextView time;
    private ImageView ret;
    private Button done;
    private EditText edit;
    private Data data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        data = (Data)this.getIntent().getExtras().get("data");

        time = (TextView)findViewById(R.id.date);
        edit = (EditText)findViewById(R.id.diary);

        //设置不可编辑状态
        edit.setFocusable(false);
        edit.setFocusableInTouchMode(false);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置可编辑状态
                edit.setFocusableInTouchMode(true);
                edit.setFocusable(true);
            }
        });

        //如果是星期天则显示红色
        String week = data.getWeek();
        if(week.equals("SUNDAY")){
            week = "<font color='#FF0000'>" + week + "</font>";
        }
        // 在标题栏位置显示当前时间
        time.setText(Html.fromHtml(week));
        time.append("/"+data.getMonth()+" "+data.getDay()+"/"+data.getYear());

        //在编辑位置显示之前保存的日记内容
        edit.setText(data.getDetail());

        done = (Button)findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将编辑区的内容写入日记
                data.setDetail(edit.getText().toString());
                data.updateAll("date = ?",data.getDate());
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        ret = (ImageView)findViewById(R.id.home);
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
