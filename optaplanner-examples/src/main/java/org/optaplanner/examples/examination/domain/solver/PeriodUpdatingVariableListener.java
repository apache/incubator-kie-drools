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

package org.optaplanner.examples.examination.domain.solver;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.examination.domain.Examination;
import org.optaplanner.examples.examination.domain.FollowingExam;
import org.optaplanner.examples.examination.domain.LeadingExam;
import org.optaplanner.examples.examination.domain.Period;

public class PeriodUpdatingVariableListener implements VariableListener<Examination, LeadingExam> {

    @Override
    public void beforeEntityAdded(ScoreDirector<Examination> scoreDirector, LeadingExam leadingExam) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<Examination> scoreDirector, LeadingExam leadingExam) {
        updatePeriod(scoreDirector, leadingExam);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<Examination> scoreDirector, LeadingExam leadingExam) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<Examination> scoreDirector, LeadingExam leadingExam) {
        updatePeriod(scoreDirector, leadingExam);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Examination> scoreDirector, LeadingExam leadingExam) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Examination> scoreDirector, LeadingExam leadingExam) {
        // Do nothing
    }

    protected void updatePeriod(ScoreDirector<Examination> scoreDirector, LeadingExam leadingExam) {
        Period period = leadingExam.getPeriod();
        for (FollowingExam followingExam : leadingExam.getFollowingExamList()) {
            scoreDirector.beforeVariableChanged(followingExam, "period");
            followingExam.setPeriod(period);
            scoreDirector.afterVariableChanged(followingExam, "period");
        }
    }

}
