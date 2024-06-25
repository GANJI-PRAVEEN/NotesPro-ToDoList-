package com.example.notespro;

import java.io.Serializable;
import java.util.Date;


public class NotesModalClass implements Serializable {
    String title;
    String content;
    String Id;
    Date date;
    int day;
    int month;

    public NotesModalClass(){}

    public NotesModalClass(String title, String content, Date date, int day, int month) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.day = day;
        this.month = month;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
