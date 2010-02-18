package org.drools.base.mvel;

import java.util.Calendar;

import org.drools.core.util.DateUtils;
import org.drools.type.DateFormatsImpl;
import org.mvel2.ConversionHandler;

public class MVELCalendarCoercion implements ConversionHandler {

    public boolean canConvertFrom(Class cls) {
        if (cls == String.class || cls.isAssignableFrom( Calendar.class )) {
            return true;
        } else {
            return false;
        }
    }

    public Object convertFrom(Object o) {
        if (o instanceof String) {
            Calendar cal = Calendar.getInstance();
            cal.setTime( DateUtils.parseDate( (String) o, DateFormatsImpl.dateFormats.get() ) );
            return cal;
        } else {
            return o;
        }
    }

}
