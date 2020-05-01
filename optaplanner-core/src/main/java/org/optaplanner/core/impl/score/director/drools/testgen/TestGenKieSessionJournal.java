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
import org.optaplanner.core.impl.score.director.drools.testgen.fact.TestGenInlineValue;
import org.optaplanner.core.impl.score.director.drools.testgen.fact.TestGenNullFact;
import org.optaplanner.core.impl.score.director.drools.testgen.fact.TestGenValueFact;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionDelete;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionFireAllRules;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionInsert;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionOperation;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestGenKieSessionJournal {

    private static final Logger logger = LoggerFactory.getLogger(TestGenKieSessionJournal.class);
    private final TestGenKieSessionEventSupport eventSupport = new TestGenKieSessionEventSupport();
    private final HashMap<Object, TestGenFact> existingInstances = new HashMap<>();
    private final List<TestGenFact> facts;
    private final List<TestGenKieSessionInsert> initialInsertJournal;
    private final List<TestGenKieSessionOperation> updateJournal;
    private int operationId = 0;
    private boolean assertMode = false;

    public TestGenKieSessionJournal() {
        facts = new ArrayList<>();
        initialInsertJournal = new ArrayList<>();
        updateJournal = new ArrayList<>();
    }

    public TestGenKieSessionJournal(List<TestGenFact> facts, List<TestGenKieSessionInsert> initialInsertJournal,
            List<TestGenKieSessionOperation> updateJournal) {
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
                    eventSupport.afterFireAllRules(replayKieSession, this, (TestGenKieSessionFireAllRules) op);
                }
            }
        } finally {
            replayKieSession.dispose();
        }
    }

    public void addFacts(Collection<Object> workingFacts) {
        int i = 0;
        for (Object instance : workingFacts) {
            logger.trace("        Working fact added: {}[{}]", instance.getClass().getSimpleName(), instance);
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
        // TODO add to existing instances?
        updateJournal.add(new TestGenKieSessionInsert(operationId++, existingInstances.get(fact)));
    }

    public void update(Object entity, VariableDescriptor<?> variableDescriptor) {
        TestGenFact entityFact = existingInstances.get(entity);
        if (entityFact == null) {
            throw new IllegalStateException("The entity (" + entity.getClass().getSimpleName()
                    + "[" + entity + "]) is not a working fact");
        }
        Object value = variableDescriptor.getValue(entity);
        TestGenFact valueFact = value == null ? TestGenNullFact.INSTANCE : existingInstances.get(value);
        if (valueFact == null) {
            // shadow variable
            valueFact = new TestGenInlineValue(value, existingInstances);
        }
        updateJournal.add(new TestGenKieSessionUpdate(operationId++, entityFact, variableDescriptor, valueFact));
    }

    public void delete(Object fact) {
        // TODO check get(fact) is not null
        // TODO remove from existing instances?
        updateJournal.add(new TestGenKieSessionDelete(operationId++, existingInstances.get(fact)));
    }

    public void fireAllRules() {
        TestGenKieSessionFireAllRules fire = new TestGenKieSessionFireAllRules(operationId++, assertMode);
        logger.trace("        FIRE ALL RULES ({})", fire);
        updateJournal.add(fire);
    }

    public void dispose() {
        facts.clear();
        existingInstances.clear();
        initialInsertJournal.clear();
        updateJournal.clear();
        operationId = 0;
    }

    void enterAssertMode() {
        assertMode = true;
    }

    void exitAssertMode() {
        assertMode = false;
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
