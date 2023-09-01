package org.drools.base.rule.accessor;

import java.io.Externalizable;
import java.lang.reflect.Method;

import org.drools.base.base.ValueType;

/**
 * An interface for a class that is able to write values into a class
 * field
 */
public interface WriteAccessor extends Externalizable {

    int getIndex();

    void setValue( Object bean, Object value );

    void setCharValue( Object bean, char value );

    void setIntValue( Object bean, int value );

    void setByteValue( Object bean, byte value );

    void setShortValue( Object bean, short value );

    void setLongValue( Object bean, long value );

    void setFloatValue( Object bean, float value );

    void setDoubleValue( Object bean, double value );

    void setBooleanValue( Object bean, boolean value );

    ValueType getValueType();

    Class< ? > getFieldType();

    Method getNativeWriteMethod();
}
