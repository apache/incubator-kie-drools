/*
 * Copyright 2008 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.spi;

import java.io.Externalizable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.base.ValueType;

/**
 * An interface for a class that is able to write values into a class
 * field
 * 
 * @author etirelli
 */
public interface WriteAccessor
    extends
    Externalizable {

    public int getIndex();

    public void setValue( Object bean, Object value );

    public void setBigDecimalValue( Object bean, BigDecimal value );

    public void setBigIntegerValue( Object bean, BigInteger value );

    public void setCharValue( Object bean, char value );

    public void setIntValue( Object bean, int value );

    public void setByteValue( Object bean, byte value );

    public void setShortValue( Object bean, short value );

    public void setLongValue( Object bean, long value );

    public void setFloatValue( Object bean, float value );

    public void setDoubleValue( Object bean, double value );

    public void setBooleanValue( Object bean, boolean value );

    public ValueType getValueType();

    public Class< ? > getFieldType();

    public Method getNativeWriteMethod();
}
