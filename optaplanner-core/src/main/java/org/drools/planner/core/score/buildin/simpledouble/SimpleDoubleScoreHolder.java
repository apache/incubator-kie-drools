/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.core.score.buildin.simpledouble;

import org.drools.common.AgendaItem;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.holder.AbstractScoreHolder;
import org.kie.event.rule.ActivationUnMatchListener;
import org.kie.runtime.rule.Match;
import org.kie.runtime.rule.RuleContext;
import org.kie.runtime.rule.Session;

public class SimpleDoubleScoreHolder extends AbstractScoreHolder {

    protected double score;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void addConstraintMatch(RuleContext kcontext, final double weight) {
        score += weight;
        registerUndoListener(kcontext, new ActivationUnMatchListener() {
            public void unMatch(Session workingMemory, Match activation) {
                score -= weight;
            }
        });
    }

    public Score extractScore() {
        return SimpleDoubleScore.valueOf(score);
    }

}
