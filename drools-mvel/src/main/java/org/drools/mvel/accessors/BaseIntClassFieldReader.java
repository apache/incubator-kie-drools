package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.BaseClassFieldReader;
import org.drools.base.base.ValueType;

public abstract class BaseIntClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseIntClassFieldReader(final int index,
                                         final Class fieldType,
                                         final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseIntClassFieldReader() {
    }

    public Object getValue(ValueResolver valueResolver, final Object object) {
        return getIntValue( valueResolver, object );
    }

    public boolean getBooleanValue(ValueResolver valueResolver, final Object object) {
        throw new RuntimeException( "Conversion to boolean not supported from int" );
    }

    public byte getByteValue(ValueResolver valueResolver, final Object object) {
        return (byte) getIntValue( valueResolver, object );

    }

    public char getCharValue(ValueResolver valueResolver, final Object object) {
        throw new RuntimeException( "Conversion to char not supported from int" );
    }

    public double getDoubleValue(ValueResolver valueResolver, final Object object) {
        return getIntValue( valueResolver, object );
    }

    public float getFloatValue(ValueResolver valueResolver, final Object object) {
        return getIntValue( valueResolver, object );
    }

    public abstract int getIntValue(ValueResolver valueResolver, Object object);

    public long getLongValue(ValueResolver valueResolver, final Object object) {
        return getIntValue( valueResolver, object );
    }

    public short getShortValue(ValueResolver valueResolver, final Object object) {
        return (short) getIntValue( valueResolver, object );
    }

    public boolean isNullValue(ValueResolver valueResolver, final Object object) {
        return false;
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod("getIntValue",
                                                     ValueResolver.class, Object.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

    public int getHashCode(ValueResolver valueResolver, final Object object) {
        return getIntValue( valueResolver, object );
    }
}
