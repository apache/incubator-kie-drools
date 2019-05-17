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

import java.io.File;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.drools.testgen.reproducer.TestGenCorruptedScoreException;
import org.optaplanner.core.impl.score.director.drools.testgen.reproducer.TestGenCorruptedScoreReproducer;
import org.optaplanner.core.impl.score.director.drools.testgen.reproducer.TestGenCorruptedVariableListenerReproducer;
import org.optaplanner.core.impl.score.director.drools.testgen.reproducer.TestGenDroolsExceptionReproducer;

public class TestGenDroolsScoreDirector<Solution_> extends DroolsScoreDirector<Solution_> {

    private static final String TEST_CLASS_NAME = "DroolsReproducerTest";
    private final TestGenKieSessionJournal journal = new TestGenKieSessionJournal();
    private final File testFile = new File(TEST_CLASS_NAME + ".java");
    private final TestGenTestWriter writer = new TestGenTestWriter();
    private final Deque<String> oldValues = new ArrayDeque<>();

    public TestGenDroolsScoreDirector(
            DroolsScoreDirectorFactory<Solution_> scoreDirectorFactory,
            boolean lookUpEnabled,
            boolean constraintMatchEnabledPreference,
            List<String> scoreDrlList,
            List<File> scoreDrlFileList) {
        super(scoreDirectorFactory, lookUpEnabled, constraintMatchEnabledPreference);
        writer.setClassName(TEST_CLASS_NAME);
        writer.setScoreDefinition(scoreDirectorFactory.getScoreDefinition());
        writer.setConstraintMatchEnabled(constraintMatchEnabledPreference);
        writer.setScoreDrlList(scoreDrlList);
        writer.setScoreDrlFileList(scoreDrlFileList);
    }

    public KieSession createKieSession() {
        KieSession newKieSession = getScoreDirectorFactory().newKieSession();

        // set a fresh score holder
        ScoreDefinition<?> scoreDefinition = getScoreDefinition();
        if (scoreDefinition != null) {
            ScoreHolder sh = scoreDefinition.buildScoreHolder(constraintMatchEnabledPreference);
            newKieSession.setGlobal(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY, sh);
        }

        return newKieSession;
    }

    @Override
    public void setWorkingSolution(Solution_ workingSolution) {
        super.setWorkingSolution(workingSolution);
        journal.dispose();
        Collection<Object> workingFacts = getWorkingFacts();
        journal.addFacts(workingFacts);
        for (Object fact : workingFacts) {
            journal.insertInitial(fact);
        }
    }

    @Override
    public Score calculateScore() {
        journal.fireAllRules();
        try {
            return super.calculateScore();
        } catch (RuntimeException e) {
            // catch any Drools exception and create a minimal reproducing test
            // TODO check the exception is coming from org.drools
            TestGenDroolsExceptionReproducer reproducer = new TestGenDroolsExceptionReproducer(e, this);
            TestGenKieSessionJournal minJournal = TestGenerator.minimize(journal, reproducer);
            writer.print(minJournal, testFile);
            throw wrapOriginalException(e);
        }
    }

    @Override
    public void assertShadowVariablesAreNotStale(Score expectedWorkingScore, Object completedAction) {
        try {
            journal.enterAssertMode();
            super.assertShadowVariablesAreNotStale(expectedWorkingScore, completedAction);
            journal.exitAssertMode();
        } catch (IllegalStateException e) {
            // catch corrupted VariableListener exception and create a minimal reproducing test
            if (e.getMessage().startsWith("Impossible")) {
                TestGenCorruptedVariableListenerReproducer reproducer
                        = new TestGenCorruptedVariableListenerReproducer(e.getMessage(), this);
                // FIXME this is currently broken. The pruning needs to be smarter and not remove genuine variable
                // updates that directly affect shadow variables in the last (corrupted) variable listeners update.
                // If the genuine update is removed the shadow update obviously becomes inconsistent, which leads
                // to a false positive and the journal no longer reproduces the original issue. This is the current
                // state.
                TestGenKieSessionJournal minJournal = TestGenerator.minimize(journal, reproducer);
                try {
                    minJournal.replay(createKieSession());
                    throw new IllegalStateException();
                } catch (TestGenCorruptedScoreException tgcse) {
                    writer.setCorruptedScoreException(tgcse);
                }
                writer.print(minJournal, testFile);
                throw wrapOriginalException(e);
            } else {
                throw new UnsupportedOperationException("Stale shadow variable reproducer not implemented.", e);
            }
        }
    }

    @Override
    public void assertWorkingScoreFromScratch(Score workingScore, Object completedAction) {
        try {
            super.assertWorkingScoreFromScratch(workingScore, completedAction);
        } catch (IllegalStateException e) {
            // catch corrupted score exception and create a minimal reproducing test
            // TODO check it's really corrupted score
            TestGenCorruptedScoreReproducer reproducer = new TestGenCorruptedScoreReproducer(e.getMessage(), this);
            TestGenKieSessionJournal minJournal = TestGenerator.minimize(journal, reproducer);
            try {
                minJournal.replay(createKieSession());
                throw new IllegalStateException();
            } catch (TestGenCorruptedScoreException tgcse) {
                writer.setCorruptedScoreException(tgcse);
            }
            writer.print(minJournal, testFile);
            throw wrapOriginalException(e);
        }
    }

    @Override
    public Collection<ConstraintMatchTotal> getConstraintMatchTotals() {
        journal.fireAllRules();
        return super.getConstraintMatchTotals();
    }

    @Override
    public Map<String, ConstraintMatchTotal> getConstraintMatchTotalMap() {
        journal.fireAllRules();
        return super.getConstraintMatchTotalMap();
    }

    @Override
    public void close() {
        journal.dispose();
        super.close();
    }

    @Override
    public void afterEntityAdded(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        journal.insert(entity);
        super.afterEntityAdded(entityDescriptor, entity);
    }

    @Override
    public void beforeVariableChanged(VariableDescriptor variableDescriptor, Object entity) {
        if (logger.isTraceEnabled()) {
            Object oldValue = variableDescriptor.getValue(entity);
            if (oldValue == null) {
                // ArrayDeque doesn't allow null values
                oldValues.push("null");
            } else {
                oldValues.push(oldValue.toString());
            }
        }
        super.beforeVariableChanged(variableDescriptor, entity);
    }

    @Override
    public void afterVariableChanged(VariableDescriptor variableDescriptor, Object entity) {
        super.afterVariableChanged(variableDescriptor, entity);
        journal.update(entity, variableDescriptor);

        if (logger.isTraceEnabled()) {
            logger.trace("          Updating variable {}.{}[{}]: {} → {}",
                    entity,
                    variableDescriptor.getVariableName(),
                    variableDescriptor.getVariablePropertyType().getSimpleName(),
                    oldValues.pop(),
                    variableDescriptor.getValue(entity));
        }
    }

    @Override
    public void afterEntityRemoved(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        journal.delete(entity);
        super.afterEntityRemoved(entityDescriptor, entity);
    }

    @Override
    public void afterProblemFactAdded(Object problemFact) {
        journal.insert(problemFact);
        super.afterProblemFactAdded(problemFact);
    }

    // TODO override afterProblemFactChanged()?
    @Override
    public void afterProblemFactRemoved(Object problemFact) {
        journal.delete(problemFact);
        super.afterProblemFactRemoved(problemFact);
    }

    private RuntimeException wrapOriginalException(RuntimeException e) {
        return new RuntimeException(e.getMessage() + "\nDrools test written to: " + testFile.getAbsolutePath(), e);
    }

}
