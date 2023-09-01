package org.drools.mvel.expr;

import java.util.Calendar;

import org.drools.util.DateUtils;
import org.mvel2.ConversionHandler;

public class MVELCalendarCoercion implements ConversionHandler {

    public boolean canConvertFrom(Class cls) {
        return cls == String.class || cls.isAssignableFrom( Calendar.class );
    }

    public Object convertFrom(Object o) {
        if (o instanceof String) {
            Calendar cal = Calendar.getInstance();
            cal.setTime( DateUtils.parseDate( (String) o ) );
            return cal;
        } else {
            return o;
        }
    }

}
