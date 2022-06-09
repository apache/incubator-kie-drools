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
