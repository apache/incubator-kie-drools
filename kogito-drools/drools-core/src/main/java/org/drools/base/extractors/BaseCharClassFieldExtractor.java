package org.drools.base.extractors;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;

public abstract class BaseCharClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 91214567753008212L;
    
    public BaseCharClassFieldExtractor(Class clazz,
                                          String fieldName) {
        super( clazz,
               fieldName );
    }

    public Object getValue(Object object) {
        return new Long( getCharValue( object ) );
    }

    public boolean getBooleanValue(Object object) {
        throw new RuntimeDroolsException("Conversion to boolean not supported from char");
    }

    public byte getByteValue(Object object) {
        throw new RuntimeDroolsException("Conversion to byte not supported from char");
    }

    public abstract char getCharValue(Object object);

    public double getDoubleValue(Object object) {
        throw new RuntimeDroolsException("Conversion to double not supported from char");
    }

    public float getFloatValue(Object object) {
        throw new RuntimeDroolsException("Conversion to float not supported from char");
    }

    public int getIntValue(Object object) {
        throw new RuntimeDroolsException("Conversion to int not supported from char");
    }

    public long getLongValue(Object object) {
        throw new RuntimeDroolsException("Conversion to long not supported from char");
    }

    public short getShortValue(Object object) {
        throw new RuntimeDroolsException("Conversion to short not supported from char");
    }

}
