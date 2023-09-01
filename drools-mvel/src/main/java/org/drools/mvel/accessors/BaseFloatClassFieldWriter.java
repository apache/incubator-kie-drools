package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.core.base.BaseClassFieldWriter;
import org.drools.base.base.ValueType;

public abstract class BaseFloatClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 510l;

    public BaseFloatClassFieldWriter(final Class< ? > clazz,
                                     final String fieldName) {
        super( clazz,
               fieldName );
    }

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseFloatClassFieldWriter(final int index,
                                        final Class< ? > fieldType,
                                        final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseFloatClassFieldWriter() {
    }

    public void setValue(final Object bean,
                         final Object value) {
        setFloatValue( bean,
                       value == null ? 0 : ((Number) value).floatValue() );
    }

    public void setBooleanValue(final Object bean,
                                final boolean value) {
        throw new RuntimeException( "Conversion to float not supported from boolean" );
    }

    public void setByteValue(final Object bean,
                             final byte value) {
        setFloatValue( bean,
                       value);
    }

    public void setCharValue(final Object bean,
                             final char value) {
        throw new RuntimeException( "Conversion to float not supported from char" );
    }

    public void setDoubleValue(final Object bean,
                               final double value) {
        setFloatValue( bean,
                       (float) value );
    }

    public abstract void setFloatValue(final Object object,
                                       final float value);

    public void setIntValue(final Object bean,
                            final int value) {
        setFloatValue( bean,
                       (float) value );
    }

    public void setLongValue(final Object bean,
                             final long value) {
        setFloatValue( bean,
                       (float) value );
    }

    public void setShortValue(final Object bean,
                              final short value) {
        setFloatValue( bean,
                       value);
    }

    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod("setFloatValue",
                                                     Object.class, float.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

}
