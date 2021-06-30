/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Models {
    private static final Logger LOGGER = LoggerFactory.getLogger(Models.class);

    private static final String CLASS_PROP = "class";
    private static final String ID_PROP = "id";
    /**
     * this prefix is only used when a variable name
     * clashes with a predefined Java keyword (e.g. `static`)
     */
    private static final String VAR_PREFIX = "v$";

    private Models() {
    }

    public static Map<String, Object> toMap(Object m) {
        try {
            Map<String, Object> map = new HashMap<>();
            BeanInfo beanInfo = Introspector.getBeanInfo(m.getClass());
            Map<String, PropertyDescriptor> descriptors = descriptorMap(beanInfo);

            for (Map.Entry<String, PropertyDescriptor> e : descriptors.entrySet()) {
                String k = e.getKey();
                if (isIdentifier(k)) {
                    LOGGER.trace("Models#toMap: Skipping `id` property for class `{}`", m.getClass().getCanonicalName());
                    continue;
                }
                k = unprefixVar(k);
                map.put(k, e.getValue().getReadMethod().invoke(m));
            }
            return map;
        } catch (IntrospectionException | ReflectiveOperationException e) {
            throw new ReflectiveModelAccessException(e);
        }
    }

    public static <T> T fromMap(T m, String id, Map<String, Object> map) {
        setId(m, id);
        return fromMap(m, map);
    }

    public static <T> T fromMap(T m, Map<String, Object> map) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(m.getClass());
            Map<String, PropertyDescriptor> descriptors = descriptorMap(beanInfo);

            for (Map.Entry<String, PropertyDescriptor> e : descriptors.entrySet()) {
                String k = e.getKey();
                k = unprefixVar(k);
                if (map.containsKey(k)) {
                    e.getValue().getWriteMethod().invoke(m, map.get(k));
                }
            }
            return m;
        } catch (IntrospectionException | ReflectiveOperationException e) {
            throw new ReflectiveModelAccessException(e);
        }
    }

    public static <T> T fromMap(Class<T> cls, Map<String, Object> map) {
        try {
            Constructor<T> constructor = cls.getConstructor();
            T t = constructor.newInstance();
            fromMap(t, map);
            return t;
        } catch (NoSuchMethodException e) {
            throw new ReflectiveModelAccessException(
                    String.format("Class `%s` must declare an empty constructor.", cls.getCanonicalName()), e);
        } catch (ReflectiveOperationException e) {
            throw new ReflectiveModelAccessException(e);
        }
    }

    public static void setId(Object m, String id) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(m.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                if (isIdentifier(pd.getName())) {
                    pd.getWriteMethod().invoke(m, id);
                    return;
                }
            }
            // no Id found, throw error
            throw new ReflectiveModelAccessException(
                    String.format(
                            "No `id` property found for class `%s`. Have you defined getters and setters?",
                            m.getClass().getCanonicalName()));
        } catch (IntrospectionException | ReflectiveOperationException e) {
            throw new ReflectiveModelAccessException(e);
        }

    }

    public static <I, O> O convert(I in, O out) {
        fromMap(out, toMap(in));
        return out;
    }

    /**
     * When a process variable name clashes with a predefined
     * Java keyword (e.g. `static`), we are prefixing the field
     * with `v$` (e.g. `v$static`).
     *
     * @return the unprefixed variable name
     */
    private static String unprefixVar(String k) {
        if (k.startsWith(VAR_PREFIX)) {
            k = k.substring(VAR_PREFIX.length());
        }
        return k;
    }

    private static boolean isIdentifier(String k) {
        return k.equals(ID_PROP);
    }

    private static Map<String, PropertyDescriptor> descriptorMap(BeanInfo beanInfo) {
        return Arrays.stream(beanInfo.getPropertyDescriptors())
                .filter(pd -> !pd.getName().equals(CLASS_PROP))
                .collect(Collectors.toMap(
                        PropertyDescriptor::getName,
                        Function.identity()));
    }

}
