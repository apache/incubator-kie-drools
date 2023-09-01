package org.drools.mvel.accessors;

import java.lang.reflect.Method;
import java.util.Date;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.BaseClassFieldReader;
import org.drools.base.base.ValueType;

public abstract class BaseDateClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    public BaseDateClassFieldReader() {
        
    }
    
    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseDateClassFieldReader(final int index,
                                       final Class fieldType,
                                       final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public Object getValue(ValueResolver valueResolver, final Object object) {
        return object;
    }

    public boolean getBooleanValue(ValueResolver valueResolver, final Object object) {
        throw new RuntimeException( "Conversion to boolean not supported from Date" );
    }

    public byte getByteValue(ValueResolver valueResolver, final Object object) {
        return (byte) getLongValue( valueResolver, object );

    }

    public char getCharValue(ValueResolver valueResolver, final Object object) {
        return (char) getLongValue( valueResolver, object );
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

    public long getLongValue(ValueResolver valueResolver, Object object) {
        return getDate( valueResolver, object ).getTime();
    }

    protected Date getDate( ValueResolver valueResolver, Object object ) {
        return (Date)getValue( valueResolver, object );
    }

    public short getShortValue(ValueResolver valueResolver, final Object object) {
        return (short) getLongValue( valueResolver, object );
    }

    public boolean isNullValue(ValueResolver valueResolver, final Object object) {
        if ( object == null ) {
            return true;
        } else {
            return getValue( valueResolver,
                             object ) == null;
        }
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod("getValue",
                                                     ValueResolver.class, Object.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

    public String getNativeReadMethodName() {
        return "getValue";
    }

    public int getHashCode(ValueResolver valueResolver, final Object object) {        
        final long temp = getLongValue( valueResolver, object );
        return (int) (temp ^ (temp >>> 32));
    }

}
