package org.optaplanner.examples.examination.persistence;

import org.optaplanner.examples.examination.domain.Examination;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class ExaminationXmlSolutionFileIO extends XStreamSolutionFileIO<Examination> {

    public ExaminationXmlSolutionFileIO() {
        super(Examination.class);
    }
}
