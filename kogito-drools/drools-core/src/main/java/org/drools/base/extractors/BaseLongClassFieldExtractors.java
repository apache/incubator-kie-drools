package org.drools.base.extractors;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;

public abstract class BaseLongClassFieldExtractors extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 2031113412843487706L;

    public BaseLongClassFieldExtractors(final Class clazz,
                                        final String fieldName) {
        super( clazz,
               fieldName );
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

}
