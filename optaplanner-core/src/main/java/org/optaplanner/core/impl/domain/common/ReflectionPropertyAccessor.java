/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.domain.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wraps {@link PropertyDescriptor} for faster and easier access.
 */
public final class ReflectionPropertyAccessor implements PropertyAccessor {

    private final PropertyDescriptor propertyDescriptor;
    private final Method readMethod;
    private final Method writeMethod;

    public ReflectionPropertyAccessor(PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
        readMethod = propertyDescriptor.getReadMethod();
        if (readMethod != null) {
            readMethod.setAccessible(true); // Performance hack by avoiding security checks
        }
        writeMethod = propertyDescriptor.getWriteMethod();
        if (writeMethod != null) {
            writeMethod.setAccessible(true); // Performance hack by avoiding security checks
        }
    }

    public Method getReadMethod() {
        return readMethod;
    }

    public Method getWriteMethod() {
        return writeMethod;
    }

    public String getName() {
        return propertyDescriptor.getName();
    }

    public Class<?> getPropertyType() {
        return propertyDescriptor.getPropertyType();
    }

    public Object executeGetter(Object bean) {
        try {
            return readMethod.invoke(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call property (" + propertyDescriptor.getName()
                    + ") getter on bean of class (" + bean.getClass() + ").", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The property (" + propertyDescriptor.getName()
                    + ") getter on bean of class (" + bean.getClass() + ") throws an exception.",
                    e.getCause());
        }
    }

    public void executeSetter(Object bean, Object value) {
        try {
            writeMethod.invoke(bean, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call property (" + propertyDescriptor.getName()
                    + ") setter on bean of class (" + bean.getClass() + ").", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The property (" + propertyDescriptor.getName()
                    + ") setter on bean of class (" + bean.getClass() + ") throws an exception.",
                    e.getCause());
        }
    }

}
