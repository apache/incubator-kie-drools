package org.drools.base.extractors;

import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;
import org.drools.base.ValueType;

public abstract class BaseIntClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 203112843487706L;

    public BaseIntClassFieldExtractor(final Class clazz,
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
    protected BaseIntClassFieldExtractor(final int index,
                                         final Class fieldType,
                                         final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public Object getValue(final Object object) {
        return new Long( getIntValue( object ) );
    }

    public boolean getBooleanValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from int" );
    }

    public byte getByteValue(final Object object) {
        return (byte) getIntValue( object );

    }

    public char getCharValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to char not supported from int" );
    }

    public double getDoubleValue(final Object object) {
        return getIntValue( object );
    }

    public float getFloatValue(final Object object) {
        return getIntValue( object );
    }

    public abstract int getIntValue(Object object);

    public long getLongValue(final Object object) {
        return getIntValue( object );
    }

    public short getShortValue(final Object object) {
        return (short) getIntValue( object );
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getIntValue",
                                                      new Class[]{Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(final Object object) {
        return getIntValue( object );
    }
}
