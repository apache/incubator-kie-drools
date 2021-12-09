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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
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

    public static Method getMethod(ClassLoader cl, Class<?> clazz, String methodName, Collection<String> parameterTypes) throws ReflectiveOperationException {
        Class<?>[] methodParameters = new Class[parameterTypes.size()];
        int i = 0;
        for (String parameterType : parameterTypes) {
            if (!parameterType.contains(".")) {
                parameterType = "java.lang." + parameterType;
            }
            methodParameters[i++] = cl.loadClass(parameterType);
        }
        try {
            return clazz.getMethod(methodName, methodParameters);
        } catch (NoSuchMethodException ex) {
            logger.info("Exact method {} match not found with parameters {}, searching for a suitable candidate", methodName, methodParameters);
            return fallbackMethod(clazz, methodName, methodParameters);
        }
    }

    private static Method fallbackMethod(Class<?> clazz, String methodName, Class<?>[] methodParameters) throws NoSuchMethodException {
        List<Method> candidates = new ArrayList<>();
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(methodName)) {
                int diff = m.getParameterCount() - methodParameters.length;
                if (diff == 0 || diff == 1) {
                    Class<?>[] thisMethodParams = m.getParameterTypes();
                    boolean valid = true;
                    boolean potentiallyValid = true;
                    for (int i = 0; potentiallyValid && i < methodParameters.length; i++) {
                        valid = isValid(thisMethodParams[i], methodParameters[i]);
                        potentiallyValid = valid || methodParameters[i].equals(java.lang.Object.class);
                    }
                    if (diff == 0 || isContext(m)) {
                        if (valid) {
                            return m;
                        } else if (potentiallyValid) {
                            candidates.add(m);
                        }
                    }
                }
            }
        }
        if (candidates.size() != 1) {
            throw new NoSuchMethodException(candidates.isEmpty() ? "No suitable method found with name " + methodName : "More than one suitable method found " + candidates);
        } else {
            return candidates.get(0);
        }
    }

    private static boolean isContext(Method m) {
        return checkExtraArg(m, KogitoProcessContext.class);
    }

    private static boolean checkExtraArg(Method m, Class<?> checkClass) {
        return checkClass.isAssignableFrom(m.getParameterTypes()[m.getParameterCount() - 1]);
    }

    private static boolean isValid(Class<?> thisMethodParam, Class<?> methodParam) {
        boolean isValid = thisMethodParam.isAssignableFrom(methodParam);
        if (!isValid) {
            Class<?> primitive = wrappers2Primitive.get(methodParam);
            if (primitive != null) {
                isValid = thisMethodParam.isAssignableFrom(primitive);
            }
        }
        return isValid;
    }
}
