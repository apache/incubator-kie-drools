/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.ConsequenceException;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.testgen.TestGenDroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.testgen.TestGenKieSessionJournal;
import org.optaplanner.core.impl.score.director.drools.testgen.TestGenKieSessionListener;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionFireAllRules;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Detects variable listener corruption. This condition is indicated by a difference between last working score and
 * the score calculated during {@link AbstractScoreDirector#assertShadowVariablesAreNotStale(Score, Object)}.
 */
public class TestGenCorruptedVariableListenerReproducer implements
        TestGenOriginalProblemReproducer,
        TestGenKieSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(TestGenCorruptedVariableListenerReproducer.class);
    private final String analysis;
    private final TestGenDroolsScoreDirector<?> scoreDirector;
    private Score<?> lastWorkingScore;
    private int lastFireId;

    public TestGenCorruptedVariableListenerReproducer(String analysis, TestGenDroolsScoreDirector<?> scoreDirector) {
        this.analysis = analysis;
        this.scoreDirector = scoreDirector;
    }

    private static Score<?> extractScore(KieSession kieSession) {
        AbstractScoreHolder sh = (AbstractScoreHolder) kieSession.getGlobal(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY);
        return sh.extractScore(0);
    }

    @Override
    public void assertReproducible(TestGenKieSessionJournal journal, String contextDescription) {
        if (!isReproducible(journal)) {
            throw new IllegalStateException(contextDescription + " Variable listeners are not corrupted.");
        }
    }

    @Override
    public boolean isReproducible(TestGenKieSessionJournal journal) {
        lastWorkingScore = null;
        lastFireId = Integer.MAX_VALUE;
        journal.addListener(this);
        try {
            journal.replay(scoreDirector.createKieSession());
            return false;
        } catch (TestGenCorruptedScoreException e) {
            return true;
        } catch (ConsequenceException e) {
            logger.debug("    Journal pruning not possible: {}", e.toString());
            return false;
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("No fact handle for ")) {
                // this is common when removing insert of a fact that is later updated - not interesting
                logger.debug("    Can't remove insert: {}", e.toString());
            } else if (e.getMessage() != null && e.getMessage().startsWith("Error evaluating constraint '")) {
                // this is common after pruning setup code, which can lead to NPE during rule evaluation
                logger.debug("    Can't drop field setup: {}", e.toString());
            } else {
                logger.info("Unexpected exception", e);
            }
            return false;
        }
    }

    @Override
    public void afterFireAllRules(KieSession kieSession, TestGenKieSessionJournal journal,
            TestGenKieSessionFireAllRules fire) {
        Score<?> workingScore = extractScore(kieSession);
        if (fire.isAssertFire()) {
            logger.debug("    [Assert mode] Score: working[{}], uncorrupted[{}] ({})",
                    workingScore, lastWorkingScore, fire);
            // if this assertion fire's score is different from the previous fire it means that a shadow variable
            // update was corrupted
            if (lastFireId == fire.getFireId() - 1
                    && !workingScore.equals(lastWorkingScore)) {
                throw new TestGenCorruptedScoreException(workingScore, lastWorkingScore);
            }
        } else {
            logger.debug("      Score: {} ({})", workingScore, fire);
        }
        lastWorkingScore = workingScore;
        lastFireId = fire.getFireId();
    }

    @Override
    public String toString() {
        return analysis;
    }

}
