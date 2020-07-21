/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.common.ConcurrentMemoization;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class FieldAccessingSolutionCloner<Solution_> implements SolutionCloner<Solution_> {

    protected final SolutionDescriptor<Solution_> solutionDescriptor;

    protected final ConcurrentMap<Class<?>, Constructor<?>> constructorMemoization = new ConcurrentMemoization<>();
    protected final ConcurrentMap<Class<?>, List<Field>> fieldListMemoization = new ConcurrentMemoization<>();
    protected final ConcurrentMap<Pair<Field, Class<?>>, Boolean> fieldDeepClonedMemoization = new ConcurrentMemoization<>();
    protected final ConcurrentMap<Class<?>, Boolean> actualValueClassDeepClonedMemoization = new ConcurrentMemoization<>();

    public FieldAccessingSolutionCloner(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Solution_ cloneSolution(Solution_ originalSolution) {
        return new FieldAccessingSolutionClonerRun().cloneSolution(originalSolution);
    }

    /**
     * This method is thread-safe.
     *
     * @param clazz never null
     * @param <C> type
     * @return never null
     */
    @SuppressWarnings("unchecked")
    protected <C> Constructor<C> retrieveCachedConstructor(Class<C> clazz) {
        return (Constructor<C>) constructorMemoization.computeIfAbsent(clazz, key -> {
            Constructor<C> constructor;
            try {
                constructor = clazz.getDeclaredConstructor();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("The class (" + clazz
                        + ") should have a no-arg constructor to create a planning clone.", e);
            }
            constructor.setAccessible(true);
            return constructor;
        });
    }

    /**
     * This method is thread-safe.
     *
     * @param clazz never null
     * @param <C> type
     * @return never null
     */
    protected <C> List<Field> retrieveCachedFields(Class<C> clazz) {
        return fieldListMemoization.computeIfAbsent(clazz, key -> {
            Field[] fields = clazz.getDeclaredFields();
            List<Field> fieldList = new ArrayList<>(fields.length);
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    fieldList.add(field);
                }
            }
            return fieldList;
        });
    }

    /**
     * This method is thread-safe.
     *
     * @param field never null
     * @param fieldInstanceClass never null
     * @param actualValueClass never null
     * @return never null
     */
    protected boolean retrieveDeepCloneDecision(Field field, Class<?> fieldInstanceClass, Class<?> actualValueClass) {
        Pair<Field, Class<?>> pair = Pair.of(field, fieldInstanceClass);
        Boolean deepCloneDecision = fieldDeepClonedMemoization.computeIfAbsent(pair,
                key -> isFieldDeepCloned(field, fieldInstanceClass));
        return deepCloneDecision || retrieveDeepCloneDecisionForActualValueClass(actualValueClass);
    }

    private boolean isFieldDeepCloned(Field field, Class<?> fieldInstanceClass) {
        if (field.getType().isEnum()) {
            return false;
        }
        return isFieldAnEntityPropertyOnSolution(field, fieldInstanceClass)
                || isFieldAnEntityOrSolution(field, fieldInstanceClass)
                || isFieldADeepCloneProperty(field, fieldInstanceClass);
    }

    protected boolean isFieldAnEntityPropertyOnSolution(Field field, Class<?> fieldInstanceClass) {
        // field.getDeclaringClass() is a superclass of or equal to the fieldInstanceClass
        if (solutionDescriptor.getSolutionClass().isAssignableFrom(fieldInstanceClass)) {
            String fieldName = field.getName();
            // This assumes we're dealing with a simple getter/setter.
            // If that assumption is false, validateCloneSolution(...) fails-fast.
            if (solutionDescriptor.getEntityMemberAccessorMap().get(fieldName) != null) {
                return true;
            }
            // This assumes we're dealing with a simple getter/setter.
            // If that assumption is false, validateCloneSolution(...) fails-fast.
            if (solutionDescriptor.getEntityCollectionMemberAccessorMap().get(fieldName) != null) {
                return true;
            }
        }
        return false;
    }

    protected boolean isFieldAnEntityOrSolution(Field field, Class<?> fieldInstanceClass) {
        Class<?> type = field.getType();
        if (isClassDeepCloned(type)) {
            return true;
        }
        if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
            if (isTypeArgumentDeepCloned(field.getGenericType())) {
                return true;
            }
        } else if (type.isArray()) {
            if (isClassDeepCloned(type.getComponentType())) {
                return true;
            }
        }
        return false;
    }

    private boolean isTypeArgumentDeepCloned(Type genericType) {
        // Check the generic type arguments of the field.
        // Yes, it is possible for fields and methods, but not instances!
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            for (Type actualTypeArgument : parameterizedType.getActualTypeArguments()) {
                if (actualTypeArgument instanceof Class
                        && isClassDeepCloned((Class) actualTypeArgument)) {
                    return true;
                }
                if (isTypeArgumentDeepCloned(actualTypeArgument)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFieldADeepCloneProperty(Field field, Class<?> fieldInstanceClass) {
        if (field.isAnnotationPresent(DeepPlanningClone.class)) {
            return true;
        }
        Method getterMethod = ReflectionHelper.getGetterMethod(fieldInstanceClass, field.getName());
        if (getterMethod != null && getterMethod.isAnnotationPresent(DeepPlanningClone.class)) {
            return true;
        }
        return false;
    }

    /**
     * This method is thread-safe.
     *
     * @param actualValueClass never null
     * @return never null
     */
    protected boolean retrieveDeepCloneDecisionForActualValueClass(Class<?> actualValueClass) {
        return actualValueClassDeepClonedMemoization.computeIfAbsent(actualValueClass,
                key -> isClassDeepCloned(actualValueClass));
    }

    protected boolean isClassDeepCloned(Class<?> type) {
        return solutionDescriptor.hasEntityDescriptor(type)
                || solutionDescriptor.getSolutionClass().isAssignableFrom(type)
                || type.isAnnotationPresent(DeepPlanningClone.class);
    }

    protected class FieldAccessingSolutionClonerRun {

        protected Map<Object, Object> originalToCloneMap;
        protected Queue<Unprocessed> unprocessedQueue;

        protected Solution_ cloneSolution(Solution_ originalSolution) {
            int entityCount = solutionDescriptor.getEntityCount(originalSolution);
            unprocessedQueue = new ArrayDeque<>(entityCount + 1);
            originalToCloneMap = new IdentityHashMap<>(
                    entityCount + 1);
            Solution_ cloneSolution = clone(originalSolution);
            processQueue();
            validateCloneSolution(originalSolution, cloneSolution);
            return cloneSolution;
        }

        protected <C> C clone(C original) {
            if (original == null) {
                return null;
            }
            C existingClone = (C) originalToCloneMap.get(original);
            if (existingClone != null) {
                return existingClone;
            }
            Class<C> instanceClass = (Class<C>) original.getClass();
            C clone = constructClone(instanceClass);
            originalToCloneMap.put(original, clone);
            copyFields(instanceClass, instanceClass, original, clone);
            return clone;
        }

        protected <C> C constructClone(Class<C> clazz) {
            try {
                Constructor<C> constructor = retrieveCachedConstructor(clazz);
                return constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("The class (" + clazz
                        + ") should have a no-arg constructor to create a planning clone.", e);
            }
        }

        protected <C> void copyFields(Class<C> clazz, Class<? extends C> instanceClass, C original, C clone) {
            for (Field field : retrieveCachedFields(clazz)) {
                Object originalValue = getFieldValue(original, field);
                if (isDeepCloneField(field, instanceClass, originalValue)) {
                    // Postpone filling in the fields
                    unprocessedQueue.add(new Unprocessed(clone, field, originalValue));
                } else {
                    // Shallow copy
                    setFieldValue(clone, field, originalValue);
                }
            }
            Class<? super C> superclass = clazz.getSuperclass();
            if (superclass != null) {
                copyFields(superclass, instanceClass, original, clone);
            }
        }

        protected boolean isDeepCloneField(Field field, Class<?> fieldInstanceClass, Object originalValue) {
            if (originalValue == null) {
                return false;
            }
            return retrieveDeepCloneDecision(field, fieldInstanceClass, originalValue.getClass());
        }

        protected void processQueue() {
            while (!unprocessedQueue.isEmpty()) {
                Unprocessed unprocessed = unprocessedQueue.remove();
                process(unprocessed);
            }
        }

        protected void process(Unprocessed unprocessed) {
            Object cloneValue;
            if (unprocessed.originalValue instanceof Collection) {
                cloneValue = cloneCollection(unprocessed.field.getType(), (Collection<?>) unprocessed.originalValue);
            } else if (unprocessed.originalValue instanceof Map) {
                cloneValue = cloneMap(unprocessed.field.getType(), (Map<?, ?>) unprocessed.originalValue);
            } else if (unprocessed.originalValue.getClass().isArray()) {
                cloneValue = cloneArray(unprocessed.field.getType(), unprocessed.originalValue);
            } else {
                cloneValue = clone(unprocessed.originalValue);
            }
            setFieldValue(unprocessed.bean, unprocessed.field, cloneValue);
        }

        protected Object cloneArray(Class<?> expectedType, Object originalArray) {
            int arrayLength = Array.getLength(originalArray);
            Object cloneArray = Array.newInstance(originalArray.getClass().getComponentType(), arrayLength);
            if (!expectedType.isInstance(cloneArray)) {
                throw new IllegalStateException("The cloneArrayClass (" + cloneArray.getClass()
                        + ") created for originalArrayClass (" + originalArray.getClass()
                        + ") is not assignable to the field's type (" + expectedType + ").\n"
                        + "Maybe consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
            }
            for (int i = 0; i < arrayLength; i++) {
                Object cloneElement = cloneCollectionsElementIfNeeded(Array.get(originalArray, i));
                Array.set(cloneArray, i, cloneElement);
            }
            return cloneArray;
        }

        protected <E> Collection<E> cloneCollection(Class<?> expectedType, Collection<E> originalCollection) {
            Collection<E> cloneCollection = constructCloneCollection(originalCollection);
            if (!expectedType.isInstance(cloneCollection)) {
                throw new IllegalStateException("The cloneCollectionClass (" + cloneCollection.getClass()
                        + ") created for originalCollectionClass (" + originalCollection.getClass()
                        + ") is not assignable to the field's type (" + expectedType + ").\n"
                        + "Maybe consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
            }
            for (E originalElement : originalCollection) {
                E cloneElement = cloneCollectionsElementIfNeeded(originalElement);
                cloneCollection.add(cloneElement);
            }
            return cloneCollection;
        }

        protected <E> Collection<E> constructCloneCollection(Collection<E> originalCollection) {
            // TODO Don't hardcode all standard collections
            if (originalCollection instanceof List) {
                if (originalCollection instanceof ArrayList) {
                    return new ArrayList<>(originalCollection.size());
                } else if (originalCollection instanceof LinkedList) {
                    return new LinkedList<>();
                } else { // Default List
                    return new ArrayList<>(originalCollection.size());
                }
            } else if (originalCollection instanceof Set) {
                if (originalCollection instanceof SortedSet) {
                    Comparator<E> setComparator = ((SortedSet) originalCollection).comparator();
                    return new TreeSet<>(setComparator);
                } else if (originalCollection instanceof LinkedHashSet) {
                    return new LinkedHashSet<>(originalCollection.size());
                } else if (originalCollection instanceof HashSet) {
                    return new HashSet<>(originalCollection.size());
                } else { // Default Set
                    // Default to a LinkedHashSet to respect order
                    return new LinkedHashSet<>(originalCollection.size());
                }
            } else if (originalCollection instanceof Deque) {
                return new ArrayDeque<>(originalCollection.size());
            } else { // Default collection
                return new ArrayList<>(originalCollection.size());
            }
        }

        protected <K, V> Map<K, V> cloneMap(Class<?> expectedType, Map<K, V> originalMap) {
            Map<K, V> cloneMap = constructCloneMap(originalMap);
            if (!expectedType.isInstance(cloneMap)) {
                throw new IllegalStateException("The cloneMapClass (" + cloneMap.getClass()
                        + ") created for originalMapClass (" + originalMap.getClass()
                        + ") is not assignable to the field's type (" + expectedType + ").\n"
                        + "Maybe consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
            }
            for (Map.Entry<K, V> originalEntry : originalMap.entrySet()) {
                K cloneKey = cloneCollectionsElementIfNeeded(originalEntry.getKey());
                V cloneValue = cloneCollectionsElementIfNeeded(originalEntry.getValue());
                cloneMap.put(cloneKey, cloneValue);
            }
            return cloneMap;
        }

        protected <K, V> Map<K, V> constructCloneMap(Map<K, V> originalMap) {
            // Normally a Map will never be selected for cloning, but extending implementations might anyway
            if (originalMap instanceof SortedMap) {
                Comparator<K> setComparator = ((SortedMap) originalMap).comparator();
                return new TreeMap<>(setComparator);
            } else if (originalMap instanceof LinkedHashMap) {
                return new LinkedHashMap<>(originalMap.size());
            } else if (originalMap instanceof HashMap) {
                return new HashMap<>(originalMap.size());
            } else { // Default Map
                // Default to a LinkedHashMap to respect order
                return new LinkedHashMap<>(originalMap.size());
            }
        }

        protected <C> C cloneCollectionsElementIfNeeded(C original) {
            // Because an element which is itself a Collection or Map might hold an entity, we clone it too
            // Also, the List<Long> in Map<String, List<Long>> needs to be cloned
            // if the List<Long> is a shadow, despite that Long never needs to be cloned (because it's immutable).
            if (original instanceof Collection) {
                return (C) cloneCollection(Collection.class, (Collection) original);
            } else if (original instanceof Map) {
                return (C) cloneMap(Map.class, (Map) original);
            } else if (original.getClass().isArray()) {
                return (C) cloneArray(original.getClass(), original);
            }
            if (retrieveDeepCloneDecisionForActualValueClass(original.getClass())) {
                return clone(original);
            } else {
                return original;
            }
        }

        /**
         * Fails fast if {@link #isFieldAnEntityPropertyOnSolution} assumptions were wrong.
         *
         * @param originalSolution never null
         * @param cloneSolution never null
         */
        protected void validateCloneSolution(Solution_ originalSolution, Solution_ cloneSolution) {
            for (MemberAccessor memberAccessor : solutionDescriptor.getEntityMemberAccessorMap().values()) {
                Object originalProperty = memberAccessor.executeGetter(originalSolution);
                if (originalProperty != null) {
                    Object cloneProperty = memberAccessor.executeGetter(cloneSolution);
                    if (originalProperty == cloneProperty) {
                        throw new IllegalStateException(
                                "The solutionProperty (" + memberAccessor.getName() + ") was not cloned as expected."
                                        + " The " + FieldAccessingSolutionCloner.class.getSimpleName() + " failed to recognize"
                                        + " that property's field, probably because its field name is different.");
                    }
                }
            }
            for (MemberAccessor memberAccessor : solutionDescriptor.getEntityCollectionMemberAccessorMap().values()) {
                Object originalProperty = memberAccessor.executeGetter(originalSolution);
                if (originalProperty != null) {
                    Object cloneProperty = memberAccessor.executeGetter(cloneSolution);
                    if (originalProperty == cloneProperty) {
                        throw new IllegalStateException(
                                "The solutionProperty (" + memberAccessor.getName() + ") was not cloned as expected."
                                        + " The " + FieldAccessingSolutionCloner.class.getSimpleName() + " failed to recognize"
                                        + " that property's field, probably because its field name is different.");
                    }
                }
            }
        }

        protected Object getFieldValue(Object bean, Field field) {
            try {
                return field.get(bean);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("The class (" + bean.getClass() + ") has a field (" + field
                        + ") which cannot be read to create a planning clone.", e);
            }
        }

        protected void setFieldValue(Object bean, Field field, Object value) {
            try {
                field.set(bean, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("The class (" + bean.getClass() + ") has a field (" + field
                        + ") which cannot be written with the value (" + value + ") to create a planning clone.", e);
            }
        }

    }

    protected static class Unprocessed {

        protected Object bean;
        protected Field field;
        protected Object originalValue;

        public Unprocessed(Object bean, Field field, Object originalValue) {
            this.bean = bean;
            this.field = field;
            this.originalValue = originalValue;
        }

    }

}
