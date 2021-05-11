package com.fsdm.wisd.scancard;

public class UserLog {

    int id,state;
    String uid,name,date;

    public UserLog(int id, int state, String uid, String name, String date) {
        this.id = id;
        this.state = state;
        this.uid = uid;
        this.name = name;
        this.date = date;
    }

    public UserLog(int state, String uid, String name, String date) {
        this.state = state;
        this.uid = uid;
        this.name = name;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getState() {
        return state;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
