package com.pict.metaappui.modal;

/**
 * Created by tushar on 6/11/15.
 */
public class CategoryItem {

    private String Name;
    private String imageUrl;
    private boolean more;

    public CategoryItem() {

    }

    public CategoryItem(String Name, String imageUrl) {
        this.Name = Name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean getMore()
    {
        return more;
    }

    public void setMore(boolean more){
        this.more=more;
    }

}
