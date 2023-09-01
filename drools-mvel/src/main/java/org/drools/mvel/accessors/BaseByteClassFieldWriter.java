package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.core.base.BaseClassFieldWriter;
import org.drools.base.base.ValueType;

/**
 * A Base class for primitive byte class field
 * writer. This class centralizes type conversions.
 */
public abstract class BaseByteClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 510l;

    public BaseByteClassFieldWriter(final Class< ? > clazz,
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
    protected BaseByteClassFieldWriter(final int index,
                                       final Class< ? > fieldType,
                                       final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseByteClassFieldWriter() {
    }

    public void setValue(final Object bean,
                         final Object value) {
        setByteValue( bean,
                      value == null ? 0 : ((Number) value).byteValue() );
    }

    public void setBooleanValue(final Object bean,
                                final boolean value) {
        throw new RuntimeException( "Conversion to byte not supported from boolean" );
    }

    public abstract void setByteValue(final Object bean,
                                      final byte value);

    public void setCharValue(final Object bean,
                             final char value) {
        throw new RuntimeException( "Conversion to byte not supported from char" );
    }

    public void setDoubleValue(final Object bean,
                               final double value) {
        setByteValue( bean,
                      (byte) value );
    }

    public void setFloatValue(final Object bean,
                              final float value) {
        setByteValue( bean,
                      (byte) value );
    }

    public void setIntValue(final Object bean,
                            final int value) {
        setByteValue( bean,
                      (byte) value );
    }

    public void setLongValue(final Object bean,
                             final long value) {
        setByteValue( bean,
                      (byte) value );
    }

    public void setShortValue(final Object bean,
                              final short value) {
        setByteValue( bean,
                      (byte) value );
    }

    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod("setByteValue",
                                                     Object.class, byte.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

}
