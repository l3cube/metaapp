package com.pict.metaappui.util;

import com.pict.metaappui.modal.RequestChild;

import java.util.Comparator;

public class CostComparator implements Comparator<RequestChild> {
    @Override
    public int compare(RequestChild lhs, RequestChild rhs) {
        return (int) (lhs.getCost()-rhs.getCost());
    }
}
