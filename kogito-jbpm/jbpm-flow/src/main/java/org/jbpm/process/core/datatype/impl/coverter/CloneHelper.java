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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloneHelper {

    private static final Logger logger = LoggerFactory.getLogger(CloneHelper.class);
    private static final CloneHelper instance = new CloneHelper(CloneHelperRegister.get().getCloners());

    public static synchronized CloneHelper get() {
        return instance;
    }

    private Map<Class<?>, UnaryOperator<?>> cloners;
    private final Map<Class<?>, UnaryOperator<?>> registeredCloners;

    private CloneHelper(Map<Class<?>, UnaryOperator<?>> registeredCloners) {
        this.registeredCloners = registeredCloners;
        this.cloners = new ConcurrentHashMap<>(registeredCloners);
    }

    @SuppressWarnings("unchecked")
    public <T> T clone(T value) {
        return value == null ? value : getCloner((Class<T>) value.getClass()).apply(value);
    }

    @SuppressWarnings("unchecked")
    public <T> UnaryOperator<T> getCloner(Class<T> type) {
        return (UnaryOperator<T>) cloners.computeIfAbsent(type, this::searchCloner);
    }

    private UnaryOperator<?> searchCloner(Class<?> type) {
        return searchRegistered(type)
                .or(() -> searchCopyConstructor(type))
                .or(() -> searchCloneable(type))
                .orElse(o -> o);
    }

    private Optional<UnaryOperator<?>> searchRegistered(Class<?> type) {
        return registeredCloners.entrySet().stream().filter(e -> e.getKey().isAssignableFrom(type)).<UnaryOperator<?>> map(Entry::getValue).findFirst();
    }

    private Optional<UnaryOperator<?>> searchCloneable(Class<?> type) {
        if (Cloneable.class.isAssignableFrom(type)) {
            try {
                Method m = type.getMethod("clone");
                return Optional.of(o -> {
                    try {
                        return m.invoke(o);
                    } catch (ReflectiveOperationException ex) {
                        throw new IllegalStateException(type + " implements cloneable but invocation to clone method failed", ex);
                    }
                });
            } catch (NoSuchMethodException ex) {
                logger.warn("{} implements cloneable but clone method cannot be found", type);
            }
        }
        return Optional.empty();
    }

    private Optional<UnaryOperator<?>> searchCopyConstructor(Class<?> type) {
        return findCopyConstructor(type).map(c -> o -> {
            try {
                return c.newInstance(o);
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException("Error cloning object " + o + " using copy constructor", ex);
            }
        });
    }

    private Optional<Constructor<?>> findCopyConstructor(Class<?> type) {
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
