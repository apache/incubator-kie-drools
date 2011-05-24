/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.bruteforce;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.planner.core.domain.PlanningVariable;
import org.drools.planner.core.domain.PlanningEntity;
import org.drools.runtime.rule.FactHandle;

public class PlanningEntityVariableIterator {

    private Object planningEntity;
    private final PlanningEntity planningEntityAnnotation;
    private Method valueMethod;

    private List<Object> planningValueList;
    private Iterator<Object> planningValueListIterator;

    public PlanningEntityVariableIterator(Object planningEntity, PlanningEntity planningEntityAnnotation,
            Collection<? extends Object> facts) {
        this.planningEntity = planningEntity;
        this.planningEntityAnnotation = planningEntityAnnotation;

        for (Method method : planningEntity.getClass().getMethods()) {
            PlanningVariable planningValueProperty = method.getAnnotation(PlanningVariable.class);
            if (planningValueProperty != null) {
                valueMethod = method;
                // TODO use when refactored to getter valueMethod.getReturnType();
                Class<?> planningValueClass = valueMethod.getParameterTypes()[0];
                planningValueList = new ArrayList<Object>();
                for (Object fact : facts) {
                    if (planningValueClass.isInstance(fact)) {
                        planningValueList.add(fact);
                    }
                }
                planningValueListIterator = planningValueList.iterator();
                break; // TODO for each method with that annotation
            }
        }
    }

    public boolean hasNext() {
        return planningValueListIterator.hasNext();
    }

    public void next(WorkingMemory workingMemory) {
        Object value = planningValueListIterator.next();
        doValue(workingMemory, value);
    }

    public void reset(WorkingMemory workingMemory) {
        planningValueListIterator = planningValueList.iterator();
        Object value = planningValueListIterator.next();
        doValue(workingMemory, value);
    }

    private void doValue(WorkingMemory workingMemory, Object value) {
        FactHandle factHandle = workingMemory.getFactHandle(planningEntity);
        try {
            valueMethod.invoke(planningEntity, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not call method to set planningValue", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Could not call method to set planningValue", e);
        }
        workingMemory.update(factHandle, planningEntity);
    }

}
