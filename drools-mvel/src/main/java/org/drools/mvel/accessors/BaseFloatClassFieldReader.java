package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.BaseClassFieldReader;
import org.drools.base.base.ValueType;

public abstract class BaseFloatClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseFloatClassFieldReader(final int index,
                                           final Class fieldType,
                                           final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseFloatClassFieldReader() {
    }

    public Object getValue(ValueResolver valueResolver, final Object object) {
        return Float.valueOf( getFloatValue( valueResolver, object ) );
    }

    public boolean getBooleanValue(ValueResolver valueResolver, final Object object) {
        throw new RuntimeException( "Conversion to boolean not supported from float" );
    }

    public byte getByteValue(ValueResolver valueResolver, final Object object) {
        return (byte) getFloatValue( valueResolver, object );

    }

    public char getCharValue(ValueResolver valueResolver, final Object object) {
        throw new RuntimeException( "Conversion to char not supported from float" );
    }

    public double getDoubleValue(ValueResolver valueResolver, final Object object) {
        return getFloatValue( valueResolver, object );
    }

    public abstract float getFloatValue(ValueResolver valueResolver, Object object);

    public int getIntValue(ValueResolver valueResolver, final Object object) {
        return (int) getFloatValue( valueResolver, object );
    }

    public long getLongValue(ValueResolver valueResolver, final Object object) {
        return (long) getFloatValue( valueResolver, object );
    }

    public short getShortValue(ValueResolver valueResolver, final Object object) {
        return (short) getFloatValue( valueResolver, object );
    }

    public boolean isNullValue(ValueResolver valueResolver, final Object object) {
        return false;
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod("getFloatValue",
                                                     ValueResolver.class, Object.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

    public int getHashCode(ValueResolver valueResolver, final Object object) {
        return Float.floatToIntBits( getFloatValue( valueResolver, object ) );
    }

}
