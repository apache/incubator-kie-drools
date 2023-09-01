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
package org.drools.model;

import java.util.Collection;
import java.util.function.Function;

public interface Prototype extends NamedModelItem {

    Object UNDEFINED_VALUE = UndefinedValue.INSTANCE;

    Collection<String> getFieldNames();

    Field getField(String name);

    int getFieldIndex(final String name);

    default Function<PrototypeFact, Object> getFieldValueExtractor(String name) {
        Field field = getField(name);
        return field != null ? field.getExtractor() : p -> p.has(name) ? p.get(name) : UNDEFINED_VALUE;
    }

    boolean isEvent();

    Prototype setAsEvent(boolean event);

    interface Field {
        String getName();

        Function<PrototypeFact, Object> getExtractor();

        boolean isTyped();

        Class<?> getType();
    }

    class UndefinedValue {
        static final UndefinedValue INSTANCE = new UndefinedValue();

        static final UnsupportedOperationException HASHCODE_EXCEPTION = new UnsupportedOperationException();

        @Override
        public int hashCode() {
            // throw an Exception to avoid indexing of an undefined value
            throw HASHCODE_EXCEPTION;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public String toString() {
            return "$UndefinedValue$";
        }
    }
}
