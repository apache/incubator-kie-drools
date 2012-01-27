package org.drools.rule.constraint;

import org.mvel2.ConversionHandler;

public class BooleanConversionHandler implements ConversionHandler {

    public static final BooleanConversionHandler INSTANCE = new BooleanConversionHandler();

    private BooleanConversionHandler() { }

    public Object convertFrom(Object in) {
        if (in.getClass() == Boolean.class || in.getClass() == boolean.class) {
            return in;
        }
        return in instanceof String && ((String)in).equalsIgnoreCase("true");
    }

    public boolean canConvertFrom(Class cls) {
        return cls == Boolean.class || cls == boolean.class || cls == String.class;
    }
}
