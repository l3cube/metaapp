package com.pict.metaappui.util;

import com.pict.metaappui.modal.RequestChild;

import java.util.Comparator;

public class RatingComparator implements Comparator<RequestChild> {
    @Override
    public int compare(RequestChild lhs, RequestChild rhs) {
        return -(lhs.getRating() - rhs.getRating());
    }
}
