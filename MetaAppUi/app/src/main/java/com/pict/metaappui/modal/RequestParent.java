package com.pict.metaappui.modal;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by tushar on 26/12/15.
 */
public class RequestParent implements ParentListItem {

    int RequestId;
    String Topic;
    String Intent_desc;
    String Deadline;
    int no_of_responses;
    List<RequestChild> Responses;

    public RequestParent() {
    }

    public RequestParent(int requestId, String topic, String intent_desc, String deadline, int no_of_responses, List<RequestChild> responses) {
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

    public List<RequestChild> getResponses() {
        return Responses;
    }

    public void setResponses(List<RequestChild> responses) {
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

    @Override
    public List<RequestChild> getChildItemList() {
        return Responses;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
