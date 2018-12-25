package com.haibin.calendarviewproject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){//弹出小窗口提醒日程
        try{

            Action act = intent.getParcelableExtra("action");
//            Toast.makeText(context, act.getContent(), Toast.LENGTH_SHORT).show();
//日程排序和返回事件
            AlertDialog.Builder builder = new AlertDialog.Builder(context).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setTitle("日程提醒");
            StringAlign align = new StringAlign(90, StringAlign.Alignment.CENTER);//调用构造方法，设置字符串对齐为居中对齐，最大长度为200
            String str = align.format(act.getTitle())+"\r\n"+align.format(act.getContent())+"\r\n"+align.format("开始："+act.getBeginTime())+"\r\n"+align.format("结束："+act.getEndTime());
            dialog.setMessage(str);
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
