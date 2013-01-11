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

package org.drools.planner.core.domain.solution.cloner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.drools.planner.api.domain.solution.cloner.SolutionCloner;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.solution.Solution;

// TODO clean me up!
public class FieldAccessingSolutionCloner<SolutionG extends Solution> implements SolutionCloner<SolutionG> {

    protected SolutionDescriptor solutionDescriptor;

    public FieldAccessingSolutionCloner(SolutionDescriptor solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public SolutionG cloneSolution(SolutionG originalSolution) {
        return new FieldAccessingSolutionClonerRun().cloneSolution(originalSolution);
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

    protected class FieldAccessingSolutionClonerRun {

        protected Map<Object,Object> originalToCloneMap;
        protected Queue<Unprocessed> unprocessedQueue;

        protected SolutionG cloneSolution(SolutionG originalSolution) {
            unprocessedQueue = new LinkedList<Unprocessed>();
            originalToCloneMap = new IdentityHashMap<Object, Object>(
                    solutionDescriptor.getPlanningEntityCount(originalSolution) + 1);
            SolutionG cloneSolution = clone(originalSolution);
            processQueue();
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
                Constructor<C> constructor = clazz.getConstructor(); // TODO cache me
                constructor.setAccessible(true);
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
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // no need to reset because getDeclaredFields() creates new Field instances
                Object originalValue = getField(original, field);
                if (isDeepCloneField(field, originalValue)) {
                    // Postpone filling in the fields
                    unprocessedQueue.add(new Unprocessed(clone, field, originalValue));
                } else {
                    // Shallow copy
                    setField(clone, field, originalValue);
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
            Class<?> declaringClass = field.getDeclaringClass();
            if (declaringClass == ((Class) solutionDescriptor.getSolutionClass())) {
                String fieldName = field.getName();
                // This presumes we're dealing with a simple getter/setter. Dangerous?
                if (solutionDescriptor.getEntityPropertyAccessorMap().get(fieldName) != null) {
                    return true;
                }
                if (solutionDescriptor.getEntityCollectionPropertyAccessorMap().get(fieldName) != null) {
                    return true;
                }
            }
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

        private void process(Unprocessed unprocessed) {
            Object cloneValue;
            if (unprocessed.originalValue instanceof Collection) {
                cloneValue = cloneCollection((Collection) unprocessed.originalValue);
            } else if (unprocessed.originalValue instanceof Map) {
                cloneValue = cloneMap((Map) unprocessed.originalValue);
            } else {
                cloneValue = clone(unprocessed.originalValue);
            }
            setField(unprocessed.bean, unprocessed.field, cloneValue);
        }

        // TODO this is bad. It should follow hibernate limitations that we use an new empty ArrayList() for List, ...
        // TODO and detect things like a TreeSet's comparator too through SortedSet.getComparator()
        private Collection cloneCollection(Collection originalCollection) {
            Collection cloneCollection;
            if (!(originalCollection instanceof Cloneable)) {
                // TODO stopgap to make the unit tests work for now
                cloneCollection = new ArrayList(originalCollection.size());
//                throw new IllegalStateException("The collection (" + originalCollection
//                        + ") is an instance of a class (" + originalCollection.getClass()
//                        + ") that does not implement Cloneable.");
            } else {
                try {
                    Method cloneMethod = originalCollection.getClass().getMethod("clone");
                    cloneCollection = (Collection) cloneMethod.invoke(originalCollection);
                    // TODO Upgrade to JDK 1.7: catch (ReflectiveOperationException e) instead of these 4
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Could not call clone() on collection (" + originalCollection
                            + ") which is an instance of a class (" + originalCollection.getClass()
                            + ") and implements Cloneable.");
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("Could not call clone() on collection (" + originalCollection
                            + ") which is an instance of a class (" + originalCollection.getClass()
                            + ") and implements Cloneable.");
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not call clone() on collection (" + originalCollection
                            + ") which is an instance of a class (" + originalCollection.getClass()
                            + ") and implements Cloneable.");
                }
                cloneCollection.clear();
            }
            for (Object originalEntity : originalCollection) {
                Object cloneElement = clone(originalEntity);
                cloneCollection.add(cloneElement);
            }
            return cloneCollection;
        }

        private Map cloneMap(Map originalMap) {
            throw new UnsupportedOperationException(); // TODO
        }

        private Object getField(Object bean, Field field) {
            try {
                return field.get(bean);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("The class (" + bean.getClass()
                        + ") has a field (" + field
                        + ") which can not be read to create a clone.", e);
            }
        }

        private void setField(Object bean, Field field, Object value) {
            try {
                field.set(bean, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("The class (" + bean.getClass()
                        + ") has a field (" + field
                        + ") which can not be written to create a clone.", e);
            }
        }

    }

}
