package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.BaseClassFieldReader;
import org.drools.base.base.ValueType;

public abstract class BaseDoubleClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseDoubleClassFieldReader(final int index,
                                            final Class fieldType,
                                            final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseDoubleClassFieldReader() {
    }

    public Object getValue(ValueResolver valueResolver, final Object object) {
        return Double.valueOf( getDoubleValue( valueResolver, object ) );
    }

    public boolean getBooleanValue(ValueResolver valueResolver, final Object object) {
        throw new RuntimeException( "Conversion to boolean not supported from double" );
    }

    public byte getByteValue(ValueResolver valueResolver, final Object object) {
        return (byte) getDoubleValue( valueResolver, object );

    }

    public char getCharValue(ValueResolver valueResolver, final Object object) {
        throw new RuntimeException( "Conversion to char not supported from double" );
    }

    public abstract double getDoubleValue(ValueResolver valueResolver, Object object);

    public float getFloatValue(ValueResolver valueResolver, final Object object) {
        return (float) getDoubleValue( valueResolver, object );
    }

    public int getIntValue(ValueResolver valueResolver, final Object object) {
        return (int) getDoubleValue( valueResolver, object );
    }

    public long getLongValue(ValueResolver valueResolver, final Object object) {
        return (long) getDoubleValue( valueResolver, object );
    }

    public short getShortValue(ValueResolver valueResolver, final Object object) {
        return (short) getDoubleValue( valueResolver, object );
    }

    public boolean isNullValue(ValueResolver valueResolver, final Object object) {
        return false;
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod("getDoubleValue",
                                                     ValueResolver.class, Object.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

    public int getHashCode(ValueResolver valueResolver, final Object object) {
        final long temp = Double.doubleToLongBits( getDoubleValue( valueResolver, object ) );
        return (int) (temp ^ (temp >>> 32));
    }

}
