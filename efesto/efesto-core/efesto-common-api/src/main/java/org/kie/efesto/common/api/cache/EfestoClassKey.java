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
package org.kie.efesto.common.api.cache;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Key used by efesto caches based on type/generics
 */
public class EfestoClassKey implements ParameterizedType {

    private final Type rawType;
    private final Type[] typeArguments;

    public EfestoClassKey(Type rawType, Type... typeArguments) {
        this.rawType = rawType;
        this.typeArguments = typeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public String toString() {
        String argsString = Stream.of(typeArguments).map(this::getCanonicalTypeName).collect(Collectors.joining(", "));
        return getCanonicalTypeName(rawType) + "<" + argsString + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EfestoClassKey that = (EfestoClassKey) o;
        return Objects.equals(rawType, that.rawType) && Arrays.equals(typeArguments, that.typeArguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rawType);
        result = 31 * result + Arrays.hashCode(typeArguments);
        return result;
    }

    private String getCanonicalTypeName(Type type) {
        return type instanceof Class ? ((Class<?>) type).getCanonicalName() : type.getTypeName();
    }



}
