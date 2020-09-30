/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.spi;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.core.common.InternalWorkingMemory;

public interface InternalReadAccessor
    extends
    ReadAccessor {

    Object getValue(InternalWorkingMemory workingMemory, Object object);

    BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory, Object object);

    BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory, Object object);

    char getCharValue(InternalWorkingMemory workingMemory, Object object);

    int getIntValue(InternalWorkingMemory workingMemory, Object object);

    byte getByteValue(InternalWorkingMemory workingMemory, Object object);

    short getShortValue(InternalWorkingMemory workingMemory, Object object);

    long getLongValue(InternalWorkingMemory workingMemory, Object object);

    float getFloatValue(InternalWorkingMemory workingMemory, Object object);

    double getDoubleValue(InternalWorkingMemory workingMemory, Object object);

    boolean getBooleanValue(InternalWorkingMemory workingMemory, Object object);

    boolean isNullValue(InternalWorkingMemory workingMemory, Object object);

    int getHashCode(InternalWorkingMemory workingMemory, Object object);
    
    boolean isGlobal();
    
    boolean isSelfReference();

}
