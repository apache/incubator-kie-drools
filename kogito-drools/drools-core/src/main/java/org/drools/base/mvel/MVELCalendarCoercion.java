package org.drools.base.mvel;

import java.util.Calendar;

import org.drools.base.evaluators.DateFactory;
import org.mvel.ConversionHandler;

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
            cal.setTime( DateFactory.parseDate( (String) o) );
            return cal;
        } else {
            return o;
        }
    }

}
