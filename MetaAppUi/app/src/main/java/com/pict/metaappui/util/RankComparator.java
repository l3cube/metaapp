package com.pict.metaappui.util;

import com.pict.metaappui.modal.RequestChild;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by tushar on 15/3/16.
 */
public class RankComparator implements Comparator<RequestChild> {
    @Override
    public int compare(RequestChild lhs, RequestChild rhs) {
        return (int)-((lhs.getRank()-rhs.getRank())*10);
    }
}

