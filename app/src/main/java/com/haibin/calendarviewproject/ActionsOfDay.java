package com.haibin.calendarviewproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ActionsOfDay extends AppCompatActivity implements View.OnClickListener {
    private ActionAdapter adapter = null;
    private List list;
    private ListView listView;
    private String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        date = getIntent().getStringExtra("date");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_of_day);
        list = new ArrayList<Action>();
        Classinit(date);
        listView = (ListView) findViewById(R.id.list_news);
        adapter = new ActionAdapter(ActionsOfDay.this, R.layout.action_list_item, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                Action act = (Action) list.get(i);
                Intent it = new Intent(ActionsOfDay.this, ActionInfo.class);
                it.putExtra("date",date);
                it.putExtra("action", act);
                startActivity(it);
                finish();
            }
        });

        Button addAction = (Button) findViewById(R.id.button_add_action);
        addAction.setOnClickListener(this);
    }

    public void Classinit(String date) {//获取当天的所有日程信息
        String title = new String();
        String content = new String();
        String beginDate = new String();
        String beginTime = new String();
        String endDate = new String();
        String endTime = new String();
        String repeat = new String();
        try {
            String localFile = getFilesDir().getAbsolutePath();
            File f = new File(localFile + "/" + "actions.txt");
            if (!f.exists()) {
                f.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(f, "rw");

            if(raf.length()!=0){
                raf.seek(0);

                while (true) {
                    title = raf.readUTF();
                    content =raf.readUTF();
                    beginDate = raf.readUTF();
                    beginTime = raf.readUTF();
                    endDate =raf.readUTF();
                    endTime =raf.readUTF();
                    repeat =raf.readUTF();

                    Action a = new Action(title
                            , content
                            , beginDate
                            , beginTime
                            , endDate
                            , endTime
                            , repeat);

                    compareDate(date,a);
                }
            }
            Collections.sort(list);
        }catch(Exception e){
//            e.printStackTrace();
        }
    }

    void compareDate(String date,Action a){
        String month1 = date.substring(5 ,7);
        String month2 = a.getBeginDate().substring(5 ,7);
        String day1 = date.substring(8, 10);
        String day2 = a.getBeginDate().substring(8, 10);

        switch(a.getRepeat()){
            case "关闭":
                if(date.equals(a.getBeginDate())){
                    list.add(a);
                }
                break;
            case "每天":
                list.add(a);
                break;
            case "每周":
                if(dateToWeek(date).equals(dateToWeek(a.getBeginDate()))){
                    list.add(a);
                }
                break;
            case "每月":
                if(day1.equals(day2)){
                    list.add(a);
                }
                break;
            case "每年":
                if(month1.equals(month2)&&day1.equals(day2)){
                    list.add(a);
                }
                break;
        }
    }

    String dateToWeek(String date){
            String dayOfweek = "-1";
            try {
                SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy/MM/dd");
                Date myDate = myFormatter.parse(date);
                SimpleDateFormat formatter = new SimpleDateFormat("E");
                String str = formatter.format(myDate);
                dayOfweek = str;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return dayOfweek;
    }

    @Override
    public void onClick(View view) {
        Intent it = new Intent(ActionsOfDay.this, AddAction.class);
        it.putExtra("date",date);
        startActivity(it);
        finish();
    }
}

