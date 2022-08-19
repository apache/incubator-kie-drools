/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloneHelperFactory {

    private static final Logger logger = LoggerFactory.getLogger(CloneHelperFactory.class);

    private CloneHelperFactory() {
    }

    public static UnaryOperator<Object> getCloner(Class<?> type) {
        // handling cloneable
        if (Cloneable.class.isAssignableFrom(type)) {
            try {
                Method m = type.getMethod("clone");
                return o -> {
                    try {
                        return m.invoke(o);
                    } catch (ReflectiveOperationException ex) {
                        throw new IllegalStateException(type + " implements cloneable but invocation to clone method failed", ex);
                    }
                };
            } catch (NoSuchMethodException ex) {
                logger.warn(type + " implements cloneable but clone method cannot be found", ex);
            }
        }

        // search copy constructor
        return findCopyConstructor(type).<UnaryOperator<Object>> map(c -> o -> {
            try {
                return c.newInstance(o);
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException("Error cloning object " + o + " using copy constructor", ex);
            }
        }).orElse(o -> o);
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
