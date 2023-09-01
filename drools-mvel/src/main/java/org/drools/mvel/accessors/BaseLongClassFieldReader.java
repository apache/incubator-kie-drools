package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.BaseClassFieldReader;
import org.drools.base.base.ValueType;

public abstract class BaseLongClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseLongClassFieldReader(final int index,
                                           final Class fieldType,
                                           final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseLongClassFieldReader() {
    }

    public Object getValue(ValueResolver valueResolver, final Object object) {
        return getLongValue( valueResolver, object );
    }

    public boolean getBooleanValue(ValueResolver valueResolver, final Object object) {
        throw new RuntimeException( "Conversion to boolean not supported from long" );
    }

    public byte getByteValue(ValueResolver valueResolver, final Object object) {
        return (byte) getLongValue( valueResolver, object );

    }

    public char getCharValue(ValueResolver valueResolver, final Object object) {
        throw new RuntimeException( "Conversion to char not supported from long" );
    }

    public double getDoubleValue(ValueResolver valueResolver, final Object object) {
        return getLongValue( valueResolver, object );
    }

    public float getFloatValue(ValueResolver valueResolver, final Object object) {
        return getLongValue( valueResolver, object );
    }

    public int getIntValue(ValueResolver valueResolver, final Object object) {
        return (int) getLongValue( valueResolver, object );
    }

    public abstract long getLongValue(ValueResolver valueResolver, Object object);

    public short getShortValue(ValueResolver valueResolver, final Object object) {
        return (short) getLongValue( valueResolver, object );
    }

    public boolean isNullValue(ValueResolver valueResolver, final Object object) {
        return false;
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod("getLongValue",
                                                     ValueResolver.class, Object.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

    public int getHashCode(ValueResolver valueResolver, final Object object) {
        final long temp = getLongValue( valueResolver, object );
        return (int) (temp ^ (temp >>> 32));
    }

}
