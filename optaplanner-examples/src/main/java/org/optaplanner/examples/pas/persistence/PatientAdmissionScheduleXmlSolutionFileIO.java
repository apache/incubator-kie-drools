package org.optaplanner.examples.pas.persistence;

import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class PatientAdmissionScheduleXmlSolutionFileIO extends XStreamSolutionFileIO<PatientAdmissionSchedule> {

    public PatientAdmissionScheduleXmlSolutionFileIO() {
        super(PatientAdmissionSchedule.class);
    }
}
