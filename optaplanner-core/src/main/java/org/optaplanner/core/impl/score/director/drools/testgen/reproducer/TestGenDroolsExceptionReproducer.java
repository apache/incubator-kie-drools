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
package org.optaplanner.core.impl.score.director.drools.testgen.reproducer;

import java.util.Objects;

import org.optaplanner.core.impl.score.director.drools.testgen.TestGenDroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.testgen.TestGenKieSessionJournal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reproduces the exception originally thrown by Drools during a call to KIE session.
 */
public class TestGenDroolsExceptionReproducer implements TestGenOriginalProblemReproducer {

    private static final Logger logger = LoggerFactory.getLogger(TestGenDroolsExceptionReproducer.class);
    private final RuntimeException originalException;
    private final TestGenDroolsScoreDirector<?> scoreDirector;

    public TestGenDroolsExceptionReproducer(RuntimeException originalException,
            TestGenDroolsScoreDirector<?> scoreDirector) {
        this.originalException = originalException;
        this.scoreDirector = scoreDirector;
    }

    @Override
    public boolean isReproducible(TestGenKieSessionJournal journal) {
        try {
            journal.replay(scoreDirector.createKieSession());
            return false;
        } catch (RuntimeException reproducedException) {
            if (areEqual(originalException, reproducedException)) {
                return true;
            } else {
                if (reproducedException.getMessage() != null
                        && reproducedException.getMessage().startsWith("No fact handle for ")) {
                    // this is common when removing insert of a fact that is later updated - not interesting
                    logger.debug("    Can't remove insert: {}", reproducedException.toString());
                } else if (reproducedException.getMessage() != null
                        && reproducedException.getMessage().startsWith("Error evaluating constraint '")) {
                    // this is common after pruning setup code, which can lead to NPE during rule evaluation
                    logger.debug("    Can't drop field setup: {}", reproducedException.toString());
                } else {
                    logger.info("Unexpected exception", reproducedException);
                }
                return false;
            }
        }
    }

    @Override
    public void assertReproducible(TestGenKieSessionJournal journal, String contextDescription) {
        try {
            journal.replay(scoreDirector.createKieSession());
            throw new IllegalStateException(contextDescription + " No exception thrown.");
        } catch (RuntimeException reproducedException) {
            if (!areEqual(originalException, reproducedException)) {
                throw new IllegalStateException(contextDescription
                        + "\nExpected [" + originalException + "]"
                        + "\nCaused [" + reproducedException + "]",
                        reproducedException);
            }
        }
    }

    private static boolean areEqual(RuntimeException originalException, RuntimeException reproducedException) {
        if (!originalException.getClass().equals(reproducedException.getClass())) {
            return false;
        }
        if (!Objects.equals(originalException.getMessage(), reproducedException.getMessage())) {
            return false;
        }
        if (reproducedException.getStackTrace().length == 0) {
            throw new IllegalStateException("Caught exception with empty stack trace => can't compare to the original. "
                    + "Use '-XX:-OmitStackTraceInFastThrow' to turn off this optimization.", reproducedException);
        }
        // TODO check all org.drools elements?
        return originalException.getStackTrace()[0].equals(reproducedException.getStackTrace()[0]);
    }

    @Override
    public String toString() {
        return originalException.toString();
    }
}
