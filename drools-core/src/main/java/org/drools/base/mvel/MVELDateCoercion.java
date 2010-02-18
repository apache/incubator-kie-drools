package org.drools.base.mvel;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.util.DateUtils;
import org.drools.type.DateFormats;
import org.drools.type.DateFormatsImpl;
import org.mvel2.ConversionHandler;

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
            return DateUtils.parseDate( (String) o, DateFormatsImpl.dateFormats.get() );
        } else {
            return o;
        }
    }

}
