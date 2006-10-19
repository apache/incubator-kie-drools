package org.drools.base.extractors;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;

public abstract class BaseCharClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 91214567753008212L;

    public BaseCharClassFieldExtractor(final Class clazz,
                                       final String fieldName) {
        super( clazz,
               fieldName );
    }

    public Object getValue(final Object object) {
        return new Long( getCharValue( object ) );
    }

    public boolean getBooleanValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from char" );
    }

    public byte getByteValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to byte not supported from char" );
    }

    public abstract char getCharValue(Object object);

    public double getDoubleValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to double not supported from char" );
    }

    public float getFloatValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to float not supported from char" );
    }

    public int getIntValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to int not supported from char" );
    }

    public long getLongValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to long not supported from char" );
    }

    public short getShortValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to short not supported from char" );
    }

}
