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

package org.drools.planner.core.score.buildin.hardandsoftlong;

import org.drools.common.AgendaItem;
import org.drools.event.rule.ActivationUnMatchListener;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.holder.AbstractScoreHolder;
import org.kie.runtime.rule.Activation;
import org.kie.runtime.rule.RuleContext;
import org.kie.runtime.rule.WorkingMemory;

public class HardAndSoftLongScoreHolder extends AbstractScoreHolder {

    protected long hardConstraintsBroken;
    protected long softConstraintsBroken;

    public long getHardConstraintsBroken() {
        return hardConstraintsBroken;
    }

    public void setHardConstraintsBroken(long hardConstraintsBroken) {
        this.hardConstraintsBroken = hardConstraintsBroken;
    }

    public long getSoftConstraintsBroken() {
        return softConstraintsBroken;
    }

    public void setSoftConstraintsBroken(long softConstraintsBroken) {
        this.softConstraintsBroken = softConstraintsBroken;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void addHardConstraintMatch(RuleContext kcontext, final long weight) {
        hardConstraintsBroken += - weight; // TODO remove minus and refactor hardConstraintsBroken
        AgendaItem agendaItem = (AgendaItem) kcontext.getActivation();
        agendaItem.setActivationUnMatchListener(
                new ActivationUnMatchListener() {
                    public void unMatch(WorkingMemory workingMemory, Activation activation) {
                        hardConstraintsBroken -= - weight; // TODO remove minus and refactor hardConstraintsBroken
                    }
                }
        );
    }

    public void addSoftConstraintMatch(RuleContext kcontext, final long weight) {
        softConstraintsBroken += - weight; // TODO remove minus and refactor softConstraintsBroken
        AgendaItem agendaItem = (AgendaItem) kcontext.getActivation();
        agendaItem.setActivationUnMatchListener(
                new ActivationUnMatchListener() {
                    public void unMatch(WorkingMemory workingMemory, Activation activation) {
                        softConstraintsBroken -= - weight; // TODO remove minus and refactor softConstraintsBroken
                    }
                }
        );
    }

    public Score extractScore() {
        return DefaultHardAndSoftLongScore.valueOf(-hardConstraintsBroken, -softConstraintsBroken);
    }

}
