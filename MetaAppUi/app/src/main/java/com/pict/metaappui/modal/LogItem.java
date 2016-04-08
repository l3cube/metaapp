package com.pict.metaappui.modal;

/**
 * Created by tushar on 16/3/16.
 */
public class LogItem {
    String name;
    String log;
    String timestamp;

    public LogItem() {
    }

    public LogItem(String name, String log, String timestamp) {
        this.name = name;
        this.log = log;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
