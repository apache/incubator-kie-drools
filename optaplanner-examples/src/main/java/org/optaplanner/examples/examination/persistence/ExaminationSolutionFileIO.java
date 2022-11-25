package org.optaplanner.examples.examination.persistence;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.examination.domain.Examination;

public class ExaminationSolutionFileIO extends AbstractJsonSolutionFileIO<Examination> {

    public ExaminationSolutionFileIO() {
        super(Examination.class);
    }
}
