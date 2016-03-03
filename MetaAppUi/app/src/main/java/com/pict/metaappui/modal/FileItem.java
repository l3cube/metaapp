package com.pict.metaappui.modal;

/**
 * Created by tushar on 1/3/16.
 */
public class FileItem {
    String name;
    String location;
    boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public FileItem() {
    }

    public FileItem(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
