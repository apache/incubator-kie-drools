/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.template.objects;

import org.drools.template.DataProvider;
import org.drools.template.parser.Column;
import org.drools.template.parser.TemplateContainer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * An object of this class acts as a DataProvider for rule templates.
 * It may be created with a collection of maps or objects. The template's
 * column names are used as keys or Bean-stype accessors to obtain the
 * actual parameters for the substitution.
 */
public class ObjectDataProvider implements DataProvider {

    private Column[]    columns;
    private Iterator<?> iter;

    /**
     * Constructor from a template container
     *
     * @param tcont a template container
     * @param objs  a collection of maps or objects
     */
    public ObjectDataProvider(TemplateContainer tcont, Collection<?> objs) {
        columns = tcont.getColumns();
        iter = objs.iterator();
    }


    public boolean hasNext() {
        return iter.hasNext();
    }

    private Method getMethod(Class<?> clazz, String field) {
        String Field = Character.toUpperCase(field.charAt(0)) + field.substring(1);
        String getter = "get" + Field;
        try {
            return clazz.getMethod(getter);
        } catch (SecurityException e) {
            return null;
        } catch (NoSuchMethodException e) {
        }
        getter = "is" + Field;
        try {
            return clazz.getMethod(getter);
        } catch (SecurityException e) {
            return null;
        } catch (NoSuchMethodException e) {
        }
        getter = field;
        try {
            return clazz.getMethod(getter);
        } catch (Exception e) {
            return null;
        }
    }

    private Field getField(Class<?> clazz, String field) {
        try {
            return clazz.getField(field);
        } catch (Exception e) {
            return null;
        }
    }

    private String[] nextFromMap(Object obj) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map) obj;
        String[] values = new String[columns.length];
        for (int iCol = 0; iCol < columns.length; iCol++) {
            Object target = map.get(columns[iCol].getName());
            values[iCol] = target == null ? null : target.toString();
        }
        return values;
    }

    private String[] nextFromObject(Object obj) {
        Class<?> clazz = obj.getClass();
        String[] values = new String[columns.length];
        for (int iCol = 0; iCol < columns.length; iCol++) {
            String fieldName = columns[iCol].getName();
            Object val = null;
            try {
                Method method = getMethod(clazz, fieldName);
                if (method != null) {
                    val = method.invoke(obj);
                } else {
                    Field field = getField(clazz, fieldName);
                    if (field != null) {
                        val = field.get(obj);
                    }
                }
            } catch (Exception e) {
                // leave it null
            }
            values[iCol] = val == null ? null : val.toString();
        }
        return values;
    }

    public String[] next() {
        Object obj = iter.next();
        if (obj instanceof Map) {
            return nextFromMap(obj);
        } else {
            return nextFromObject(obj);
        }
    }
}
