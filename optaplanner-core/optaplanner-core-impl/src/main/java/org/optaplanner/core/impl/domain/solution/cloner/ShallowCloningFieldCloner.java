/*
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

package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.Objects;

import org.optaplanner.core.api.function.TriConsumer;

final class ShallowCloningFieldCloner {

    public static ShallowCloningFieldCloner of(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType == boolean.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyBoolean);
        } else if (fieldType == byte.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyByte);
        } else if (fieldType == char.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyChar);
        } else if (fieldType == short.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyShort);
        } else if (fieldType == int.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyInt);
        } else if (fieldType == long.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyLong);
        } else if (fieldType == float.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyFloat);
        } else if (fieldType == double.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyDouble);
        } else {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyObject);
        }

    }

    private final Field field;
    private final TriConsumer<Field, Object, Object> copyOperation;

    private ShallowCloningFieldCloner(Field field, TriConsumer<Field, Object, Object> copyOperation) {
        this.field = Objects.requireNonNull(field);
        this.copyOperation = Objects.requireNonNull(copyOperation);
    }

    public <C> void clone(C original, C clone) {
        copyOperation.accept(field, original, clone);
    }

}
