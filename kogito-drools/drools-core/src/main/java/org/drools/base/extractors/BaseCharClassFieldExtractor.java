package org.drools.base.extractors;

import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;
import org.drools.base.ValueType;

public abstract class BaseCharClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 91214567753008212L;

    public BaseCharClassFieldExtractor(final Class clazz,
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
    protected BaseCharClassFieldExtractor(final int index,
                                          final Class fieldType,
                                          final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public Object getValue(final Object object) {
        return new Long( getCharValue( object ) );
    }

    public boolean getBooleanValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from char" );
    }

    public byte getByteValue(final Object object) {
        return (byte) getCharValue( object );
    }

    public abstract char getCharValue(Object object);

    public double getDoubleValue(final Object object) {
        return getCharValue( object );
    }

    public float getFloatValue(final Object object) {
        return getCharValue( object );
    }

    public int getIntValue(final Object object) {
        return getCharValue( object );
    }

    public long getLongValue(final Object object) {
        return getCharValue( object );
    }

    public short getShortValue(final Object object) {
        return (short) getCharValue( object );
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getCharValue",
                                                      new Class[]{Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(final Object object) {
        return getCharValue( object );
    }
}
