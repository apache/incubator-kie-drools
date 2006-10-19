package org.drools.base.extractors;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;

public abstract class BaseDoubleClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 20311112843487706L;

    public BaseDoubleClassFieldExtractor(final Class clazz,
                                         final String fieldName) {
        super( clazz,
               fieldName );
    }

    public Object getValue(final Object object) {
        return new Double( getDoubleValue( object ) );
    }

    public boolean getBooleanValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from double" );
    }

    public byte getByteValue(final Object object) {
        return (byte) getDoubleValue( object );

    }

    public char getCharValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to char not supported from double" );
    }

    public abstract double getDoubleValue(Object object);

    public float getFloatValue(final Object object) {
        return (float) getDoubleValue( object );
    }

    public int getIntValue(final Object object) {
        return (int) getDoubleValue( object );
    }

    public long getLongValue(final Object object) {
        return (long) getDoubleValue( object );
    }

    public short getShortValue(final Object object) {
        return (short) getDoubleValue( object );
    }

}
