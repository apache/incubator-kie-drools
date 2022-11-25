package org.optaplanner.examples.pas.persistence;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;

public class PatientAdmissionScheduleSolutionFileIO extends AbstractJsonSolutionFileIO<PatientAdmissionSchedule> {

    public PatientAdmissionScheduleSolutionFileIO() {
        super(PatientAdmissionSchedule.class);
    }
}
