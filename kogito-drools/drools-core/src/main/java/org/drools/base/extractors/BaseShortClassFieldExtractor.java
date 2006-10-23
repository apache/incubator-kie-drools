package org.drools.base.extractors;

import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;

public abstract class BaseShortClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 2031113412843487706L;

    public BaseShortClassFieldExtractor(final Class clazz,
                                        final String fieldName) {
        super( clazz,
               fieldName );
    }

    public Object getValue(final Object object) {
        return new Long( getShortValue( object ) );
    }

    public boolean getBooleanValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from short" );
    }

    public byte getByteValue(final Object object) {
        return (byte) getShortValue( object );

    }

    public char getCharValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to char not supported from short" );
    }

    public double getDoubleValue(final Object object) {
        return getShortValue( object );
    }

    public float getFloatValue(final Object object) {
        return getShortValue( object );
    }

    public int getIntValue(final Object object) {
        return getShortValue( object );
    }

    public long getLongValue(final Object object) {
        return getShortValue( object );
    }

    public abstract short getShortValue(Object object);

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getShortValue", new Class[] { Object.class } );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException("This is a bug. Please report to development team: "+e.getMessage(), e);
        }
    }
}
