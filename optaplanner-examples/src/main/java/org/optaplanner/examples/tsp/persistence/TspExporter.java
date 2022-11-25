package org.optaplanner.examples.tsp.persistence;

import java.io.IOException;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.tsp.app.TspApp;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;

public class TspExporter extends AbstractTxtSolutionExporter<TspSolution> {

    public static final String OUTPUT_FILE_SUFFIX = "tour";

    public static void main(String[] args) {
        SolutionConverter<TspSolution> converter =
                SolutionConverter.createExportConverter(TspApp.DATA_DIR_NAME, new TspExporter(), new TspSolutionFileIO());
        converter.convertAll();
    }

    @Override
    public String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    @Override
    public TxtOutputBuilder<TspSolution> createTxtOutputBuilder() {
        return new TspOutputBuilder();
    }

    public static class TspOutputBuilder extends TxtOutputBuilder<TspSolution> {

        @Override
        public void writeSolution() throws IOException {
            bufferedWriter.write("NAME : " + solution.getName() + "\n");
            bufferedWriter.write("TYPE : TOUR\n");
            bufferedWriter.write("DIMENSION : " + solution.getLocationList().size() + "\n");
            bufferedWriter.write("TOUR_SECTION\n");
            Standstill standstill = solution.getDomicile();
            while (standstill != null) {
                bufferedWriter.write(standstill.getLocation().getId() + "\n");
                standstill = findNextVisit(standstill);
            }
            bufferedWriter.write("EOF\n");
        }

        private Visit findNextVisit(Standstill standstill) {
            for (Visit visit : solution.getVisitList()) {
                if (visit.getPreviousStandstill() == standstill) {
                    return visit;
                }
            }
            return null;
        }
    }

}
