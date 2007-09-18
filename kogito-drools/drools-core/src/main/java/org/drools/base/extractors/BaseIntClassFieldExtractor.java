package org.drools.base.extractors;

import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;

public abstract class BaseIntClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 400L;

    public BaseIntClassFieldExtractor(final Class clazz,
                                      final String fieldName) {
        super( clazz,
               fieldName );
    }

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseIntClassFieldExtractor(final int index,
                                         final Class fieldType,
                                         final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public Object getValue(InternalWorkingMemory workingMemory, final Object object) {
        return new Integer( getIntValue( workingMemory, object ) );
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory, final Object object) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from int" );
    }

    public byte getByteValue(InternalWorkingMemory workingMemory, final Object object) {
        return (byte) getIntValue( workingMemory, object );

    }

    public char getCharValue(InternalWorkingMemory workingMemory, final Object object) {
        throw new RuntimeDroolsException( "Conversion to char not supported from int" );
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory, final Object object) {
        return getIntValue( workingMemory, object );
    }

    public float getFloatValue(InternalWorkingMemory workingMemory, final Object object) {
        return getIntValue( workingMemory, object );
    }

    public abstract int getIntValue(InternalWorkingMemory workingMemory, Object object);

    public long getLongValue(InternalWorkingMemory workingMemory, final Object object) {
        return getIntValue( workingMemory, object );
    }

    public short getShortValue(InternalWorkingMemory workingMemory, final Object object) {
        return (short) getIntValue( workingMemory, object );
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory, final Object object) {
        return false;
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getIntValue",
                                                      new Class[]{InternalWorkingMemory.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(InternalWorkingMemory workingMemory, final Object object) {
        return getIntValue( workingMemory, object );
    }
}
