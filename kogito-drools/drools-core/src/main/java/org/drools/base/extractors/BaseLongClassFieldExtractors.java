package org.drools.base.extractors;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;

public abstract class BaseLongClassFieldExtractors extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 2031113412843487706L;
    
    public BaseLongClassFieldExtractors(Class clazz,
                                       String fieldName) {
        super( clazz,
               fieldName );
    }

    public Object getValue(Object object) {
        return new Long( getLongValue( object ) );
    }

    public boolean getBooleanValue(Object object) {
        throw new RuntimeDroolsException("Conversion to boolean not supported from long");
    }

    public byte getByteValue(Object object) {
        return (byte) getLongValue( object );
        
    }

    public char getCharValue(Object object) {
        throw new RuntimeDroolsException("Conversion to char not supported from long");
    }

    public double getDoubleValue(Object object) {
        return getLongValue( object );
    }

    public float getFloatValue(Object object) {
        return getLongValue( object );
    }

    public int getIntValue(Object object) {
        return (int) getLongValue( object );
    }

    public abstract long getLongValue(Object object);

    public short getShortValue(Object object) {
        return (short) getLongValue( object );
    }

}
