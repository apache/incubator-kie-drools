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
import java.util.List;

import org.optaplanner.core.impl.score.director.drools.testgen.fact.TestGenFact;
import org.optaplanner.core.impl.score.director.drools.testgen.mutation.TestGenHeadCuttingMutator;
import org.optaplanner.core.impl.score.director.drools.testgen.mutation.TestGenRemoveRandomBlockMutator;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionInsert;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionOperation;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionUpdate;
import org.optaplanner.core.impl.score.director.drools.testgen.reproducer.TestGenOriginalProblemReproducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class TestGenerator {

    private static final Logger logger = LoggerFactory.getLogger(TestGenerator.class);
    private final TestGenOriginalProblemReproducer reproducer;
    private TestGenKieSessionJournal journal;

    static TestGenKieSessionJournal minimize(TestGenKieSessionJournal journal, TestGenOriginalProblemReproducer reproducer) {
        return new TestGenerator(journal, reproducer).run();
    }

    private TestGenerator(TestGenKieSessionJournal journal, TestGenOriginalProblemReproducer reproducer) {
        this.journal = journal;
        this.reproducer = reproducer;
    }

    private TestGenKieSessionJournal run() {
        logger.info("Creating a minimal test that reproduces following Drools problem: {}", reproducer);
        logger.info("The KIE session journal has {} facts, {} inserts and {} updates.",
                journal.getFacts().size(), journal.getInitialInserts().size(), journal.getMoveOperations().size());
        logger.info("Trying to reproduce with the complete KIE session journal...");
        assertOriginalExceptionReproduced("Cannot reproduce the original problem even without journal modifications. "
                + "This is a bug!");
        logger.info("Reproduced.");
        dropOldestUpdates();
        pruneUpdates();
        pruneInserts();
        pruneFacts();
        pruneSetup();
        pruneFacts();
        assertOriginalExceptionReproduced("Cannot reproduce the original problem after pruning the journal. "
                + "This is a bug!");
        return journal;
    }

    private void dropOldestUpdates() {
        logger.info("Dropping oldest {} updates...", journal.getMoveOperations().size());
        TestGenHeadCuttingMutator<TestGenKieSessionOperation> m = new TestGenHeadCuttingMutator<>(journal.getMoveOperations());
        while (m.canMutate()) {
            long start = System.currentTimeMillis();
            TestGenKieSessionJournal testJournal = new TestGenKieSessionJournal(journal.getFacts(), journal.getInitialInserts(), m.mutate());
            boolean reproduced = reproduce(testJournal);
            double tookSeconds = (System.currentTimeMillis() - start) / 1000d;
            String outcome = reproduced ? "Reproduced" : "Can't reproduce";
            logger.debug("    {} with journal size: {} (took {}s)", outcome, m.getResult().size(), tookSeconds);
            if (!reproduced) {
                m.revert();
            }
        }
        journal = new TestGenKieSessionJournal(journal.getFacts(), journal.getInitialInserts(), m.getResult());
        logger.info("{} updates remaining.", journal.getMoveOperations().size());
    }

    private void pruneUpdates() {
        logger.info("Pruning {} updates...", journal.getMoveOperations().size());
        TestGenRemoveRandomBlockMutator<TestGenKieSessionOperation> m = new TestGenRemoveRandomBlockMutator<>(journal.getMoveOperations());
        while (m.canMutate()) {
            logger.debug("    Current journal size: {}", m.getResult().size());
            TestGenKieSessionJournal testJournal = new TestGenKieSessionJournal(journal.getFacts(), journal.getInitialInserts(), m.mutate());
            boolean reproduced = reproduce(testJournal);
            String outcome = reproduced ? "Reproduced" : "Can't reproduce";
            List<TestGenKieSessionOperation> block = m.getRemovedBlock();
            logger.debug("    {} without block of {} [{} - {}]",
                    outcome, block.size(), block.get(0), block.get(block.size() - 1));
            if (!reproduced) {
                m.revert();
            }
        }
        journal = new TestGenKieSessionJournal(journal.getFacts(), journal.getInitialInserts(), m.getResult());
        logger.info("{} updates remaining.", journal.getMoveOperations().size());
    }

    private void pruneInserts() {
        logger.info("Pruning {} inserts...", journal.getInitialInserts().size());
        TestGenRemoveRandomBlockMutator<TestGenKieSessionInsert> m = new TestGenRemoveRandomBlockMutator<>(journal.getInitialInserts());
        while (m.canMutate()) {
            logger.debug("    Current journal size: {}", m.getResult().size());
            TestGenKieSessionJournal testJournal = new TestGenKieSessionJournal(journal.getFacts(), m.mutate(), journal.getMoveOperations());
            boolean reproduced = reproduce(testJournal);
            String outcome = reproduced ? "Reproduced" : "Can't reproduce";
            List<TestGenKieSessionInsert> block = m.getRemovedBlock();
            logger.debug("    {} without block of {} [{} - {}]",
                    outcome, block.size(), block.get(0), block.get(block.size() - 1));
            if (!reproduced) {
                m.revert();
            }
        }
        journal = new TestGenKieSessionJournal(journal.getFacts(), m.getResult(), journal.getMoveOperations());
        logger.info("{} inserts remaining.", journal.getInitialInserts().size());
    }

    private void pruneFacts() {
        logger.info("Pruning {} facts...", journal.getFacts().size());
        ArrayList<TestGenFact> minimal = new ArrayList<>();
        for (TestGenKieSessionInsert insert : journal.getInitialInserts()) {
            addWithDependencies(insert.getFact(), minimal);
        }
        for (TestGenKieSessionOperation op : journal.getMoveOperations()) {
            if (op.getClass().equals(TestGenKieSessionUpdate.class)) {
                TestGenFact f = ((TestGenKieSessionUpdate) op).getValue();
                addWithDependencies(f, minimal);
            }
        }
        journal.getFacts().retainAll(minimal);
        logger.info("{} facts remaining.", journal.getFacts().size());
    }

    private static void addWithDependencies(TestGenFact f, List<TestGenFact> factList) {
        if (factList.contains(f)) {
            return;
        }
        factList.add(f);
        for (TestGenFact dependency : f.getDependencies()) {
            addWithDependencies(dependency, factList);
        }
    }

    private void pruneSetup() {
        logger.info("Pruning {} facts setup code...", journal.getFacts().size());
        long disabled = journal.getFacts().stream()
                .flatMap(fact -> fact.getFields().stream()) // for all fields of all facts
                .filter(field -> {
                    field.setActive(false); // when disabled
                    if (reproduce(journal)) { // and the exception is still reproducible
                        return true; // count it
                    } else {
                        // otherwise reset to active (the field must be set in order to reproduce the exception)
                        field.setActive(true);
                        return false;
                    }
                }).count();
        logger.info("Disabled {} field setters.", disabled);
    }

    private boolean reproduce(TestGenKieSessionJournal testJournal) {
        return reproducer.isReproducible(testJournal);
    }

    private void assertOriginalExceptionReproduced(String message) {
        reproducer.assertReproducible(journal, message);
    }
}
