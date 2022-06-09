package org.optaplanner.examples.pas.solver.move;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;

public class BedChangeMove extends AbstractMove<PatientAdmissionSchedule> {

    private BedDesignation bedDesignation;
    private Bed toBed;

    public BedChangeMove(BedDesignation bedDesignation, Bed toBed) {
        this.bedDesignation = bedDesignation;
        this.toBed = toBed;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<PatientAdmissionSchedule> scoreDirector) {
        return !Objects.equals(bedDesignation.getBed(), toBed);
    }

    @Override
    public BedChangeMove createUndoMove(ScoreDirector<PatientAdmissionSchedule> scoreDirector) {
        return new BedChangeMove(bedDesignation, bedDesignation.getBed());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<PatientAdmissionSchedule> scoreDirector) {
        PatientAdmissionMoveHelper.moveBed(scoreDirector, bedDesignation, toBed);
    }

    @Override
    public BedChangeMove rebase(ScoreDirector<PatientAdmissionSchedule> destinationScoreDirector) {
        return new BedChangeMove(destinationScoreDirector.lookUpWorkingObject(bedDesignation),
                destinationScoreDirector.lookUpWorkingObject(toBed));
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(bedDesignation);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toBed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BedChangeMove other = (BedChangeMove) o;
        return Objects.equals(bedDesignation, other.bedDesignation) &&
                Objects.equals(toBed, other.toBed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bedDesignation, toBed);
    }

    @Override
    public String toString() {
        return bedDesignation + " {" + bedDesignation.getBed() + " -> " + toBed + "}";
    }

}
