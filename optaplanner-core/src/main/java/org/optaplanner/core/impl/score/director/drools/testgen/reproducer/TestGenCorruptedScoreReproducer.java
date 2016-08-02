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

import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.testgen.TestGenKieSessionJournal;
import org.optaplanner.core.impl.score.director.drools.testgen.TestGenKieSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestGenCorruptedScoreReproducer implements TestGenOriginalProblemReproducer, TestGenKieSessionListener {

    private static final Logger log = LoggerFactory.getLogger(TestGenCorruptedScoreReproducer.class);
    private final String analysis;
    private final KieSession originalKieSession;
    private final ScoreDefinition<?> scoreDefinition;
    private final boolean constraintMatchEnabledPreference;

    public TestGenCorruptedScoreReproducer(String analysis, KieSession originalKieSession, ScoreDefinition<?> scoreDefinition, boolean constraintMatchEnabledPreference) {
        this.analysis = analysis;
        this.originalKieSession = originalKieSession;
        this.scoreDefinition = scoreDefinition;
        this.constraintMatchEnabledPreference = constraintMatchEnabledPreference;
    }

    private static Score<?> extractScore(KieSession kieSession) {
        ScoreHolder sh = (ScoreHolder) kieSession.getGlobal(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY);
        return sh.extractScore(0);
    }

    private KieSession createKieSession() {
        KieSession newKieSession = originalKieSession.getKieBase().newKieSession();

        for (String globalKey : originalKieSession.getGlobals().getGlobalKeys()) {
            newKieSession.setGlobal(globalKey, originalKieSession.getGlobal(globalKey));
        }

        // set a fresh score holder
        if (scoreDefinition != null) {
            ScoreHolder sh = scoreDefinition.buildScoreHolder(constraintMatchEnabledPreference);
            newKieSession.setGlobal(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY, sh);
        }

        return newKieSession;
    }

    @Override
    public boolean isReproducible(TestGenKieSessionJournal journal) {
        journal.addListener(this);
        try {
            journal.replay(createKieSession());
            return false;
        } catch (TestGenCorruptedScoreException e) {
            return true;
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("No fact handle for ")) {
                // this is common when removing insert of a fact that is later updated - not interesting
                log.debug("    Can't remove insert: {}: {}", e.getClass().getSimpleName(), e.getMessage());
            } else {
                log.info("Unexpected exception", e);
            }
            return false;
        }
    }

    @Override
    public void assertReproducible(TestGenKieSessionJournal journal, String message) {
        if (!isReproducible(journal)) {
            throw new IllegalStateException(message + " The score is not corrupted.");
        }
    }

    @Override
    public void afterFireAllRules(KieSession kieSession) {
        KieSession uncorruptedSession = createKieSession();
        for (Object object : kieSession.getObjects()) {
            uncorruptedSession.insert(object);
        }
        uncorruptedSession.fireAllRules();
        uncorruptedSession.dispose();
        Score<?> uncorruptedScore = extractScore(uncorruptedSession);
        Score<?> workingScore = extractScore(kieSession);
        if (!workingScore.equals(uncorruptedScore)) {
            log.debug("    Score: working[{}], uncorrupted[{}]", workingScore, uncorruptedScore);
            throw new TestGenCorruptedScoreException("Working: " + workingScore + ", uncorrupted: "
                    + uncorruptedScore);
        }
    }

    @Override
    public String toString() {
        return analysis;
    }

}
