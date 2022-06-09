package org.optaplanner.core.impl.domain.common.accessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Fast and easy access to a {@link Member} of a bean,
 * which is a property (with a getter and optional setter {@link Method}) or a {@link Field}.
 *
 * @see ReflectionBeanPropertyMemberAccessor
 * @see ReflectionFieldMemberAccessor
 * @see ReflectionMethodMemberAccessor
 */
public interface MemberAccessor {

    Class<?> getDeclaringClass();

    String getName();

    Class<?> getType();

    /**
     * As defined by {@link Method#getGenericReturnType()} and {@link Field#getGenericType()}.
     *
     * @return never null
     */
    Type getGenericType();

    Object executeGetter(Object bean);

    /**
     * In order to support node sharing in constraint streams, we need to reference {@link #executeGetter(Object)}
     * in a way so that the method reference stays the same instance.
     * This method returns just such a method reference.
     *
     * @param <Result_>
     * @return never null, a constant reference to {@link #executeGetter(Object)}
     */
    <Fact_, Result_> Function<Fact_, Result_> getGetterFunction();

    boolean supportSetter();

    void executeSetter(Object bean, Object value);

    String getSpeedNote();

    /**
     * As defined in {@link AnnotatedElement#getAnnotation(Class)}.
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

}
