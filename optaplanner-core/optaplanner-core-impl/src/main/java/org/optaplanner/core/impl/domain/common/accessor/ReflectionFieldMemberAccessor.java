package org.optaplanner.core.impl.domain.common.accessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * A {@link MemberAccessor} based on a field.
 */
public final class ReflectionFieldMemberAccessor extends AbstractMemberAccessor {

    private final Field field;

    public ReflectionFieldMemberAccessor(Field field) {
        this.field = field;
        // Performance hack by avoiding security checks
        field.setAccessible(true);
    }

    @Override
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }

    @Override
    public Object executeGetter(Object bean) {
        try {
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot get the field (" + field.getName()
                    + ") on bean of class (" + bean.getClass() + ").\n" +
                    MemberAccessorFactory.CLASSLOADER_NUDGE_MESSAGE, e);
        }
    }

    @Override
    public boolean supportSetter() {
        return true;
    }

    @Override
    public void executeSetter(Object bean, Object value) {
        try {
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot set the field (" + field.getName()
                    + ") on bean of class (" + bean.getClass() + ").", e);
        }
    }

    @Override
    public String getSpeedNote() {
        return "slow access with reflection";
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    @Override
    public String toString() {
        return "field " + field;
    }

}
