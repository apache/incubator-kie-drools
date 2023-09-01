package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.core.base.BaseClassFieldWriter;
import org.drools.base.base.ValueType;

public abstract class BaseDoubleClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 510l;

    public BaseDoubleClassFieldWriter(final Class< ? > clazz,
                                      final String fieldName) {
        super( clazz,
               fieldName );
    }

    /**
     * This constructor is not supposed to be used from outside the class hierarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseDoubleClassFieldWriter(final int index,
                                         final Class< ? > fieldType,
                                         final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseDoubleClassFieldWriter() {
    }

    public void setValue(final Object bean,
                         final Object value) {
        setDoubleValue( bean,
                        value == null ? 0 : ((Number) value).doubleValue() );
    }

    public void setBooleanValue(final Object bean,
                                final boolean value) {
        throw new RuntimeException( "Conversion to double not supported from boolean" );
    }

    public void setByteValue(final Object bean,
                             final byte value) {
        setDoubleValue( bean,
                        value);

    }

    public void setCharValue(final Object bean,
                             final char value) {
        throw new RuntimeException( "Conversion to double not supported from char" );
    }

    public abstract void setDoubleValue(final Object object,
                                        final double value);

    public void setFloatValue(final Object bean,
                              final float value) {
        setDoubleValue( bean,
                        value);
    }

    public void setIntValue(final Object bean,
                            final int value) {
        setDoubleValue( bean,
                        value);
    }

    public void setLongValue(final Object bean,
                             final long value) {
        setDoubleValue( bean,
                        (double) value );
    }

    public void setShortValue(final Object bean,
                              final short value) {
        setDoubleValue( bean,
                        value);
    }

    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod("setDoubleValue",
                                                     Object.class, double.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

}
