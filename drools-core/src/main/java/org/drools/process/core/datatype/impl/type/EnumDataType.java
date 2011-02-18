/*
 * Copyright 2005 JBoss Inc
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
package org.drools.process.core.datatype.impl.type;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.datatype.DataType;

/**
 * Representation of an Enum datatype.
 * 
 */
public class EnumDataType implements DataType {

    private static final long serialVersionUID = 4L;

    private String className;
    private transient Map<String, Object> valueMap;
    
    public EnumDataType() {
    }

    public EnumDataType(String className) {
        setClassName(className);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
        return getValueMap().containsValue(value);
    }
    
    public Object readValue(String value) {
        return getValueMap().get(value);
    }

    public String writeValue(Object value) {
        return value == null ? "" : value.toString();
    }

    public String getStringType() {
        return className == null ? "java.lang.Object" : className;
    }

    public Object[] getValues() {
        return getValueMap().values().toArray();
    }

    public String[] getValueNames() {
        return getValueMap().keySet().toArray(new String[0]);
    }

    public Map<String, Object> getValueMap() {
        if (this.valueMap == null) {
            try {
                this.valueMap = new HashMap<String, Object>();
                if (className == null) {
                    return null;
                }
                Class<?> clazz = Class.forName(className);
                if (!clazz.isEnum()) {
                    return null;
                }
                Object[] values = (Object[]) clazz.getMethod("values", null).invoke(clazz, null);
                for (Object value: values) {
                    this.valueMap.put(value.toString(), value);
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(
                    "Could not find data type " + className);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(
                    "IllegalAccessException " + e);
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException(
                    "InvocationTargetException " + e);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(
                    "NoSuchMethodException " + e);
            }

        }
        return this.valueMap;
    }
}
