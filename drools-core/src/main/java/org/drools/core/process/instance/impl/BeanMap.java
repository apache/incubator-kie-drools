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

package org.drools.core.process.instance.impl;

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
import java.util.Objects;
import java.util.Set;

/**
 * A utility class that exposes getters and setters of a bean
 * as key/value pairss in a Map.
 */
public class BeanMap extends AbstractMap<String, Object> {

    static void fillBean(Object bean, Map<String, Object> values) {
        new BeanMap(bean).putAll(values);
    }

    static void fillMap(Map<String, Object> values, Object bean) {
        values.putAll(new BeanMap(bean));
    }

    private final Map<String, Entry<String, Object>> accessors;

    BeanMap(Object bean) {
        Objects.requireNonNull(bean);
        HashMap<String, Accessors> accessors = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                accessors.put(pd.getName(),
                              new Accessors(
                                      pd.getName(),
                                      pd.getReadMethod(),
                                      pd.getWriteMethod(),
                                      bean));
            }
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
        this.accessors = Collections.unmodifiableMap(accessors);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return new HashSet<>(accessors.values());
    }

    @Override
    public Object put(String key, Object value) {
        return accessors.get(key).setValue(value);
    }

    static class Accessors implements Map.Entry<String, Object> {

        final String id;
        final Method getter, setter;
        final Object target;

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

