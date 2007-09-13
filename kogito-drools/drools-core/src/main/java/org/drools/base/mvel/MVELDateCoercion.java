package org.drools.base.mvel;

import java.util.Date;

import org.drools.base.evaluators.DateFactory;
import org.mvel.ConversionHandler;

public class MVELDateCoercion implements ConversionHandler {

    public boolean canConvertFrom(Class cls) {
        if (cls == String.class || cls.isAssignableFrom( Date.class )) {
            return true;
        } else {
            return false;
        }
    }

    public Object convertFrom(Object o) {
        if (o instanceof String) {
            return DateFactory.parseDate( (String) o);
        } else {
            return o;
        }
    }

}
