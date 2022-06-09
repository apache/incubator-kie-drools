package org.optaplanner.examples.pas.solver.move;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;

public class PatientAdmissionMoveHelper {

    public static void moveBed(ScoreDirector<PatientAdmissionSchedule> scoreDirector, BedDesignation bedDesignation,
            Bed toBed) {
        scoreDirector.beforeVariableChanged(bedDesignation, "bed");
        bedDesignation.setBed(toBed);
        scoreDirector.afterVariableChanged(bedDesignation, "bed");
    }

    private PatientAdmissionMoveHelper() {
    }

}
