package org.optaplanner.examples.pas.persistence;

import org.optaplanner.examples.common.persistence.jackson.AbstractExampleSolutionFileIO;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;

public class PatientAdmissionScheduleSolutionFileIO extends AbstractExampleSolutionFileIO<PatientAdmissionSchedule> {

    public PatientAdmissionScheduleSolutionFileIO() {
        super(PatientAdmissionSchedule.class);
    }
}
