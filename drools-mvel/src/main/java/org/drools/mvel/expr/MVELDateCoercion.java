package org.drools.mvel.expr;

import java.util.Date;

import org.drools.util.DateUtils;
import org.mvel2.ConversionHandler;

public class MVELDateCoercion implements ConversionHandler {

    public boolean canConvertFrom(Class cls) {
        return cls == String.class || cls.isAssignableFrom( Date.class );
    }

    public Object convertFrom(Object o) {
        if (o instanceof String) {
            return DateUtils.parseDate( (String) o );
        } else {
            return o;
        }
    }

}
