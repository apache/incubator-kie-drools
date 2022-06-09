package org.optaplanner.core.impl.domain.common.accessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * A {@link MemberAccessor} based on a single read {@link Method}.
 * Do not confuse with {@link ReflectionBeanPropertyMemberAccessor} which is richer.
 */
public final class ReflectionMethodMemberAccessor extends AbstractMemberAccessor {

    private final Class<?> returnType;
    private final String methodName;
    private final Method readMethod;

    public ReflectionMethodMemberAccessor(Method readMethod) {
        this.readMethod = readMethod;
        readMethod.setAccessible(true); // Performance hack by avoiding security checks
        returnType = readMethod.getReturnType();
        methodName = readMethod.getName();
        if (readMethod.getParameterTypes().length != 0) {
            throw new IllegalArgumentException("The readMethod (" + readMethod + ") must not have any parameters ("
                    + Arrays.toString(readMethod.getParameterTypes()) + ").");
        }
        if (readMethod.getReturnType() == void.class) {
            throw new IllegalArgumentException("The readMethod (" + readMethod + ") must have a return type ("
                    + readMethod.getReturnType() + ").");
        }
    }

    @Override
    public Class<?> getDeclaringClass() {
        return readMethod.getDeclaringClass();
    }

    @Override
    public String getName() {
        return methodName;
    }

    @Override
    public Class<?> getType() {
        return returnType;
    }

    @Override
    public Type getGenericType() {
        return readMethod.getGenericReturnType();
    }

    @Override
    public Object executeGetter(Object bean) {
        try {
            return readMethod.invoke(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call property (" + methodName
                    + ") getterMethod (" + readMethod + ") on bean of class (" + bean.getClass() + ").\n" +
                    MemberAccessorFactory.CLASSLOADER_NUDGE_MESSAGE, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The property (" + methodName
                    + ") getterMethod (" + readMethod + ") on bean of class (" + bean.getClass()
                    + ") throws an exception.",
                    e.getCause());
        }
    }

    @Override
    public String getSpeedNote() {
        return "slow access with reflection";
    }

    @Override
    public boolean supportSetter() {
        return false;
    }

    @Override
    public void executeSetter(Object bean, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return readMethod.getAnnotation(annotationClass);
    }

    @Override
    public String toString() {
        return "method " + methodName + " on " + readMethod.getDeclaringClass();
    }

}
