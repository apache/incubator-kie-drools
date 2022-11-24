package org.optaplanner.examples.examination.persistence;

import org.optaplanner.examples.common.persistence.jackson.AbstractExampleSolutionFileIO;
import org.optaplanner.examples.examination.domain.Examination;

public class ExaminationSolutionFileIO extends AbstractExampleSolutionFileIO<Examination> {

    public ExaminationSolutionFileIO() {
        super(Examination.class);
    }
}
