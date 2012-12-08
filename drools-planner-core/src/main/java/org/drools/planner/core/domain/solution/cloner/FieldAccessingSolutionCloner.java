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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.drools.planner.api.domain.solution.cloner.SolutionCloner;
import org.drools.planner.core.domain.common.DescriptorUtils;
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

    public SolutionG cloneSolution(SolutionG original) {
        SolutionG clone = shallowClone(original);
        cloneEntityProperties(clone);

        return clone;
    }

    private <C> C shallowClone(C original) {
        Class<C> clazz = (Class<C>) original.getClass();
        C clone = constructClone(clazz);
        shallowCopyFields(clazz, original, clone);
        return clone;
    }

    private <C> C constructClone(Class<C> clazz) {
        try {
            Constructor<C> constructor = clazz.getConstructor(); // TODO move into SolutionDescriptor
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("The class (" + clazz
                    + ") should have a no-arg constructor to create a clone.", e);
        }
    }

    private <C> void shallowCopyFields(Class<C> clazz, C original, C clone) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // no need to reset because getDeclaredFields() creates new Field instances
            try {
                Object originalValue = field.get(original);
                field.set(clone, originalValue);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("The class (" + clazz
                        + ") has a field (" + field + ") which could not be accessed to create a clone.", e);
            }
        }
        Class<? super C> superclass = clazz.getSuperclass();
        if (superclass != null) {
            shallowCopyFields(superclass, original, clone);
        }
    }

    private void cloneEntityProperties(SolutionG clone) {
        for (PropertyDescriptor descriptor : solutionDescriptor.getEntityPropertyDescriptorMap().values()) {
            Object originalEntity = DescriptorUtils.executeGetter(descriptor, clone);
            Object cloneEntity = shallowClone(originalEntity);
            DescriptorUtils.executeSetter(descriptor, clone, cloneEntity);
        }
        for (PropertyDescriptor descriptor : solutionDescriptor.getEntityCollectionPropertyDescriptorMap().values()) {
            Collection originalEntityCollection = (Collection) DescriptorUtils.executeGetter(descriptor, clone);
            Collection cloneEntityCollection = shallowCloneEntityCollection(originalEntityCollection);
            cloneEntityCollection.clear();
            for (Object originalEntity : originalEntityCollection) {
                Object cloneEntity = shallowClone(originalEntity);
                cloneEntityCollection.add(cloneEntity);
            }
            DescriptorUtils.executeSetter(descriptor, clone, cloneEntityCollection);
        }
    }

    // TODO this is bad. It should follow hibernate limitations that we use an new empty ArrayList() for List, ...
    // TODO and detect things like a TreeSet's comparator too through SortedSet.getComparator()
    private Collection shallowCloneEntityCollection(Collection originalEntityCollection) {
        if (!(originalEntityCollection instanceof Cloneable)) {
            throw new IllegalStateException("The entityCollection (" + originalEntityCollection
                    + ") is an instance of a class (" + originalEntityCollection.getClass()
                    + ") that does not implement Cloneable.");
        }
        Method cloneMethod = null;
        try {
            cloneMethod = originalEntityCollection.getClass().getMethod("clone");
//            if (!Collection.class.isAssignableFrom(cloneMethod.getReturnType())) {
//                throw new IllegalStateException("The entityCollection (" + originalEntityCollection
//                        + ") is an instance of a class (" + originalEntityCollection.getClass()
//                        + ") that does not implement Cloneable.");
//            }
            return (Collection) cloneMethod.invoke(originalEntityCollection);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not call clone() on entityCollection (" + originalEntityCollection
                    + ") which is an instance of a class (" + originalEntityCollection.getClass()
                    + ") and implements Cloneable.");
        }
    }

}
