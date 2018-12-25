package com.haibin.calendarviewproject;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ActionInfo extends AppCompatActivity implements View.OnClickListener {
    private Action act;
    private String date;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private EditText actionTitle;
    private EditText actionContent;
    private TextView beginDate;
    private TextView endDate;
    private TextView beginTime;
    private TextView endTime;
    private TextView repeat;
    private Button save;
    private Button delete;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        date = getIntent().getStringExtra("date");
        act = (Action) getIntent().getParcelableExtra("action");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_info);

        actionTitle = (EditText) findViewById(R.id.edit_action_title);
        actionContent = (EditText) findViewById(R.id.edit_action_content);
        beginDate = (TextView) findViewById(R.id.text_begin_date);
        endDate = (TextView) findViewById(R.id.text_end_date);
        beginTime = (TextView) findViewById(R.id.text_begin_time);
        endTime = (TextView) findViewById(R.id.text_end_time);
        repeat = (TextView) findViewById(R.id.text_choose_repeat);
        save = (Button) findViewById(R.id.button_save);
        delete = (Button) findViewById(R.id.button_delete);

        actionTitle.setText(act.getTitle());
        actionContent.setText(act.getContent());
        beginDate.setText(act.getBeginDate());
        beginTime.setText(act.getBeginTime());
        endDate.setText(act.getEndDate());
        endTime.setText(act.getEndTime());
        repeat.setText(act.getRepeat());

        beginDate.setOnClickListener(this);
        beginTime.setOnClickListener(this);
        endDate.setOnClickListener(this);
        endTime.setOnClickListener(this);
        repeat.setOnClickListener(this);
        save.setOnClickListener(this);
        delete.setOnClickListener(this);
        calendar = Calendar.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_begin_date:
            case R.id.text_end_date:
                chooseDate(v.getId());
                break;
            case R.id.text_begin_time:
            case R.id.text_end_time:
                chooseTime(v.getId());
                break;
            case R.id.button_save:
                Action newAct = new Action(actionTitle.getText().toString()
                        , actionContent.getText().toString()
                        , beginDate.getText().toString()
                        , beginTime.getText().toString()
                        , endDate.getText().toString()
                        , endTime.getText().toString()
                        , repeat.getText().toString());
                try {
                    UpdateFile(newAct);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //设置闹钟
                setAlarm(newAct);
                Intent it = new Intent(ActionInfo.this, ActionsOfDay.class);
                it.putExtra("date", date);
                startActivity(it);
                finish();
                break;
            case R.id.button_delete:
                //删除闹钟
                cancelFromFile(act);
                cancelAlarm(act);
                Intent it2 = new Intent(ActionInfo.this, ActionsOfDay.class);
                it2.putExtra("date", date);
                startActivity(it2);
                finish();
                break;
            case R.id.text_choose_repeat:
                final String[] items = new String[]{"关闭", "每天", "每周", "每月", "每年"};
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("重复")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                repeat.setText(items[which]);
                            }
                        }).create();
                dialog.show();
                break;
        }
    }

    private void chooseDate(int id) {
        final int ID = id;
        datePickerDialog = new DatePickerDialog(
                this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                String time = String.valueOf(year) + "/" + String.valueOf(monthOfYear + 1) + "/" + Integer.toString(dayOfMonth);
                TextView date = (TextView) findViewById(ID);
                date.setText(time);
            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
//        //自动弹出键盘问题解决
//        datePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void chooseTime(int id) {
        final int ID = id;
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                String hour = String.format("%02d", hourOfDay);
                String minute = String.format("%02d", minuteOfHour);
                TextView time = (TextView) findViewById(ID);
                time.setText(hour + ":" + minute);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
//        //自动弹出键盘问题解决
//        timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void setAlarm(Action act) {
        //创建Intent对象，action指向广播接收类，附加信息为字符串
        Intent intent = new Intent("MYALARMRECEIVER");
        intent.putExtra("action", act);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        long time = System.currentTimeMillis();
        try {
            time = dateformat.parse(act.getBeginDate() + " " + act.getBeginTime()).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //第二个参数可以用来区分不同日程,这里的time是一个独一无二的时间转换成的毫秒数
        PendingIntent pi = PendingIntent.getBroadcast(this, (int) time, intent, 0);
        switch (repeat.getText().toString()) {
            case "关闭":
                am.set(AlarmManager.RTC_WAKEUP, time, pi);
                break;
            case "每天":
                am.setRepeating(AlarmManager.RTC_WAKEUP, time, 24 * 60 * 60 * 1000, pi);
                break;
            case "每周":
                am.setRepeating(AlarmManager.RTC_WAKEUP, time, 7 * 24 * 60 * 60 * 1000, pi);
                break;
            case "每月":
                am.setRepeating(AlarmManager.RTC_WAKEUP, time, 30 * 24 * 60 * 60 * 1000, pi);
                break;
            case "每年":
                am.setRepeating(AlarmManager.RTC_WAKEUP, time, 365 * 24 * 60 * 60 * 1000, pi);
                break;
        }
    }

    void cancelAlarm(Action act) {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent("MYALARMRECEIVER");
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        long time = System.currentTimeMillis();
        try {
            time = dateformat.parse(act.getBeginDate() + " " + act.getBeginTime()).getTime();
            //第二个参数可以用来区分不同日程,这里的time是一个独一无二的时间转换成的毫秒数
            PendingIntent pi = PendingIntent.getBroadcast(this, (int) time, intent, 0);
            am.cancel(pi);//取消闹钟
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void UpdateFile(Action newAct) throws IOException {
        String localFile = getFilesDir().getAbsolutePath();
        File f = new File(localFile + "/" + "actions.txt");
        if (!f.exists()) {
            f.createNewFile();
        }
        RandomAccessFile raf = new RandomAccessFile(f, "rw");

        try {
            long beginPosition = 0;
            String title = "c";
            String content = "c";
            String beginDate = "c";
            String beginTime = "c";
            String endDate = "c";
            String endTime = "c";
            String repeat = "c";
            long endPosition = 0;

            if (raf.length() != 0) {
                raf.seek(0);

                while (true) {
                    beginPosition = raf.getFilePointer();
                    title = raf.readUTF();
                    content = raf.readUTF();
                    beginDate = raf.readUTF();
                    beginTime = raf.readUTF();
                    endDate = raf.readUTF();
                    endTime = raf.readUTF();
                    repeat = raf.readUTF();
                    endPosition = raf.getFilePointer();
                    if (beginDate.equals(act.getBeginDate()) && beginTime.equals(act.getBeginTime())) {
                        break;
                    }
                }
            }
            byte[] bytes1 = new byte[(int) beginPosition];
            raf.seek(0);
            raf.read(bytes1, 0, (int) beginPosition);//将待修改的日程前面的部分读出来
            byte[] bytes2 = new byte[(int) raf.length() - (int) endPosition];
            raf.seek(endPosition);
            raf.read(bytes2, 0, (int) raf.length() - (int) endPosition);//将待修改的日程后面的部分读出来

            f.delete();//删除原有文件

            File f2 = new File(localFile + "/" + "actions.txt");
            if (!f2.exists()) {
                f2.createNewFile();
            }
            RandomAccessFile raf2 = new RandomAccessFile(f2, "rw");
            raf2.write(bytes1);
            raf2.writeUTF(newAct.getTitle());
            raf2.writeUTF(newAct.getContent());
            raf2.writeUTF(newAct.getBeginDate());
            raf2.writeUTF(newAct.getBeginTime());
            raf2.writeUTF(newAct.getEndDate());
            raf2.writeUTF(newAct.getEndTime());
            raf2.writeUTF(newAct.getRepeat());
            raf2.write(bytes2);
            cancelAlarm(new Action(title
                    , content
                    , beginDate
                    , beginTime
                    , endDate
                    , endTime
                    , repeat));

        } catch (EOFException e) {
//            e.printStackTrace();
        }
    }

    void cancelFromFile(Action act) {
        try {
            String localFile = getFilesDir().getAbsolutePath();
            File f = new File(localFile + "/" + "actions.txt");
            if (!f.exists()) {
                f.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(f, "rw");

            long beginPosition = 0;
            long endPosition = 0;
            String currentDate = "c";
            String currentTime = "c";

            raf.seek(0);

            while (true) {
                beginPosition = raf.getFilePointer();
                raf.readUTF();
                raf.readUTF();
                currentDate = raf.readUTF();
                currentTime = raf.readUTF();
                raf.readUTF();
                raf.readUTF();
                raf.readUTF();
                endPosition = raf.getFilePointer();

                if (currentDate.equals(act.getBeginDate()) && currentTime.equals(act.getBeginTime())) {
                    break;
                }
            }
            byte[] bytes1 = new byte[(int) beginPosition];
            raf.seek(0);
            raf.read(bytes1, 0, (int) beginPosition);//将待删除的日程前面的部分读出来
            byte[] bytes2 = new byte[(int) raf.length() - (int) endPosition];
            raf.seek(endPosition);
            raf.read(bytes2, 0, (int) raf.length() - (int) endPosition);//将待删除的日程后面的部分读出来

            f.delete();//删除原有文件

            File f2 = new File(localFile + "/" + "actions.txt");
            if (!f2.exists()) {
                f2.createNewFile();
            }
            RandomAccessFile raf2 = new RandomAccessFile(f2, "rw");
            raf2.write(bytes1);
            raf2.write(bytes2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
