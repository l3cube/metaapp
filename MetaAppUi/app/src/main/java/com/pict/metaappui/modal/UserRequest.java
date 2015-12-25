package com.pict.metaappui.modal;

/**
 * Created by tushar on 23/12/15.
 */
public class UserRequest {
    int id;
    int requestId;
    String topic;
    String intent_desc;
    String deadline;
    boolean pending;

    public UserRequest(){

    }

    public UserRequest(int requestId,String topic,String intent_desc,String deadline,boolean pending){
        this.requestId=requestId;
        this.topic=topic;
        this.intent_desc=intent_desc;
        this.deadline=deadline;
        this.pending=pending;
    }

    public UserRequest(int id,int requestId,String topic,String intent_desc,String deadline,boolean pending){
        this.id=id;
        this.requestId=requestId;
        this.topic=topic;
        this.intent_desc=intent_desc;
        this.deadline=deadline;
        this.pending=pending;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getIntent_desc() {
        return intent_desc;
    }

    public void setIntent_desc(String intent_desc) {
        this.intent_desc = intent_desc;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
