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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jboss.jandex.IndexView;

public class QuarkusRecordableParameterizedType implements ParameterizedType {

    String rawTypeName;
    Type ownerType;
    Type[] actualTypeArguments;

    public QuarkusRecordableParameterizedType() {
    }

    public QuarkusRecordableParameterizedType(org.jboss.jandex.ParameterizedType type, IndexView indexView) {
        this(type.name().toString(),
                QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(type.owner(), indexView),
                type.asParameterizedType()
                        .arguments().stream()
                        .map(argument -> QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(argument, indexView))
                        .toArray(Type[]::new));
    }

    public QuarkusRecordableParameterizedType(String rawTypeName, Type ownerType, Type[] actualTypeArguments) {
        this.rawTypeName = rawTypeName;
        this.ownerType = ownerType;
        this.actualTypeArguments = actualTypeArguments;
    }

    @Override
    public String getTypeName() {
        String prefix = "";
        if (ownerType != null) {
            prefix = ownerType.getTypeName() + ".";
        }
        return prefix + rawTypeName + "<" +
                Arrays.stream(actualTypeArguments)
                        .map(Type::getTypeName)
                        .collect(Collectors.joining(","))
                +
                ">";
    }

    public String getRawTypeName() {
        return rawTypeName;
    }

    public void setRawTypeName(String rawTypeName) {
        this.rawTypeName = rawTypeName;
    }

    @Override
    public String toString() {
        return getTypeName();
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    public void setActualTypeArguments(Type[] actualTypeArguments) {
        this.actualTypeArguments = actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return new QuarkusRecordableClassType(rawTypeName);
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(Type ownerType) {
        this.ownerType = ownerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuarkusRecordableParameterizedType that = (QuarkusRecordableParameterizedType) o;
        return rawTypeName.equals(that.rawTypeName) && Objects.equals(ownerType, that.ownerType)
                && Arrays.equals(actualTypeArguments, that.actualTypeArguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rawTypeName, ownerType);
        result = 31 * result + Arrays.hashCode(actualTypeArguments);
        return result;
    }
}
