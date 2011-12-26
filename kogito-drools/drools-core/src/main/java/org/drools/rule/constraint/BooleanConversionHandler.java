package org.drools.rule.constraint;

import org.mvel2.ConversionHandler;

public class BooleanConversionHandler implements ConversionHandler {

    public static final BooleanConversionHandler INSTANCE = new BooleanConversionHandler();

    private BooleanConversionHandler() { }

    public Object convertFrom(Object in) {
        if (in.getClass() == Boolean.class || in.getClass() == boolean.class) return in;
        if (in instanceof String) {
            if (((String)in).equalsIgnoreCase("true")) return true;
            if (((String)in).equalsIgnoreCase("false")) return false;
        }
        throw new ClassCastException("Cannot convert " + in + " into a Boolean");
    }

    public boolean canConvertFrom(Class cls) {
        return cls == Boolean.class || cls == boolean.class || cls == String.class;
    }
}
