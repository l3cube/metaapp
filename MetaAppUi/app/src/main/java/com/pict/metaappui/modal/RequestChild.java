package com.pict.metaappui.modal;

/**
 * Created by tushar on 26/12/15.
 */
public class RequestChild {
    int requestId;
    String topic;
    String service_desc;
    String time_to_complete;
    float cost;
    String seller;
    double rank;
    int rating;

    public RequestChild() {

    }

    public RequestChild(int requestId, String topic, String service_desc, String time_to_complete,float cost,String seller,double rank,int rating) {
        this.requestId = requestId;
        this.topic = topic;
        this.service_desc=service_desc;
        this.time_to_complete=time_to_complete;
        this.cost=cost;
        this.seller=seller;
        this.rank = rank;
        this.rating = rating;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getService_desc() {
        return service_desc;
    }

    public void setService_desc(String service_desc) {
        this.service_desc = service_desc;
    }

    public String getTime_to_complete() {
        return time_to_complete;
    }

    public void setTime_to_complete(String time_to_complete) {
        this.time_to_complete = time_to_complete;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
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
