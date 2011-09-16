/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.domain.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public class DescriptorUtils {

    public static Object executeGetter(PropertyDescriptor propertyDescriptor, Object bean) {
        try {
            return propertyDescriptor.getReadMethod().invoke(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call property (" + propertyDescriptor.getName()
                    + ") getter on bean of class (" + bean.getClass() + ").", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The property (" + propertyDescriptor.getName()
                    + ") getter on bean of class (" + bean.getClass() + ") throws an exception.",
                    e.getCause());
        }
    }

    public static void executeSetter(PropertyDescriptor propertyDescriptor, Object bean, Object value) {
        // TODO generated
        try {
            propertyDescriptor.getWriteMethod().invoke(bean, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call property (" + propertyDescriptor.getName()
                    + ") setter on bean of class (" + bean.getClass() + ").", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The property (" + propertyDescriptor.getName()
                    + ") setter on bean of class (" + bean.getClass() + ") throws an exception.",
                    e.getCause());
        }
    }

    private DescriptorUtils() {
    }

}
