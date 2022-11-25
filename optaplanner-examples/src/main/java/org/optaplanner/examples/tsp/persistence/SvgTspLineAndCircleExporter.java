package org.optaplanner.examples.tsp.persistence;

import java.io.IOException;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.tsp.app.TspApp;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.location.Location;

public class SvgTspLineAndCircleExporter extends AbstractTxtSolutionExporter<TspSolution> {

    public static final String OUTPUT_FILE_SUFFIX = "line.svg";

    public static void main(String[] args) {
        SolutionConverter<TspSolution> converter = SolutionConverter.createExportConverter(TspApp.DATA_DIR_NAME,
                new SvgTspLineAndCircleExporter(), new TspSolutionFileIO());
        converter.convertAll();
    }

    @Override
    public String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    @Override
    public TxtOutputBuilder<TspSolution> createTxtOutputBuilder() {
        return new SvgTspOutputBuilder();
    }

    public static class SvgTspOutputBuilder extends AbstractSvgTspOutputBuilder {

        @Override
        public void writeSolution() throws IOException {

            determineSizeAndOffset(solution);

            writeSvgHeader();

            bufferedWriter.write("<g stroke='black' stroke-width='1'>\n");

            double oldLat = 0;
            double oldLong = 0;

            Standstill standstill = solution.getDomicile();
            Location home = standstill.getLocation();
            while (standstill != null) {
                bufferedWriter.write("  <line x1='" + (oldLat + offsetX) + "' y1='" + (height - (oldLong + offsetY)) + "' ");
                Location location = standstill.getLocation();
                bufferedWriter.write("x2='" + (location.getLongitude() + offsetX) + "' y2='"
                        + (height - (location.getLatitude() + offsetY)) + "' />\n");
                bufferedWriter.write("    <circle r='3' ");
                bufferedWriter.write("cx='" + (location.getLongitude() + offsetX) + "' cy='"
                        + (height - (location.getLatitude() + offsetY)) + "' />\n");
                oldLat = location.getLongitude();
                oldLong = location.getLatitude();
                standstill = findNextVisit(standstill);
            }
            bufferedWriter.write("  <line x1='" + (oldLat + offsetX) + "' y1='" + (height - (oldLong + offsetY)) + "' ");
            bufferedWriter
                    .write("x2='" + (home.getLongitude() + offsetX) + "' y2='" + (height - (home.getLatitude() + offsetY))
                            + "' />n");
            bufferedWriter.write("</g>\n");
            bufferedWriter.write("</svg>\n");
        }

    }

}
