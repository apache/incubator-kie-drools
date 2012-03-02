/*
 * Copyright 2005 JBoss Inc
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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.base.ValueType;

/**
 * A public interface for Read accessors
 */
public interface ReadAccessor {

    Object getValue(Object object);

    BigDecimal getBigDecimalValue(Object object);

    BigInteger getBigIntegerValue(Object object);

    char getCharValue(Object object);

    int getIntValue(Object object);

    byte getByteValue(Object object);

    short getShortValue(Object object);

    long getLongValue(Object object);

    float getFloatValue(Object object);

    double getDoubleValue(Object object);

    boolean getBooleanValue(Object object);

    boolean isNullValue(Object object);

    ValueType getValueType();

    Class< ? > getExtractToClass();

    String getExtractToClassName();

    Method getNativeReadMethod();

    String getNativeReadMethodName();

    int getHashCode(Object object);

    int getIndex();

}
