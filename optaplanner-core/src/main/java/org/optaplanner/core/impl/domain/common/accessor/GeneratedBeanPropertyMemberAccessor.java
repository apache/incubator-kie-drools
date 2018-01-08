/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.common.accessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.compiler.StringGeneratedJavaCompilerFacade;

/**
 * A {@link MemberAccessor} based on a getter and optionally a setter.
 */
public abstract class GeneratedBeanPropertyMemberAccessor implements MemberAccessor {

    public static GeneratedBeanPropertyMemberAccessor generateBeanPropertyMemberAccessor(Method getterMethod) {
        Class<?> declaringClass = getterMethod.getDeclaringClass();
        if (!ReflectionHelper.isGetterMethod(getterMethod)) {
            throw new IllegalArgumentException("The getterMethod (" + getterMethod + ") is not a valid getter.");
        }
        Class<?> propertyType = getterMethod.getReturnType();
        String propertyName = ReflectionHelper.getGetterPropertyName(getterMethod);
        Method setterMethod = ReflectionHelper.getSetterMethod(declaringClass, getterMethod.getReturnType(), propertyName);

        String packageName = MemberAccessorFactory.class.getPackage().getName()
                + ".generated." + declaringClass.getPackage().getName();
        String simpleClassName = declaringClass.getSimpleName() + "$" + getterMethod.getName();
        String fullClassName = packageName + "." + simpleClassName;
        final String source = "package " + packageName + ";\n"
                + "import java.lang.reflect.Method;\n"
                + "public final class " + simpleClassName + " extends " + GeneratedBeanPropertyMemberAccessor.class.getName() + " {\n"
                + "    public " + simpleClassName + "(Class<?> propertyType, String propertyName, Method getterMethod, Method setterMethod) {\n"
                + "        super(propertyType, propertyName, getterMethod, setterMethod);\n"
                + "    }\n"
                + "    public Object executeGetter(Object bean) {\n"
                + "        return ((" + declaringClass.getName() + ") bean)." + getterMethod.getName() + "();\n"
                + "    }\n"
                + "    public void executeSetter(Object bean, Object value) {\n"
                + ((setterMethod == null) ? "        throw new UnsupportedOperationException();"
                : "        ((" + declaringClass.getName() + ") bean)." + setterMethod.getName() + "((" + propertyType.getName() + ") value);\n")
                + "    }\n"
                + "}";
        StringGeneratedJavaCompilerFacade compilerFacade = new StringGeneratedJavaCompilerFacade(
                MemberAccessorFactory.class.getClassLoader());
        Class<? extends GeneratedBeanPropertyMemberAccessor> compiledClass = compilerFacade.compile(
                fullClassName, source, GeneratedBeanPropertyMemberAccessor.class);
        try {
            return compiledClass.getConstructor(Class.class, String.class, Method.class, Method.class)
                    .newInstance(propertyType, propertyName, getterMethod, setterMethod);
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("The generated class (" + fullClassName + ") failed to instantiate.", e);
        }
    }

    protected final Class<?> propertyType;
    protected final String propertyName;
    private final Method getterMethod;
    private final Method setterMethod;

    public GeneratedBeanPropertyMemberAccessor(Class<?> propertyType, String propertyName,
            Method getterMethod, Method setterMethod) {
        this.propertyType = propertyType;
        this.propertyName = propertyName;
        this.getterMethod = getterMethod;
        this.setterMethod = setterMethod;
    }

    @Override
    public String getName() {
        return propertyName;
    }

    @Override
    public Class<?> getType() {
        return propertyType;
    }

    @Override
    public Type getGenericType() {
        return getterMethod.getGenericReturnType();
    }

    @Override
    public boolean supportSetter() {
        return setterMethod != null;
    }

    @Override
    public String getSpeedNote() {
        return "fast access with generated code";
    }

    // ************************************************************************
    // AnnotatedElement methods
    // ************************************************************************

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return getterMethod.isAnnotationPresent(annotationClass);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return getterMethod.getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return getterMethod.getAnnotations();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return getterMethod.getDeclaredAnnotations();
    }

    @Override
    public String toString() {
        return "bean property " + propertyName + " on " + getterMethod.getDeclaringClass();
    }

}
