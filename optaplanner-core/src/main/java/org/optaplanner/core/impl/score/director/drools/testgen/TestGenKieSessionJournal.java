/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.director.drools.testgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.kie.api.runtime.KieSession;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.director.drools.testgen.fact.TestGenFact;
import org.optaplanner.core.impl.score.director.drools.testgen.fact.TestGenNullFact;
import org.optaplanner.core.impl.score.director.drools.testgen.fact.TestGenValueFact;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionDelete;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionFireAllRules;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionInsert;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionOperation;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionUpdate;

public class TestGenKieSessionJournal {

    private final TestGenKieSessionEventSupport eventSupport = new TestGenKieSessionEventSupport();
    private final HashMap<Object, TestGenFact> existingInstances = new HashMap<Object, TestGenFact>();
    private final List<TestGenFact> facts;
    private final List<TestGenKieSessionInsert> initialInsertJournal;
    private final List<TestGenKieSessionOperation> updateJournal;
    private int operationId = 0;

    public TestGenKieSessionJournal() {
        facts = new ArrayList<TestGenFact>();
        initialInsertJournal = new ArrayList<TestGenKieSessionInsert>();
        updateJournal = new ArrayList<TestGenKieSessionOperation>();
    }

    public TestGenKieSessionJournal(List<TestGenFact> facts, List<TestGenKieSessionInsert> initialInsertJournal, List<TestGenKieSessionOperation> updateJournal) {
        this.facts = facts;
        this.initialInsertJournal = initialInsertJournal;
        this.updateJournal = updateJournal;
    }

    public void replay(final KieSession replayKieSession) {
        // reset facts to the original state
        for (TestGenFact fact : facts) {
            fact.reset();
        }

        // insert facts into KIE session
        for (TestGenKieSessionOperation insert : initialInsertJournal) {
            insert.invoke(replayKieSession);
        }

        // replay KIE session operations
        try {
            for (TestGenKieSessionOperation op : updateJournal) {
                op.invoke(replayKieSession);
                // detect corrupted score after firing rules
                if (op.getClass().equals(TestGenKieSessionFireAllRules.class)) {
                    eventSupport.afterFireAllRules(replayKieSession);
                }
            }
        } catch (RuntimeException ex) {
            throw ex;
        } finally {
            replayKieSession.dispose();
        }
    }

    public void addFacts(Collection<Object> workingFacts) {
        int i = 0;
        for (Object instance : workingFacts) {
            TestGenFact fact = new TestGenValueFact(i++, instance);
            facts.add(fact);
            existingInstances.put(instance, fact);
        }

        for (TestGenFact fact : facts) {
            fact.setUp(existingInstances);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // KIE session operations recording
    //------------------------------------------------------------------------------------------------------------------
    //
    public void insertInitial(Object fact) {
        initialInsertJournal.add(new TestGenKieSessionInsert(operationId++, existingInstances.get(fact)));
    }

    public void insert(Object fact) {
        updateJournal.add(new TestGenKieSessionInsert(operationId++, existingInstances.get(fact)));
    }

    public void update(Object entity, VariableDescriptor<?> variableDescriptor) {
        TestGenFact entityFact = existingInstances.get(entity);
        Object value = variableDescriptor.getValue(entity);
        TestGenFact valueFact = value == null ? new TestGenNullFact() : existingInstances.get(value);
        updateJournal.add(new TestGenKieSessionUpdate(operationId++, entityFact, variableDescriptor, valueFact));
    }

    public void delete(Object entity) {
        updateJournal.add(new TestGenKieSessionDelete(operationId++, existingInstances.get(entity)));
    }

    public void fireAllRules() {
        updateJournal.add(new TestGenKieSessionFireAllRules(operationId++));
    }

    public void dispose() {
        facts.clear();
        existingInstances.clear();
        initialInsertJournal.clear();
        updateJournal.clear();
        operationId = 0;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Listeners
    //------------------------------------------------------------------------------------------------------------------
    //
    public void addListener(TestGenKieSessionListener listener) {
        eventSupport.addEventListener(listener);
    }

    public void removeListener(TestGenKieSessionListener listener) {
        eventSupport.removeEventListener(listener);
    }

    //------------------------------------------------------------------------------------------------------------------
    // Getters
    //------------------------------------------------------------------------------------------------------------------
    //
    public List<TestGenFact> getFacts() {
        return facts;
    }

    public List<TestGenKieSessionInsert> getInitialInserts() {
        return initialInsertJournal;
    }

    public List<TestGenKieSessionOperation> getMoveOperations() {
        return updateJournal;
    }

}
