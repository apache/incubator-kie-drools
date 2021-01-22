/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus.gizmo.types;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

public class QuarkusRecordableArrayType implements GenericArrayType {

    Type genericComponentType;

    public QuarkusRecordableArrayType() {
    }

    public QuarkusRecordableArrayType(Type genericComponentType) {
        this.genericComponentType = genericComponentType;
    }

    @Override
    public String getTypeName() {
        return genericComponentType.getTypeName() + "[]";
    }

    @Override
    public Type getGenericComponentType() {
        return genericComponentType;
    }

    public void setGenericComponentType(Type genericComponentType) {
        this.genericComponentType = genericComponentType;
    }

    @Override
    public String toString() {
        return getTypeName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuarkusRecordableArrayType that = (QuarkusRecordableArrayType) o;
        return genericComponentType.equals(that.genericComponentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genericComponentType);
    }
}
