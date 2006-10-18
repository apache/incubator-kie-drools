package org.drools.base.extractors;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;

public abstract class BaseFloatClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 20311112843487706L;
    
    public BaseFloatClassFieldExtractor(Class clazz,
                                       String fieldName) {
        super( clazz,
               fieldName );
    }

    public Object getValue(Object object) {
        return new Float( getFloatValue( object ) );
    }

    public boolean getBooleanValue(Object object) {
        throw new RuntimeDroolsException("Conversion to boolean not supported from float");
    }

    public byte getByteValue(Object object) {
        return (byte) getFloatValue( object );
        
    }

    public char getCharValue(Object object) {
        throw new RuntimeDroolsException("Conversion to char not supported from float");
    }

    public double getDoubleValue(Object object) {
        return getFloatValue( object );
    }

    public abstract float getFloatValue(Object object);

    public int getIntValue(Object object) {
        return (int) getFloatValue( object );
    }

    public long getLongValue(Object object) {
        return (long) getFloatValue( object );
    }

    public short getShortValue(Object object) {
        return (short) getFloatValue( object );
    }

}
