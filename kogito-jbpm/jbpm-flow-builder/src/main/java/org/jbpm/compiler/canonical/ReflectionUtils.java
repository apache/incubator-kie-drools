/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.compiler.canonical;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtils {

    private ReflectionUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    private static Map<Class<?>, Class<?>> wrappers2Primitive = new ConcurrentHashMap<>();

    static {
        wrappers2Primitive.put(Boolean.class, boolean.class);
        wrappers2Primitive.put(Byte.class, byte.class);
        wrappers2Primitive.put(Character.class, char.class);
        wrappers2Primitive.put(Double.class, double.class);
        wrappers2Primitive.put(Float.class, float.class);
        wrappers2Primitive.put(Integer.class, int.class);
        wrappers2Primitive.put(Long.class, long.class);
        wrappers2Primitive.put(Short.class, short.class);
    }

    public static boolean isWrapper(Class<?> clazz) {
        return wrappers2Primitive.containsKey(clazz);
    }

    public static Class<?> getPrimitive(Class<?> clazz) {
        return wrappers2Primitive.get(clazz);
    }

    public static Method
            getMethod(ClassLoader cl,
                    Class<?> clazz,
                    String methodName,
                    Collection<String> parameterTypes) throws ReflectiveOperationException {

        boolean hasPrimitive = false;
        Class<?>[] methodParameters = new Class<?>[parameterTypes.size()];
        Class<?>[] primitiveParameters = new Class<?>[parameterTypes.size()];

        Iterator<String> iter = parameterTypes.iterator();
        int i = 0;
        while (iter.hasNext()) {
            String parameter = iter.next();
            if (!parameter.contains(".")) {
                parameter = "java.lang." + parameter;
            }
            Class<?> parameterClass = cl.loadClass(parameter);
            methodParameters[i] = parameterClass;
            Class<?> primitive = wrappers2Primitive.get(parameterClass);
            if (primitive != null) {
                primitiveParameters[i] = primitive;
                hasPrimitive = true;
            } else {
                primitiveParameters[i] = parameterClass;
            }
            i++;
        }
        try {
            return clazz.getMethod(methodName, methodParameters);
        } catch (NoSuchMethodException ex) {
            if (hasPrimitive) {
                try {
                    return clazz.getMethod(methodName, primitiveParameters);
                } catch (NoSuchMethodException ex2) {
                    logger.warn("Unable to find method {} with primitive arguments {}", methodName, primitiveParameters);
                }
            }
            throw ex;
        }
    }
}
