package com.pict.metaappui.modal;

/**
 * Created by tushar on 15/3/16.
 */
public class SellerRank {
    String name;
    double rank;
    int rating;

    public SellerRank(String name, double rank,int rating) {
        this.name = name;
        this.rank = rank;
        this.rating = rating;
    }

    public SellerRank() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
