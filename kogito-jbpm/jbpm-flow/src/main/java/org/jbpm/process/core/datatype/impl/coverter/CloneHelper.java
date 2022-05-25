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
package org.jbpm.process.core.datatype.impl.coverter;

import java.lang.reflect.Constructor;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloneHelper {

    private static final Logger logger = LoggerFactory.getLogger(CloneHelper.class);

    private CloneHelper() {
    }

    public static <T> T clone(T o) {
        Class<?> type = o.getClass();

        // handling cloneable
        if (Cloneable.class.isAssignableFrom(type)) {
            try {
                return (T) type.getMethod("clone").invoke(o);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException(type + " implements cloneable but clone method cannot be found", ex);
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException(type + " implements cloneable but invocation to clone method failed", ex);
            }
        }

        // search copy constructor
        Optional<Constructor<?>> copyConstructor = findCopyConstructor(type);
        if (copyConstructor.isPresent()) {
            try {
                return (T) copyConstructor.get().newInstance(o);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(
                        "cannot clone object " + o + " of type " + type + " using copy constructor. There was a failure when invoking it. Please review copy constructor implementation", e);
            }
        }

        logger.warn("Object cannot be cloned. Please either register a cloner, implements cloneable or provide copy constructor. Returning same instance");
        return o;
    }

    private static Optional<Constructor<?>> findCopyConstructor(Class<?> type) {

        try {
            return Optional.of(type.getConstructor(type));
        } catch (ReflectiveOperationException ex) {
            for (Constructor<?> constructor : type.getConstructors()) {
                if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].isAssignableFrom(type)) {
                    return Optional.of(constructor);
                }
            }
        }
        logger.debug("Cannot find copy constructor for type {}", type);
        return Optional.empty();
    }

}
