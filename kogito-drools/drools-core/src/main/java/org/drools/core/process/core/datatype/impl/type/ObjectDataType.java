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

package org.drools.core.process.core.datatype.impl.type;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.process.core.datatype.DataType;

import com.thoughtworks.xstream.XStream;

/**
 * Representation of an object datatype.
 */
public class ObjectDataType implements DataType {

    private static final long serialVersionUID = 510l;

    private String className;

    private ClassLoader classLoader;
    
    public ObjectDataType() {
    }

    public ObjectDataType(String className) {
        setClassName(className);
    }

    public ObjectDataType(String className, ClassLoader classLoader) {
        setClassName(className);
        setClassLoader(classLoader);
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

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        className = (String) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(className);
    }

    public boolean verifyDataType(final Object value) {
        if (value == null) {
            return true;
        }
        try {
            Class<?> clazz = Class.forName(className, true, value.getClass().getClassLoader());
            if (clazz.isInstance(value)) {
                return true;
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(
                "Could not find data type " + className);
        }
        return false;
    }

    public Object readValue(String value) {
        XStream xstream = new XStream();
        if (classLoader != null) {
            xstream.setClassLoader(classLoader);
        }
        return xstream.fromXML(value);
    }

    public String writeValue(Object value) {
        XStream xstream = new XStream();
        if (classLoader != null) {
            xstream.setClassLoader(classLoader);
        }
        return xstream.toXML(value);
    }

    public String getStringType() {
        return className == null ? "java.lang.Object" : className;
    }
}
