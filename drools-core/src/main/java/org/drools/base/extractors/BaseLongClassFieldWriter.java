package org.drools.base.extractors;

import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldWriter;
import org.drools.base.ValueType;

public abstract class BaseLongClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 400L;

    public BaseLongClassFieldWriter(final Class< ? > clazz,
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
    protected BaseLongClassFieldWriter(final int index,
                                       final Class< ? > fieldType,
                                       final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public void setValue(final Object bean,
                         final Object value) {
        setLongValue( bean,
                      value == null ? 0 : ((Number) value).longValue() );
    }

    public void setBooleanValue(final Object bean,
                                final boolean value) {
        throw new RuntimeDroolsException( "Conversion to long not supported from boolean" );
    }

    public void setByteValue(final Object bean,
                             final byte value) {
        setLongValue( bean,
                      (long) value );

    }

    public void setCharValue(final Object bean,
                             final char value) {
        throw new RuntimeDroolsException( "Conversion to long not supported from char" );
    }

    public void setDoubleValue(final Object bean,
                               final double value) {
        setLongValue( bean,
                      (long) value );
    }

    public void setFloatValue(final Object bean,
                              final float value) {
        setLongValue( bean,
                      (long) value );
    }

    public void setIntValue(final Object bean,
                            final int value) {
        setLongValue( bean,
                      (long) value );
    }

    public abstract void setLongValue(final Object object,
                                      final long value);

    public void setShortValue(final Object bean,
                              final short value) {
        setLongValue( bean,
                      (long) value );
    }

    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod( "setLongValue",
                                                      new Class[]{Object.class, long.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

}
