package org.optaplanner.examples.tsp.persistence;

import java.io.IOException;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.Location;

/**
 * Common base for the SVG exporters with some helpers
 *
 * @author hrupp
 */
public abstract class AbstractSvgTspOutputBuilder extends AbstractTxtSolutionExporter.TxtOutputBuilder<TspSolution> {

    double width = 0;
    double height = 0;
    double offsetX = 0;
    double offsetY = 0;

    void writeSvgHeader() throws IOException {
        bufferedWriter.write("<?xml version='1.0'?>\n");
        bufferedWriter.write(
                "<svg xmlns='http://www.w3.org/2000/svg' version='1.2' baseProfile='tiny' \n");
        bufferedWriter.write("width='" + (width + offsetX) + "px' height='" + (height + offsetY) + "px'>\n");
    }

    void determineSizeAndOffset(TspSolution solution) {
        Standstill standstill = solution.getDomicile();
        while (standstill != null) {
            Location location = standstill.getLocation();
            if (location.getLongitude() > width) {
                width = location.getLongitude();
            }
            if (location.getLatitude() > height) {
                height = location.getLatitude();
            }
            if (location.getLongitude() < offsetX) {
                offsetX = location.getLongitude();
            }
            if (location.getLatitude() < offsetY) {
                offsetY = location.getLatitude();
            }
            standstill = findNextVisit(standstill);
        }
        // We provide a slightly bigger canvas
        height *= 1.05;
        width *= 1.05;
        offsetY = -offsetY;
        offsetX = -offsetX;
    }

    Visit findNextVisit(Standstill standstill) {
        for (Visit visit : solution.getVisitList()) {
            if (visit.getPreviousStandstill() == standstill) {
                return visit;
            }
        }
        return null;
    }

}
