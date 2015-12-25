package com.pict.metaappui.modal;

import java.util.List;

/**
 * Created by tushar on 23/12/15.
 */
public class FulfilledRequest {
    int RequestId;
    String Topic;
    String Intent_desc;
    String Deadline;
    int no_of_responses;
    List<UserResponses> Responses;

    public FulfilledRequest() {
    }

    public FulfilledRequest(int requestId, String topic, String intent_desc, String deadline, int no_of_responses, List<UserResponses> responses) {
        RequestId = requestId;
        Topic = topic;
        Intent_desc = intent_desc;
        Deadline = deadline;
        this.no_of_responses = no_of_responses;
        Responses = responses;
    }

    public int getRequestId() {
        return RequestId;
    }

    public void setRequestId(int requestId) {
        RequestId = requestId;
    }

    public List<UserResponses> getResponses() {
        return Responses;
    }

    public void setResponses(List<UserResponses> responses) {
        Responses = responses;
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

    public int getNo_of_responses() {
        return no_of_responses;
    }

    public void setNo_of_responses(int no_of_responses) {
        this.no_of_responses = no_of_responses;
    }
}
