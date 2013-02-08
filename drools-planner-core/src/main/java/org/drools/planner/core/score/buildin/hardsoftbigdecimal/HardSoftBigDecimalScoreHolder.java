/*
 * Copyright 2013 JBoss Inc
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

package org.drools.planner.core.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;

import org.drools.common.AgendaItem;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.holder.AbstractScoreHolder;
import org.kie.event.rule.ActivationUnMatchListener;
import org.kie.runtime.rule.Match;
import org.kie.runtime.rule.RuleContext;
import org.kie.runtime.rule.Session;

public class HardSoftBigDecimalScoreHolder extends AbstractScoreHolder {

    protected BigDecimal hardScore;
    protected BigDecimal softScore;

    public BigDecimal getHardScore() {
        return hardScore;
    }

    public void setHardScore(BigDecimal hardScore) {
        this.hardScore = hardScore;
    }

    public BigDecimal getSoftScore() {
        return softScore;
    }

    public void setSoftScore(BigDecimal softScore) {
        this.softScore = softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void addHardConstraintMatch(RuleContext kcontext, final BigDecimal weight) {
        hardScore = hardScore.add(weight);
        AgendaItem agendaItem = (AgendaItem) kcontext.getMatch();
        agendaItem.setActivationUnMatchListener(
                new ActivationUnMatchListener() {
                    public void unMatch(Session workingMemory, Match activation) {
                        hardScore = hardScore.add(weight);
                    }
                }
        );
    }

    public void addSoftConstraintMatch(RuleContext kcontext, final BigDecimal weight) {
        softScore = softScore.subtract(weight);
        AgendaItem agendaItem = (AgendaItem) kcontext.getMatch();
        agendaItem.setActivationUnMatchListener(
                new ActivationUnMatchListener() {
                    public void unMatch(Session workingMemory, Match activation) {
                        softScore = softScore.subtract(weight);
                    }
                }
        );
    }

    public Score extractScore() {
        return HardSoftBigDecimalScore.valueOf(hardScore, softScore);
    }

}
