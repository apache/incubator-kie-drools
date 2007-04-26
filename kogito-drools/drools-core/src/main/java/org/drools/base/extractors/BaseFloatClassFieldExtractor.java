package org.drools.base.extractors;

import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;
import org.drools.base.ValueType;

public abstract class BaseFloatClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 20311112843487706L;

    public BaseFloatClassFieldExtractor(final Class clazz,
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
    protected BaseFloatClassFieldExtractor(final int index,
                                           final Class fieldType,
                                           final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public Object getValue(final Object object) {
        return new Float( getFloatValue( object ) );
    }

    public boolean getBooleanValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from float" );
    }

    public byte getByteValue(final Object object) {
        return (byte) getFloatValue( object );

    }

    public char getCharValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to char not supported from float" );
    }

    public double getDoubleValue(final Object object) {
        return getFloatValue( object );
    }

    public abstract float getFloatValue(Object object);

    public int getIntValue(final Object object) {
        return (int) getFloatValue( object );
    }

    public long getLongValue(final Object object) {
        return (long) getFloatValue( object );
    }

    public short getShortValue(final Object object) {
        return (short) getFloatValue( object );
    }

    public boolean isNullValue(final Object object) {
        return false;
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getFloatValue",
                                                      new Class[]{Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(final Object object) {
        return Float.floatToIntBits( getFloatValue( object ) );
    }

}
