package org.optaplanner.examples.nurserostering.persistence;

import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class NurseRosterXmlSolutionFileIO extends XStreamSolutionFileIO<NurseRoster> {

    public NurseRosterXmlSolutionFileIO() {
        super(NurseRoster.class);
    }
}
