package org.drools.base.extractors;

import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;
import org.drools.base.ValueType;

public abstract class BaseLongClassFieldExtractors extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 2031113412843487706L;

    public BaseLongClassFieldExtractors(final Class clazz,
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
    protected BaseLongClassFieldExtractors(final int index,
                                           final Class fieldType,
                                           final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public Object getValue(final Object object) {
        return new Long( getLongValue( object ) );
    }

    public boolean getBooleanValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from long" );
    }

    public byte getByteValue(final Object object) {
        return (byte) getLongValue( object );

    }

    public char getCharValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to char not supported from long" );
    }

    public double getDoubleValue(final Object object) {
        return getLongValue( object );
    }

    public float getFloatValue(final Object object) {
        return getLongValue( object );
    }

    public int getIntValue(final Object object) {
        return (int) getLongValue( object );
    }

    public abstract long getLongValue(Object object);

    public short getShortValue(final Object object) {
        return (short) getLongValue( object );
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getLongValue",
                                                      new Class[]{Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(final Object object) {
        final long temp = getLongValue( object );
        return (int) (temp ^ (temp >>> 32));
    }

}
