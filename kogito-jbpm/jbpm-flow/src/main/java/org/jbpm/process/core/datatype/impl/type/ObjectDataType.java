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
import org.jbpm.process.core.datatype.impl.coverter.TypeConverterRegistry;

/**
 * Representation of an object datatype.
 */
public class ObjectDataType implements DataType {

    private static final long serialVersionUID = 510l;

    private String className;

    private ClassLoader classLoader;

    private transient Class<?> clazz;

    public ObjectDataType() {
    }

    public ObjectDataType(String className) {
        setClassName(className);
    }

    public ObjectDataType(String className, ClassLoader classLoader) {
        setClassName(className);
        setClassLoader(classLoader);
    }

    public ObjectDataType(Class<?> clazz) {
        this(clazz.getCanonicalName());
        this.clazz = clazz;
        this.classLoader = clazz.getClassLoader();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        className = (String) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(className);
    }

    @Override
    public boolean verifyDataType(final Object value) {
        if (value == null) {
            return true;
        }

        Class<?> clazz = find(getStringType(), value.getClass().getClassLoader());
        if (clazz == null) {
            clazz = find(getStringType(), Thread.currentThread().getContextClassLoader());
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Could not find data type " + className);
        }

        return clazz.isInstance(value);

    }

    private Class<?> find(String type, ClassLoader classLoader) {
        try {
            return Class.forName(type, true, classLoader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public Object readValue(String value) {
        return TypeConverterRegistry.get().forType(getStringType()).apply(value);
    }

    @Override
    public String writeValue(Object value) {
        return value.toString();
    }

    @Override
    public String getStringType() {
        return className == null ? "java.lang.Object" : className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjectDataType that = (ObjectDataType) o;
        return Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className);
    }

    @Override
    public Class<?> getObjectClass() {
        if (clazz == null) {
            ClassLoader cl = classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader();
            try {
                clazz = cl.loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return clazz;
    }
}
