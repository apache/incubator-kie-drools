package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.core.base.BaseClassFieldWriter;
import org.drools.base.base.ValueType;

/**
 * A Base class for primitive boolean class field
 * write accessors. This class centralizes type conversions.
 */
public abstract class BaseBooleanClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 510l;

    public BaseBooleanClassFieldWriter(final Class< ? > clazz,
                                       final String fieldName) {
        super( clazz,
               fieldName );
    }

    public BaseBooleanClassFieldWriter() {
    }

    /**
     * This constructor is not supposed to be used from outside the class hierarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseBooleanClassFieldWriter(final int index,
                                          final Class< ? > fieldType,
                                          final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public void setValue(final Object bean,
                         final Object value) {
        setBooleanValue( bean,
                         value == null ? false : ((Boolean) value).booleanValue() );
    }

    public abstract void setBooleanValue(final Object bean,
                                         final boolean value);

    public void setByteValue(final Object bean,
                             final byte value) {
        throw new RuntimeException( "Conversion to boolean not supported from byte" );
    }

    public void setCharValue(final Object bean,
                             final char value) {
        throw new RuntimeException( "Conversion to boolean not supported from char" );
    }

    public void setDoubleValue(final Object bean,
                               final double value) {
        throw new RuntimeException( "Conversion to boolean not supported from double" );
    }

    public void setFloatValue(final Object bean,
                              final float value) {
        throw new RuntimeException( "Conversion to boolean not supported from float" );
    }

    public void setIntValue(final Object bean,
                            final int value) {
        throw new RuntimeException( "Conversion to boolean not supported from int" );
    }

    public void setLongValue(final Object bean,
                             final long value) {
        throw new RuntimeException( "Conversion to boolean not supported from long" );
    }

    public void setShortValue(final Object bean,
                              final short value) {
        throw new RuntimeException( "Conversion to boolean not supported from short" );
    }

    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod("setBooleanValue",
                                                     Object.class, boolean.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

}
