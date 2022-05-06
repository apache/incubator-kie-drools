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

package org.drools.core.rule.accessor;

import java.lang.reflect.Method;

import org.drools.core.base.ValueType;
import org.drools.core.common.ReteEvaluator;

/**
 * A public interface for Read accessors
 */
public interface ReadAccessor {

    Object getValue(Object object);

    boolean isNullValue(Object object);

    ValueType getValueType();

    Class< ? > getExtractToClass();

    String getExtractToClassName();

    Method getNativeReadMethod();

    String getNativeReadMethodName();

    int getHashCode(Object object);

    int getIndex();

    Object getValue(ReteEvaluator reteEvaluator, Object object);

    char getCharValue(ReteEvaluator reteEvaluator, Object object);

    int getIntValue(ReteEvaluator reteEvaluator, Object object);

    byte getByteValue(ReteEvaluator reteEvaluator, Object object);

    short getShortValue(ReteEvaluator reteEvaluator, Object object);

    long getLongValue(ReteEvaluator reteEvaluator, Object object);

    float getFloatValue(ReteEvaluator reteEvaluator, Object object);

    double getDoubleValue(ReteEvaluator reteEvaluator, Object object);

    boolean getBooleanValue(ReteEvaluator reteEvaluator, Object object);

    boolean isNullValue(ReteEvaluator reteEvaluator, Object object);

    int getHashCode(ReteEvaluator reteEvaluator, Object object);

    boolean isGlobal();

    boolean isSelfReference();
}
