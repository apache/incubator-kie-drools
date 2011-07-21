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
