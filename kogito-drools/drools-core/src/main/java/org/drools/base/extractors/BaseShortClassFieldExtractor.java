package org.drools.base.extractors;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;

public abstract class BaseShortClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 2031113412843487706L;
    
    public BaseShortClassFieldExtractor(Class clazz,
                                       String fieldName) {
        super( clazz,
               fieldName );
    }

    public Object getValue(Object object) {
        return new Long( getShortValue( object ) );
    }

    public boolean getBooleanValue(Object object) {
        throw new RuntimeDroolsException("Conversion to boolean not supported from short");
    }

    public byte getByteValue(Object object) {
        return (byte) getShortValue( object );
        
    }

    public char getCharValue(Object object) {
        throw new RuntimeDroolsException("Conversion to char not supported from short");
    }

    public double getDoubleValue(Object object) {
        return getShortValue( object );
    }

    public float getFloatValue(Object object) {
        return getShortValue( object );
    }

    public int getIntValue(Object object) {
        return getShortValue( object );
    }

    public long getLongValue(Object object) {
        return getShortValue( object );
    }

    public abstract short getShortValue(Object object);

}
