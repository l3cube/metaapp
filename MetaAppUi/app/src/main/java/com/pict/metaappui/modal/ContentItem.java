package com.pict.metaappui.modal;

/**
 * Created by tushar on 29/2/16.
 */

public class ContentItem {
    private String name;
    private int imageResId;
    private String pattern;

    public ContentItem(String name, int imageResId, String pattern) {
        this.name = name;
        this.imageResId = imageResId;
        this.pattern = pattern;
    }

    public ContentItem(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
