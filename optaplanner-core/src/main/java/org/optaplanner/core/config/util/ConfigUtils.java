/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.impl.domain.common.AlphabeticMemberComparator;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory;

import static org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory.MemberAccessorType.*;

public class ConfigUtils {

    private static final AlphabeticMemberComparator alphabeticMemberComparator = new AlphabeticMemberComparator();

    public static <T> T newInstance(Object bean, String propertyName, Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("The " + bean.getClass().getSimpleName() + "'s " + propertyName + " ("
                    + clazz.getName() + ") does not have a public no-arg constructor"
                    // Inner classes include local, anonymous and non-static member classes
                    + ((clazz.isLocalClass() || clazz.isAnonymousClass() || clazz.isMemberClass())
                    && !Modifier.isStatic(clazz.getModifiers()) ? " because it is an inner class." : "."), e);
        }
    }

    public static void applyCustomProperties(Object bean, String beanClassPropertyName,
            Map<String, String> customProperties, String customPropertiesPropertyName) {
        if (customProperties == null) {
            return;
        }
        Class<?> beanClass = bean.getClass();
        customProperties.forEach((propertyName, valueString) -> {
            Method setterMethod = ReflectionHelper.getSetterMethod(beanClass, propertyName);
            if (setterMethod == null) {
                throw new IllegalStateException("The custom property " + propertyName + " (" + valueString
                        + ") in the " + customPropertiesPropertyName
                        + " cannot be set on the " + beanClassPropertyName + " (" + beanClass
                        + ") because that class has no public setter for that property.\n"
                        + "Maybe add a public setter for that custom property (" + propertyName
                        + ") on that class (" + beanClass.getSimpleName() + ").\n"
                        + "Maybe don't configure that custom property " + propertyName + " (" + valueString
                        + ") in the " + customPropertiesPropertyName + ".");
            }
            Class<?> propertyType = setterMethod.getParameterTypes()[0];
            Object typedValue;
            try {
                if (propertyType.equals(String.class)) {
                    typedValue = valueString;
                } else if (propertyType.equals(Boolean.TYPE) || propertyType.equals(Boolean.class)) {
                    typedValue = Boolean.parseBoolean(valueString);
                } else if (propertyType.equals(Integer.TYPE) || propertyType.equals(Integer.class)) {
                    typedValue = Integer.parseInt(valueString);
                } else if (propertyType.equals(Long.TYPE) || propertyType.equals(Long.class)) {
                    typedValue = Long.parseLong(valueString);
                } else if (propertyType.equals(Float.TYPE) || propertyType.equals(Float.class)) {
                    typedValue = Float.parseFloat(valueString);
                } else if (propertyType.equals(Double.TYPE) || propertyType.equals(Double.class)) {
                    typedValue = Double.parseDouble(valueString);
                } else if (propertyType.equals(BigDecimal.class)) {
                    typedValue = new BigDecimal(valueString);
                } else if (propertyType.isEnum()) {
                    typedValue = Enum.valueOf((Class<? extends Enum>) propertyType, valueString);
                } else {
                    throw new IllegalStateException("The custom property " + propertyName + " (" + valueString
                            + ") in the " + customPropertiesPropertyName
                            + " has an unsupported propertyType (" + propertyType + ") for value (" + valueString + ").");
                }
            } catch (NumberFormatException e) {
                throw new IllegalStateException("The custom property " + propertyName + " (" + valueString
                        + ") in the " + customPropertiesPropertyName
                        + " cannot be parsed to the propertyType (" + propertyType
                        + ") of the setterMethod (" + setterMethod + ").");
            }
            try {
                setterMethod.invoke(bean, typedValue);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("The custom property " + propertyName + " (" + valueString
                        + ") in the " + customPropertiesPropertyName
                        + " has a setterMethod (" + setterMethod + ") on the beanClass (" + beanClass
                        + ") that cannot be called for the typedValue (" + typedValue + ").", e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("The custom property " + propertyName + " (" + valueString
                        + ") in the " + customPropertiesPropertyName
                        + " has a setterMethod (" + setterMethod + ") on the beanClass (" + beanClass
                        + ") that throws an exception for the typedValue (" + typedValue + ").",
                        e.getCause());
            }
        });
    }

    public static <C extends AbstractConfig<C>> C inheritConfig(C original, C inherited) {
        if (inherited != null) {
            if (original == null) {
                original = inherited.copyConfig();
            } else {
                original.inherit(inherited);
            }
        }
        return original;
    }

    public static <C extends AbstractConfig<C>> List<C> inheritMergeableListConfig(List<C> originalList, List<C> inheritedList) {
        if (inheritedList != null) {
            List<C> mergedList = new ArrayList<>(inheritedList.size()
                    + (originalList == null ? 0 : originalList.size()));
            // The inheritedList should be before the originalList
            for (C inherited : inheritedList) {
                C copy = inherited.copyConfig();
                mergedList.add(copy);
            }
            if (originalList != null) {
                mergedList.addAll(originalList);
            }
            originalList = mergedList;
        }
        return originalList;
    }

    public static <T> T inheritOverwritableProperty(T original, T inherited) {
        if (original != null) {
            // Original overwrites inherited
            return original;
        } else {
            return inherited;
        }
    }

    public static <T> List<T> inheritMergeableListProperty(List<T> originalList, List<T> inheritedList) {
        if (inheritedList == null) {
            return originalList;
        } else if (originalList == null) {
            // Shallow clone due to XStream implicit elements and modifications after calling inherit
            return new ArrayList<>(inheritedList);
        } else {
            // The inheritedList should be before the originalList
            List<T> mergedList = new ArrayList<>(inheritedList);
            mergedList.addAll(originalList);
            return mergedList;
        }
    }

    public static <K, T> Map<K, T> inheritMergeableMapProperty(Map<K, T> originalMap, Map<K, T> inheritedMap) {
        if (inheritedMap == null) {
            return originalMap;
        } else if (originalMap == null) {
            return inheritedMap;
        } else {
            // The inheritedMap should be before the originalMap
            Map<K, T> mergedMap = new LinkedHashMap<>(inheritedMap);
            mergedMap.putAll(originalMap);
            return mergedMap;
        }
    }

    public static <T> T mergeProperty(T a, T b) {
        return Objects.equals(a, b) ? a : null;
    }

    /**
     * A relaxed version of {@link #mergeProperty(Object, Object)}. Used primarily for merging failed benchmarks,
     * where a property remains the same over benchmark runs (for example: dataset problem size), but the property in
     * the failed benchmark isn't initialized, therefore null. When merging, we can still use the correctly initialized
     * property of the benchmark that didn't fail.
     * <p>
     * Null-handling:
     * <ul>
     *     <li>if <strong>both</strong> properties <strong>are null</strong>, returns null</li>
     *     <li>if <strong>only one</strong> of the properties <strong>is not null</strong>, returns that property</li>
     *     <li>if <strong>both</strong> properties <strong>are not null</strong>, returns {@link #mergeProperty(Object, Object)}</li>
     * </ul>
     *
     * @see #mergeProperty(Object, Object)
     * @param a property {@code a}
     * @param b property {@code b}
     * @param <T> the type of property {@code a} and {@code b}
     * @return sometimes null
     */
    public static <T> T meldProperty(T a, T b) {
        if (a == null && b == null) {
            return null;
        } else if (a == null && b != null) {
            return b;
        } else if (a != null && b == null) {
            return a;
        } else {
            return ConfigUtils.mergeProperty(a, b);
        }
    }

    public static boolean isEmptyCollection(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Divides and ceils the result without using floating point arithmetic. For floor division,
     * see {@link Math#floorDiv(long, long)}.
     *
     * @throws ArithmeticException if {@code divisor == 0}
     * @param dividend the dividend
     * @param divisor the divisor
     * @return dividend / divisor, ceiled
     */
    public static int ceilDivide(int dividend, int divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero: " + dividend + "/" + divisor);
        }
        int correction;
        if (dividend % divisor == 0) {
            correction = 0;
        } else if (Integer.signum(dividend) * Integer.signum(divisor) < 0) {
            correction = 0;
        } else {
            correction = 1;
        }
        return (dividend / divisor) + correction;
    }

    /**
     * Name of the variable that represents {@link Runtime#availableProcessors()}.
     */
    public static final String AVAILABLE_PROCESSOR_COUNT = "availableProcessorCount";

    public static int resolveThreadPoolSizeScript(String propertyName, String script, String... magicValues) {
        final String scriptLanguage = "JavaScript";
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(scriptLanguage);
        if (scriptEngine == null) {
            throw new IllegalStateException("The " + propertyName + " (" + script
                    + ") could not resolve because the JVM doesn't support scriptLanguage (" + scriptLanguage + ").\n"
                    + "Maybe try running in a normal JVM.");
        }
        scriptEngine.put(AVAILABLE_PROCESSOR_COUNT, Runtime.getRuntime().availableProcessors());
        Object scriptResult;
        try {
            scriptResult = scriptEngine.eval(script);
        } catch (ScriptException e) {
            throw new IllegalArgumentException("The " + propertyName + " (" + script
                    + ") is not in magicValues (" + Arrays.toString(magicValues)
                    + ") and cannot be parsed in " + scriptLanguage
                    + " with the variables ([" + AVAILABLE_PROCESSOR_COUNT + "]).", e);
        }
        if (!(scriptResult instanceof Number)) {
            throw new IllegalArgumentException("The " + propertyName + " (" + script
                    + ") is resolved to scriptResult (" + scriptResult + ") in " + scriptLanguage
                    + " but is not a " + Number.class.getSimpleName() + ".");
        }
        return ((Number) scriptResult).intValue();
    }

    // ************************************************************************
    // Member and annotation methods
    // ************************************************************************

    public static List<Class<?>> getAllAnnotatedLineageClasses(Class<?> bottomClass,
            Class<? extends Annotation> annotation) {
        if (!bottomClass.isAnnotationPresent(annotation)) {
            return Collections.emptyList();
        }
        List<Class<?>> lineageClassList = new ArrayList<>();
        lineageClassList.add(bottomClass);
        Class<?> superclass = bottomClass.getSuperclass();
        lineageClassList.addAll(getAllAnnotatedLineageClasses(superclass, annotation));
        for (Class<?> superInterface : bottomClass.getInterfaces()) {
            lineageClassList.addAll(getAllAnnotatedLineageClasses(superInterface, annotation));
        }
        return lineageClassList;
    }

    /**
     * @param baseClass never null
     * @return never null, sorted by type (fields before methods), then by {@link AlphabeticMemberComparator}.
     */
    public static List<Member> getDeclaredMembers(Class<?> baseClass) {
        Stream<Field> fieldStream = Stream.of(baseClass.getDeclaredFields())
                .sorted(alphabeticMemberComparator);
        Stream<Method> methodStream = Stream.of(baseClass.getDeclaredMethods())
                // A bridge method is a generic variant that duplicates a concrete method
                // Example: "Score getScore()" that duplicates "HardSoftScore getScore()"
                .filter(method -> !method.isBridge())
                .sorted(alphabeticMemberComparator);
        return Stream.<Member>concat(fieldStream, methodStream)
                .collect(Collectors.toList());
    }

    /**
     * @param baseClass never null
     * @param annotationClass never null
     * @return never null, sorted by type (fields before methods), then by {@link AlphabeticMemberComparator}.
     */
    public static List<Member> getAllMembers(Class<?> baseClass, Class<? extends Annotation> annotationClass) {
        Class<?> clazz = baseClass;
        Stream<Member> memberStream = Stream.empty();
        while (clazz != null) {
            Stream<Field> fieldStream = Stream.of(clazz.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(annotationClass))
                    .sorted(alphabeticMemberComparator);
            Stream<Method> methodStream = Stream.of(clazz.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(annotationClass))
                    .sorted(alphabeticMemberComparator);
            memberStream = Stream.concat(memberStream, Stream.concat(fieldStream, methodStream));
            clazz = clazz.getSuperclass();
        }
        return memberStream.collect(Collectors.toList());
    }

    @SafeVarargs
    public static Class<? extends Annotation> extractAnnotationClass(Member member,
            Class<? extends Annotation>... annotationClasses) {
        Class<? extends Annotation> annotationClass = null;
        for (Class<? extends Annotation> detectedAnnotationClass : annotationClasses) {
            if (((AnnotatedElement) member).isAnnotationPresent(detectedAnnotationClass)) {
                if (annotationClass != null) {
                    throw new IllegalStateException("The class (" + member.getDeclaringClass()
                            + ") has a member (" + member + ") that has both a "
                            + annotationClass.getSimpleName() + " annotation and a "
                            + detectedAnnotationClass.getSimpleName() + " annotation.");
                }
                annotationClass = detectedAnnotationClass;
                // Do not break early: check other annotationClasses too
            }
        }
        return annotationClass;
    }

    public static Class<?> extractCollectionGenericTypeParameter(
            String parentClassConcept, Class<?> parentClass,
            Class<?> type, Type genericType,
            Class<? extends Annotation> annotationClass, String memberName) {
        if (!(genericType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("The " + parentClassConcept + " (" + parentClass + ") has a "
                    + (annotationClass == null ? "auto discovered" : annotationClass.getSimpleName() + " annotated")
                    + " member (" + memberName
                    + ") with a member type (" + type
                    + ") that returns a " + Collection.class.getSimpleName()
                    + " which has no generic parameters.\n"
                    + "Maybe the member (" + memberName + ") should return a typed "
                    + Collection.class.getSimpleName() + ".");
        }
        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        if (typeArguments.length != 1) {
            throw new IllegalArgumentException("The " + parentClassConcept + " (" + parentClass + ") has a "
                    + (annotationClass == null ? "auto discovered" : annotationClass.getSimpleName() + " annotated")
                    + " member (" + memberName
                    + ") with a member type (" + type
                    + ") which is parameterized collection with an unsupported number of generic parameters ("
                    + typeArguments.length + ").");
        }
        Type typeArgument = typeArguments[0];
        if (typeArgument instanceof ParameterizedType) {
            // Remove the type parameters so it can be cast to a Class
            typeArgument = ((ParameterizedType) typeArgument).getRawType();
        }
        if (!(typeArgument instanceof Class)) {
            throw new IllegalArgumentException("The " + parentClassConcept + " (" + parentClass + ") has a "
                    + (annotationClass == null ? "auto discovered" : annotationClass.getSimpleName() + " annotated")
                    + " member (" + memberName
                    + ") with a member type (" + type
                    + ") which is parameterized collection with an unsupported type argument ("
                    + typeArgument + ").");
        }
        return ((Class) typeArgument);
    }

    public static <C> MemberAccessor findPlanningIdMemberAccessor(Class<C> clazz) {
        List<Member> memberList = getAllMembers(clazz, PlanningId.class);
        if (memberList.isEmpty()) {
            return null;
        }
        if (memberList.size() > 1) {
            throw new IllegalArgumentException("The class (" + clazz
                    + ") has " + memberList.size() + " members (" + memberList + ") with a "
                    + PlanningId.class.getSimpleName() + " annotation.");
        }
        Member member = memberList.get(0);
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(member, FIELD_OR_READ_METHOD, PlanningId.class);
        if (!memberAccessor.getType().isPrimitive() && !Comparable.class.isAssignableFrom(memberAccessor.getType())) {
            throw new IllegalArgumentException("The class (" + clazz
                    + ") has a member (" + member + ") with a " + PlanningId.class.getSimpleName()
                    + " annotation that returns a type (" + memberAccessor.getType()
                    + ") that does not implement " + Comparable.class.getSimpleName() + ".\n"
                    + "Maybe use an " + Integer.class.getSimpleName()
                    + " or " + String.class.getSimpleName() + " type instead.");
        }
        return memberAccessor;
    }

    // ************************************************************************
    // Private constructor
    // ************************************************************************

    private ConfigUtils() {
    }

}
