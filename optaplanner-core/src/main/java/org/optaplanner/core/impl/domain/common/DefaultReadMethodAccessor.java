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
public final class DefaultReadMethodAccessor implements ReadMethodAccessor {

    private final Method readMethod;
    private final Class<?> returnType;

    public DefaultReadMethodAccessor(Method readMethod) {
        this.readMethod = readMethod;
        readMethod.setAccessible(true); // Performance hack by avoiding security checks
        returnType = readMethod.getReturnType();
        if (returnType == Void.TYPE) {
            throw new IllegalArgumentException("The readMethod (" + readMethod
                    + ") must not have the returnType (" + Void.TYPE + ")");
        }
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Method getReadMethod() {
        return readMethod;
    }

    public Object read(Object object) {
        try {
            return readMethod.invoke(object);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call readMethod (" + readMethod.getName()
                    + ") on an instance of class (" + object.getClass() + ").", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The readMethod (" + readMethod.getName()
                    + ") on an instance of class (" + object.getClass() + ") throws an exception.",
                    e.getCause());
        }
    }

}
