package org.drools.base.extractors;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;

public abstract class BaseIntClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 203112843487706L;

    public BaseIntClassFieldExtractor(final Class clazz,
                                      final String fieldName) {
        super( clazz,
               fieldName );
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

}
