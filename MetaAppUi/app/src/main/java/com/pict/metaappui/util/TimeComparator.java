package com.pict.metaappui.util;

import com.pict.metaappui.modal.RequestChild;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class TimeComparator implements Comparator<RequestChild> {
    @Override
    public int compare(RequestChild lhs, RequestChild rhs) {
        try {
            DateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
            Date lhsd=formater.parse(lhs.getTime_to_complete());
            Date rhsd=formater.parse(rhs.getTime_to_complete());
            if(lhsd.before(rhsd))
                return -1;
            if(lhsd.after(rhsd))
                return 1;
            return 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
