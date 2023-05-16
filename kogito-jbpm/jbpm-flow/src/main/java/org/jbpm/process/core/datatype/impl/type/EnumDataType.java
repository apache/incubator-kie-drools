/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.core.datatype.impl.type;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

import org.jbpm.process.core.datatype.DataType;

/**
 * Representation of an Enum datatype.
 */
public class EnumDataType implements DataType {

    private static final long serialVersionUID = 4L;
    private Class<? extends Enum> enumClass;

    public EnumDataType() {
        this.enumClass = Enum.class;
    }

    /**
     * @deprecated use constructor that accepts enum class
     */
    @Deprecated
    public EnumDataType(String className) {
        try {
            this.enumClass = Class.forName(className).asSubclass(Enum.class);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public EnumDataType(Class<? extends Enum> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        String className = (String) in.readObject();
        enumClass = className == null ? Enum.class : Class.forName(className).asSubclass(Enum.class);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(enumClass.getName());
    }

    @Override
    public boolean verifyDataType(final Object value) {
        return value == null || enumClass.isAssignableFrom(value.getClass());
    }

    @Override
    public Object readValue(String value) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return null;
        }
    }

    @Override
    public String writeValue(Object value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public String getStringType() {
        return enumClass.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EnumDataType that = (EnumDataType) o;
        return Objects.equals(enumClass, that.enumClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enumClass);
    }

    @Override
    public Class<?> getObjectClass() {
        return enumClass;
    }
}
