/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        SolutionConverter<TspSolution> converter = SolutionConverter.createExportConverter(
                TspApp.DATA_DIR_NAME, TspSolution.class, new SvgTspLineAndCircleExporter());
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
