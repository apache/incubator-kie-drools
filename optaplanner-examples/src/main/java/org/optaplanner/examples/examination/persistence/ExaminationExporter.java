package org.optaplanner.examples.examination.persistence;

import java.io.IOException;
import java.util.Comparator;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.examination.app.ExaminationApp;
import org.optaplanner.examples.examination.domain.Exam;
import org.optaplanner.examples.examination.domain.Examination;

public class ExaminationExporter extends AbstractTxtSolutionExporter<Examination> {

    private static final String OUTPUT_FILE_SUFFIX = "sln";

    public static void main(String[] args) {
        SolutionConverter<Examination> converter = SolutionConverter.createExportConverter(
                ExaminationApp.DATA_DIR_NAME, new ExaminationExporter(), new ExaminationSolutionFileIO());
        converter.convertAll();
    }

    @Override
    public String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    @Override
    public TxtOutputBuilder<Examination> createTxtOutputBuilder() {
        return new ExaminationOutputBuilder();
    }

    public static class ExaminationOutputBuilder extends TxtOutputBuilder<Examination> {

        @Override
        public void writeSolution() throws IOException {
            solution.getExamList().sort(Comparator.comparingLong(Exam::getId)); // TODO remove me when obsolete
            for (Exam exam : solution.getExamList()) {
                bufferedWriter.write(exam.getPeriod().getId() + ", " + exam.getRoom().getId() + "\r\n");
            }
        }

    }

}
