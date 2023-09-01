/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
