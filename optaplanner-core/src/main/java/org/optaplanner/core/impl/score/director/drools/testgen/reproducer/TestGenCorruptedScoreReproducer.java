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
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.testgen.TestGenDroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.testgen.TestGenKieSessionJournal;
import org.optaplanner.core.impl.score.director.drools.testgen.TestGenKieSessionListener;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionFireAllRules;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionInsert;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Detects corrupted score for the given journal. It should behave equally to
 * {@link AbstractScoreDirector#assertWorkingScoreFromScratch(Score, Object)}.
 */
public class TestGenCorruptedScoreReproducer implements TestGenOriginalProblemReproducer, TestGenKieSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(TestGenCorruptedScoreReproducer.class);
    private final String analysis;
    private final TestGenDroolsScoreDirector<?> scoreDirector;

    public TestGenCorruptedScoreReproducer(String analysis, TestGenDroolsScoreDirector<?> scoreDirector) {
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
            throw new IllegalStateException(contextDescription + " The score is not corrupted.");
        }
    }

    @Override
    public boolean isReproducible(TestGenKieSessionJournal journal) {
        journal.addListener(this);
        try {
            journal.replay(scoreDirector.createKieSession());
            return false;
        } catch (TestGenCorruptedScoreException e) {
            return true;
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
        KieSession uncorruptedSession = scoreDirector.createKieSession();
        for (TestGenKieSessionInsert insert : journal.getInitialInserts()) {
            Object object = insert.getFact().getInstance();
            uncorruptedSession.insert(object);
        }
        uncorruptedSession.fireAllRules();
        uncorruptedSession.dispose();
        Score<?> uncorruptedScore = extractScore(uncorruptedSession);
        Score<?> workingScore = extractScore(kieSession);
        if (!workingScore.equals(uncorruptedScore)) {
            logger.debug("    Score: working[{}], uncorrupted[{}]", workingScore, uncorruptedScore);
            throw new TestGenCorruptedScoreException(workingScore, uncorruptedScore);
        }
    }

    @Override
    public String toString() {
        return analysis;
    }

}
