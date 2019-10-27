package com.app.notemii;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Note {
    private String id;
    private String title;
    private String content;
    private String date;
    private String alarmDate;
    private String alarmTime;

    public Note (){}

    public Note(String id, String title, String content,String date, String alarmDate, String alarmTime){
        this.date=date;
        this.content=content;
        this.id=id;
        this.title=title;
        this.alarmDate=alarmDate;
        this.alarmTime = alarmTime;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("content", content);
        result.put("id", id);
        result.put("title", title);
        result.put("alarmDate", alarmDate);
        result.put("alarmTime", alarmTime);

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAlarmDate() {
        return alarmDate;
    }

    public String getAlarmTime() {
        return alarmTime;
    }
}
