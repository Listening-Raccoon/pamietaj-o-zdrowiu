package com.example.pamietajozdrowiu;

public class HistoryInstanceModel {
    private final String id;
    private final String date;
    private final String title;
    private final String content;

    public HistoryInstanceModel(String id, String date, String title, String content) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
