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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

public class FieldAccessingSolutionCloner<Solution_ extends Solution> implements SolutionCloner<Solution_> {

    protected final SolutionDescriptor solutionDescriptor;

    protected final Map<Class, Constructor> constructorCache = new HashMap<Class, Constructor>();
    protected final Map<Class, List<Field>> fieldListCache = new HashMap<Class, List<Field>>();
    protected final Map<Pair<Field, Class>, Boolean> deepCloneDecisionFieldCache = new HashMap<Pair<Field, Class>, Boolean>();
    protected final Map<Class, Boolean> deepCloneDecisionActualValueClassCache = new HashMap<Class, Boolean>();

    public FieldAccessingSolutionCloner(SolutionDescriptor solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Solution_ cloneSolution(Solution_ originalSolution) {
        return new FieldAccessingSolutionClonerRun().cloneSolution(originalSolution);
    }

    protected <C> Constructor<C> retrieveCachedConstructor(Class<C> clazz) throws NoSuchMethodException {
        Constructor<C> constructor = constructorCache.get(clazz);
        if (constructor == null) {
            constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructorCache.put(clazz, constructor);
        }
        return constructor;
    }

    protected <C> List<Field> retrieveCachedFields(Class<C> clazz) {
        List<Field> fieldList = fieldListCache.get(clazz);
        if (fieldList == null) {
            Field[] fields = clazz.getDeclaredFields();
            fieldList = new ArrayList<Field>(fields.length);
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    fieldList.add(field);
                }
            }
            fieldListCache.put(clazz, fieldList);
        }
        return fieldList;
    }

    protected boolean retrieveDeepCloneDecision(Field field, Class fieldInstanceClass, Class<?> actualValueClass) {
        Pair<Field, Class> pair = Pair.of(field, fieldInstanceClass);
        Boolean deepCloneDecision = deepCloneDecisionFieldCache.get(pair);
        if (deepCloneDecision == null) {
            deepCloneDecision = isFieldDeepCloned(field, fieldInstanceClass);
            deepCloneDecisionFieldCache.put(pair, deepCloneDecision);
        }
        if (deepCloneDecision) {
            return true;
        }
        return retrieveDeepCloneDecisionForActualValueClass(actualValueClass);
    }

    private boolean isFieldDeepCloned(Field field, Class fieldInstanceClass) {
        return isFieldAnEntityPropertyOnSolution(field, fieldInstanceClass)
                || isFieldAnEntityOrSolution(field, fieldInstanceClass)
                || isFieldADeepCloneProperty(field, fieldInstanceClass);
    }

    protected boolean isFieldAnEntityPropertyOnSolution(Field field, Class fieldInstanceClass) {
        // field.getDeclaringClass() is a superclass of or equal to the fieldInstanceClass
        if (solutionDescriptor.getSolutionClass().isAssignableFrom(fieldInstanceClass)) {
            String fieldName = field.getName();
            // This assumes we're dealing with a simple getter/setter.
            // If that assumption is false, validateCloneSolution(...) fails-fast.
            if (solutionDescriptor.getEntityPropertyAccessorMap().get(fieldName) != null) {
                return true;
            }
            // This assumes we're dealing with a simple getter/setter.
            // If that assumption is false, validateCloneSolution(...) fails-fast.
            if (solutionDescriptor.getEntityCollectionPropertyAccessorMap().get(fieldName) != null) {
                return true;
            }
        }
        return false;
    }

    protected boolean isFieldAnEntityOrSolution(Field field, Class fieldInstanceClass) {
        Class<?> type = field.getType();
        if (isClassDeepCloned(type)) {
            return true;
        }
        if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
            if (isTypeArgumentDeepCloned(field.getGenericType())) {
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

    private boolean isFieldADeepCloneProperty(Field field, Class fieldInstanceClass) {
        if (field.isAnnotationPresent(DeepPlanningClone.class)) {
            return true;
        }
        Method getterMethod = ReflectionHelper.getGetterMethod(fieldInstanceClass, field.getName());
        if (getterMethod != null && getterMethod.isAnnotationPresent(DeepPlanningClone.class)) {
            return true;
        }
        return false;
    }

    protected boolean retrieveDeepCloneDecisionForActualValueClass(Class<?> actualValueClass) {
        Boolean deepCloneDecision = deepCloneDecisionActualValueClassCache.get(actualValueClass);
        if (deepCloneDecision == null) {
            deepCloneDecision = isClassDeepCloned(actualValueClass);
            deepCloneDecisionActualValueClassCache.put(actualValueClass, deepCloneDecision);
        }
        return deepCloneDecision;
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
            unprocessedQueue = new ArrayDeque<Unprocessed>(entityCount + 1);
            originalToCloneMap = new IdentityHashMap<Object, Object>(
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
                return  existingClone;
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
                // TODO Upgrade to JDK 1.7: catch (ReflectiveOperationException e) instead of these 4
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("The class (" + clazz
                        + ") should have a no-arg constructor to create a clone.", e);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("The class (" + clazz
                        + ") should have a no-arg constructor to create a clone.", e);
            } catch (InstantiationException e) {
                throw new IllegalStateException("The class (" + clazz
                        + ") should have a no-arg constructor to create a clone.", e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("The class (" + clazz
                        + ") should have a no-arg constructor to create a clone.", e);
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

        protected boolean isDeepCloneField(Field field, Class fieldInstanceClass, Object originalValue) {
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
            } else {
                cloneValue = clone(unprocessed.originalValue);
            }
            setFieldValue(unprocessed.bean, unprocessed.field, cloneValue);
        }

        protected <E> Collection<E> cloneCollection(Class<?> expectedType, Collection<E> originalCollection) {
            Collection<E> cloneCollection = constructCloneCollection(originalCollection);
            if (!expectedType.isInstance(cloneCollection)) {
                throw new IllegalStateException("The cloneCollectionClass (" + cloneCollection.getClass()
                        + ") created for originalCollectionClass (" + originalCollection.getClass()
                        + ") is not assignable to the field's type (" + expectedType + ")."
                        + " Consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
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
                    return new ArrayList<E>(originalCollection.size());
                } else if (originalCollection instanceof LinkedList) {
                    return new LinkedList<E>();
                } else { // Default List
                    return new ArrayList<E>(originalCollection.size());
                }
            } else if (originalCollection instanceof Set) {
                if (originalCollection instanceof SortedSet) {
                    Comparator<E> setComparator = ((SortedSet) originalCollection).comparator();
                    return new TreeSet<E>(setComparator);
                } else if (originalCollection instanceof LinkedHashSet) {
                    return new LinkedHashSet<E>(originalCollection.size());
                } else if (originalCollection instanceof HashSet) {
                    return new HashSet<E>(originalCollection.size());
                } else { // Default Set
                    // Default to a LinkedHashSet to respect order
                    return new LinkedHashSet<E>(originalCollection.size());
                }
            } else if (originalCollection instanceof Deque) {
                return new ArrayDeque<E>(originalCollection.size());
            } else { // Default collection
                return new ArrayList<E>(originalCollection.size());
            }
        }

        protected <K, V> Map<K, V> cloneMap(Class<?> expectedType, Map<K, V> originalMap) {
            Map<K, V> cloneMap = constructCloneMap(originalMap);
            if (!expectedType.isInstance(cloneMap)) {
                throw new IllegalStateException("The cloneMapClass (" + cloneMap.getClass()
                        + ") created for originalMapClass (" + originalMap.getClass()
                        + ") is not assignable to the field's type (" + expectedType + ")."
                        + " Consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
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
                return new TreeMap<K, V>(setComparator);
            } else if (originalMap instanceof LinkedHashMap) {
                return new LinkedHashMap<K, V>(originalMap.size());
            } else if (originalMap instanceof HashMap) {
                return new HashMap<K, V>(originalMap.size());
            } else { // Default Map
                // Default to a LinkedHashMap to respect order
                return new LinkedHashMap<K, V>(originalMap.size());
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
            }
            if (retrieveDeepCloneDecisionForActualValueClass(original.getClass())) {
                return clone(original);
            } else {
                return original;
            }
        }

        /**
         * Fails fast if {@link #isFieldAnEntityPropertyOnSolution} assumptions were wrong.
         * @param originalSolution never null
         * @param cloneSolution never null
         */
        protected void validateCloneSolution(Solution_ originalSolution, Solution_ cloneSolution) {
            for (MemberAccessor memberAccessor
                    : solutionDescriptor.getEntityPropertyAccessorMap().values()) {
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
            for (MemberAccessor memberAccessor
                    : solutionDescriptor.getEntityCollectionPropertyAccessorMap().values()) {
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
                        + ") which can not be read to create a clone.", e);
            }
        }

        protected void setFieldValue(Object bean, Field field, Object value) {
            try {
                field.set(bean, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("The class (" + bean.getClass() + ") has a field (" + field
                        + ") which can not be written with the value (" + value + ") to create a clone.", e);
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
