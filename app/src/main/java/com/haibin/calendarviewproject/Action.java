package com.haibin.calendarviewproject;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Action implements Parcelable,Comparable<Action>{
    private String title;
    private String content;
    private String beginDate;
    private String beginTime;
    private String endDate;
    private String endTime;
    private String repeat;

    public Action(String title, String content, String beginDate, String beginTime, String endDate, String endTime, String repeat) {
        this.title = title;
        this.content = content;
        this.beginDate = beginDate;
        this.beginTime = beginTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.repeat = repeat;
    }

    public static final Creator<Action> CREATOR = new Creator<Action>() {
        @Override
        public Action createFromParcel(Parcel in) {
            return new Action(in);
        }

        @Override
        public Action[] newArray(int size) {
            return new Action[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getTitle());
        parcel.writeString(getContent());
        parcel.writeString(getBeginDate());
        parcel.writeString(getBeginTime());
        parcel.writeString(getEndDate());
        parcel.writeString(getEndTime());
        parcel.writeString(getRepeat());
    }

    protected Action(Parcel in) {
        title = in.readString();
        content = in.readString();
        beginDate = in.readString();
        beginTime = in.readString();
        endDate = in.readString();
        endTime = in.readString();
        repeat = in.readString();
    }

    @Override
    public int compareTo(@NonNull Action action) {
        int hour1 = Integer.parseInt(this.getBeginTime().substring(0 ,2));
        int hour2 = Integer.parseInt(action.getBeginTime().substring(0 ,2));
        int minute1 = Integer.parseInt(this.getBeginTime().substring(3, 5));
        int minute2 = Integer.parseInt(action.getBeginTime().substring(3, 5));
        int i = hour1-hour2;//先判断小时大小
        if(i==0){
            i=minute1-minute2;//小时相等再判断分钟大小
        }
        return i;
    }
}
