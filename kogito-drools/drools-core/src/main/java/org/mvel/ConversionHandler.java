package org.mvel;

public interface ConversionHandler {
    public Object convertFrom(Object in);
    public boolean canConvertFrom(Class cls);
}
