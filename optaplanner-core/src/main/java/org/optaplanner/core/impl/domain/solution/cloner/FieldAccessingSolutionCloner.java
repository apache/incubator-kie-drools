/*
 * Copyright 2012 JBoss Inc
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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

import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.common.PropertyAccessor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.solution.Solution;

public class FieldAccessingSolutionCloner<SolutionG extends Solution> implements SolutionCloner<SolutionG> {

    protected SolutionDescriptor solutionDescriptor;
    protected Map<Class, Constructor> constructorCache = new HashMap<Class, Constructor>();
    protected Map<Class, Field[]> fieldsCache = new HashMap<Class, Field[]>();

    public FieldAccessingSolutionCloner(SolutionDescriptor solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public SolutionG cloneSolution(SolutionG originalSolution) {
        return new FieldAccessingSolutionClonerRun().cloneSolution(originalSolution);
    }

    protected <C> Constructor<C> retrieveCachedConstructor(Class<C> clazz) throws NoSuchMethodException {
        Constructor<C> constructor = constructorCache.get(clazz);
        if (constructor == null) {
            constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            constructorCache.put(clazz, constructor);
        }
        return constructor;
    }

    protected <C> Field[] retrieveCachedFields(Class<C> clazz) {
        Field[]fields = fieldsCache.get(clazz);
        if (fields == null) {
            fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // no need to reset because getDeclaredFields() creates new Field instances
                field.setAccessible(true);
            }
            fieldsCache.put(clazz, fields);
        }
        return fields;
    }

    protected class FieldAccessingSolutionClonerRun {

        protected Map<Object,Object> originalToCloneMap;
        protected Queue<Unprocessed> unprocessedQueue;

        protected SolutionG cloneSolution(SolutionG originalSolution) {
            unprocessedQueue = new LinkedList<Unprocessed>();
            originalToCloneMap = new IdentityHashMap<Object, Object>(
                    solutionDescriptor.getPlanningEntityCount(originalSolution) + 1);
            SolutionG cloneSolution = clone(originalSolution);
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
            Class<C> clazz = (Class<C>) original.getClass();
            C clone = constructClone(clazz);
            originalToCloneMap.put(original, clone);
            copyFields(clazz, original, clone);
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

        protected <C> void copyFields(Class<C> clazz, C original, C clone) {
            for (Field field : retrieveCachedFields(clazz)) {
                Object originalValue = getFieldValue(original, field);
                if (isDeepCloneField(field, originalValue)) {
                    // Postpone filling in the fields
                    unprocessedQueue.add(new Unprocessed(clone, field, originalValue));
                } else {
                    // Shallow copy
                    setFieldValue(clone, field, originalValue);
                }
            }
            Class<? super C> superclass = clazz.getSuperclass();
            if (superclass != null) {
                copyFields(superclass, original, clone);
            }
        }

        protected boolean isDeepCloneField(Field field, Object originalValue) {
            if (originalValue == null) {
                return false;
            }
            if (isFieldAnEntityPropertyOnSolution(field)) {
                return true;
            }
            if (isValueAnEntity(originalValue)) {
                return true;
            }
            return false;
        }

        protected boolean isFieldAnEntityPropertyOnSolution(Field field) {
            Class<?> declaringClass = field.getDeclaringClass();
            if (declaringClass == ((Class) solutionDescriptor.getSolutionClass())) {
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

        protected boolean isValueAnEntity(Object originalValue) {
            Class valueClass = originalValue.getClass();
            if (solutionDescriptor.getPlanningEntityClassSet().contains(valueClass)
                    || valueClass == ((Class) solutionDescriptor.getSolutionClass())) {
                return true;
            }
            return false;
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
                cloneValue = cloneMap(unprocessed.field.getType(), (Map<?,?>) unprocessed.originalValue);
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
                E cloneElement = clone(originalElement);
                cloneCollection.add(cloneElement);
            }
            return cloneCollection;
        }

        protected <E> Collection<E> constructCloneCollection(Collection<E> originalCollection) {
            if (originalCollection instanceof List) {
                if (originalCollection instanceof ArrayList) {
                    return new ArrayList<E>(originalCollection.size());
                } else if (originalCollection instanceof LinkedList) {
                    return new LinkedList<E>();
                } else { // Default List
                    return new ArrayList<E>(originalCollection.size());
                }
            } if (originalCollection instanceof Set) {
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
            } else { // Default collection
                return new ArrayList<E>(originalCollection.size());
            }
        }

        protected <K,V> Map<K,V> cloneMap(Class<?> expectedType, Map<K,V> originalMap) {
            Map<K,V> cloneMap = constructCloneMap(originalMap);
            if (!expectedType.isInstance(cloneMap)) {
                throw new IllegalStateException("The cloneMapClass (" + cloneMap.getClass()
                        + ") created for originalMapClass (" + originalMap.getClass()
                        + ") is not assignable to the field's type (" + expectedType + ")."
                        + " Consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
            }
            for (Map.Entry<K,V> originalEntry : originalMap.entrySet()) {
                K cloneKey = clone(originalEntry.getKey());
                V cloneValue = clone(originalEntry.getValue());
                cloneMap.put(cloneKey, cloneValue);
            }
            return cloneMap;
        }

        protected <K,V> Map<K,V> constructCloneMap(Map<K,V> originalMap) {
            // Normally a Map will never be selected for cloning, but extending implementations might anyway
            if (originalMap instanceof SortedMap) {
                Comparator setComparator = ((SortedMap) originalMap).comparator();
                return new TreeMap<K,V>(setComparator);
            } else if (originalMap instanceof LinkedHashMap) {
                return new LinkedHashMap<K,V>(originalMap.size());
            } else if (originalMap instanceof HashMap) {
                return new HashMap<K,V>(originalMap.size());
            } else { // Default Map
                // Default to a LinkedHashMap to respect order
                return new LinkedHashMap<K,V>(originalMap.size());
            }
        }

        /**
         * Fails fast if {@link #isFieldAnEntityPropertyOnSolution} assumptions were wrong.
         * @param originalSolution never null
         * @param cloneSolution never null
         */
        protected void validateCloneSolution(SolutionG originalSolution, SolutionG cloneSolution) {
            for (PropertyAccessor propertyAccessor
                    : solutionDescriptor.getEntityPropertyAccessorMap().values()) {
                Object originalProperty = propertyAccessor.executeGetter(originalSolution);
                if (originalProperty != null) {
                    Object cloneProperty = propertyAccessor.executeGetter(cloneSolution);
                    if (originalProperty == cloneProperty) {
                        throw new IllegalStateException(
                                "The solutionProperty (" + propertyAccessor.getName() + ") was not cloned as expected."
                                + " The " + FieldAccessingSolutionCloner.class.getSimpleName() + " failed to recognize"
                                + " that property's field, probably because its field name is different.");
                    }
                }
            }
            for (PropertyAccessor propertyAccessor
                    : solutionDescriptor.getEntityCollectionPropertyAccessorMap().values()) {
                Object originalProperty = propertyAccessor.executeGetter(originalSolution);
                if (originalProperty != null) {
                    Object cloneProperty = propertyAccessor.executeGetter(cloneSolution);
                    if (originalProperty == cloneProperty) {
                        throw new IllegalStateException(
                                "The solutionProperty (" + propertyAccessor.getName() + ") was not cloned as expected."
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
                throw new IllegalStateException("The class (" + bean.getClass()
                        + ") has a field (" + field
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
