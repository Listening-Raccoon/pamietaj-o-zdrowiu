package com.example.pamietajozdrowiu;



public class ScheduleInstanceModel {
    final private String id;
    final private String name;
    final private String date;
    final private String time;

    public ScheduleInstanceModel(String id, String name, String date, String time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public ScheduleInstanceModel(String name, String date, String time) {
        this.id = "";
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
