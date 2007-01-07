package org.mvel.conversion;

import org.mvel.ConversionHandler;

public class StringCH implements ConversionHandler {
    public Object convertFrom(Object in) {
        return String.valueOf(in);
    }


    public boolean canConvertFrom(Class cls) {
        return true;
    }
}
