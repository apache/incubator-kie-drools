package org.mvel.conversion;

import org.mvel.ConversionHandler;

public class ObjectCH implements ConversionHandler {
    public Object convertFrom(Object in) {
        return in;
    }


    public boolean canConvertFrom(Class cls) {
        return true;
    }
}
