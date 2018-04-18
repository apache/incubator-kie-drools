/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.core.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A utility class that exposes getters and setters of a bean
 * as key/value pairs in a Map.
 */
public class BeanMap<T> extends AbstractMap<String, Object> {

    private final T bean;
    private final Map<String, Accessors> accessors;

    public BeanMap(T bean) {
        this.bean = bean;
        this.accessors = Collections.unmodifiableMap(accessorsOf(bean));
    }

    public T getBean() {
        return bean;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return new HashSet<>(accessors.values());
    }

    protected boolean propertyExists(String prop) {
        return accessors.containsKey(prop);
    }

    @Override
    public boolean containsKey(Object key) {
        return key instanceof String && propertyExists((String) key);
    }

    @Override
    public Object get(Object key) {
        Accessors a = this.accessors.get(key);
        if (a == null) {
            return null;
        }
        return a.getValue();
    }

    @Override
    public Object put(String key, Object value) {
        Accessors accessors = this.accessors.get(key);
        if (accessors == null) {
            throw new IllegalArgumentException("Unknown key '" + key + "'");
        }
        return accessors.setValue(value);
    }

    private Map<String, Accessors> accessorsOf(T bean) {
        HashMap<String, Accessors> accessors = new HashMap<>();
        if (bean != null) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                    accessors.put(pd.getName(), Accessors.of(bean, pd));
                }
            } catch (IntrospectionException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return accessors;
    }

    private static class Accessors implements Map.Entry<String, Object> {

        final String id;
        final Method getter, setter;
        final Object target;

        static Accessors of(Object bean, PropertyDescriptor descriptor) {
            return new Accessors(
                    descriptor.getName(),
                    descriptor.getReadMethod(),
                    descriptor.getWriteMethod(),
                    bean
            );
        }

        Accessors(String id, Method getter, Method setter, Object target) {
            this.id = id;
            this.getter = getter;
            this.setter = setter;
            this.target = target;
        }

        @Override
        public String getKey() {
            return id;
        }

        @Override
        public Object getValue() {
            try {
                return getter.invoke(target);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public Object setValue(Object o) {
            try {
                Object prev = getValue();
                setter.invoke(target, o);
                return prev;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}

