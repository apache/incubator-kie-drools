package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.core.base.BaseClassFieldWriter;
import org.drools.base.base.ValueType;

public abstract class BaseCharClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 510l;

    public BaseCharClassFieldWriter(final Class< ? > clazz,
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
    protected BaseCharClassFieldWriter(final int index,
                                       final Class< ? > fieldType,
                                       final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseCharClassFieldWriter() {
    }

    public void setValue(final Object bean,
                         final Object value) {
        setCharValue( bean,
                     value == null ? '\0' : ((Character) value).charValue() );
    }

    public void setBooleanValue(final Object bean,
                                final boolean value) {
        throw new RuntimeException( "Conversion to char not supported from boolean" );
    }

    public void setByteValue(final Object bean,
                             final byte value) {
        setCharValue( bean,
                      (char) value );
    }

    public abstract void setCharValue(final Object object,
                                      final char value);

    public void setDoubleValue(final Object bean,
                               final double value) {
        setCharValue( bean,
                      (char) value );
    }

    public void setFloatValue(final Object bean,
                              final float value) {
        setCharValue( bean,
                      (char) value );
    }

    public void setIntValue(final Object bean,
                            final int value) {
        setCharValue( bean,
                      (char) value );
    }

    public void setLongValue(final Object bean,
                             final long value) {
        setCharValue( bean,
                      (char) value );
    }

    public void setShortValue(final Object bean,
                              final short value) {
        setCharValue( bean,
                      (char) value );
    }

    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod("setCharValue",
                                                     Object.class, char.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }
}
