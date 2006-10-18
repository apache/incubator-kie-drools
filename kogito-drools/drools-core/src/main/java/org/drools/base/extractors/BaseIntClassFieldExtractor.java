package org.drools.base.extractors;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;

public abstract class BaseIntClassFieldExtractor extends BaseClassFieldExtractor {


    private static final long serialVersionUID = 203112843487706L;
    
    public BaseIntClassFieldExtractor(Class clazz,
                                       String fieldName) {
        super( clazz,
               fieldName );
    }

    public Object getValue(Object object) {
        return new Long( getIntValue( object ) );
    }

    public boolean getBooleanValue(Object object) {
        throw new RuntimeDroolsException("Conversion to boolean not supported from int");
    }

    public byte getByteValue(Object object) {
        return (byte) getIntValue( object );
        
    }

    public char getCharValue(Object object) {
        throw new RuntimeDroolsException("Conversion to char not supported from int");
    }

    public double getDoubleValue(Object object) {
        return getIntValue( object );
    }

    public float getFloatValue(Object object) {
        return getIntValue( object );
    }

    public abstract int getIntValue(Object object);

    public long getLongValue(Object object) {
        return getIntValue( object );
    }

    public short getShortValue(Object object) {
        return (short) getIntValue( object );
    }

}
