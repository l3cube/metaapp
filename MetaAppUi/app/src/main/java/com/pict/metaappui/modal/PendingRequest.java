package com.pict.metaappui.modal;

/**
 * Created by tushar on 23/12/15.
 */
public class PendingRequest {
    int RequestId;
    String Topic;
    String Intent_desc;
    String Deadline;

    public PendingRequest() {
    }

    public PendingRequest(int requestId, String topic, String intent_desc, String deadline) {
        RequestId = requestId;
        Topic = topic;
        Intent_desc = intent_desc;
        Deadline = deadline;
    }

    public int getRequestId() {
        return RequestId;
    }

    public void setRequestId(int requestId) {
        RequestId = requestId;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }

    public String getIntent_desc() {
        return Intent_desc;
    }

    public void setIntent_desc(String intent_desc) {
        Intent_desc = intent_desc;
    }

    public String getDeadline() {
        return Deadline;
    }

    public void setDeadline(String deadline) {
        Deadline = deadline;
    }
}
