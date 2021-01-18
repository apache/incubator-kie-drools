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

public class SvgTspPathExporter extends AbstractTxtSolutionExporter<TspSolution> {

    public static final String OUTPUT_FILE_SUFFIX = "path.svg";

    public static void main(String[] args) {
        SolutionConverter<TspSolution> converter = SolutionConverter.createExportConverter(
                TspApp.DATA_DIR_NAME, TspSolution.class, new SvgTspPathExporter());
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
            bufferedWriter.write("<path style='stroke:#ff0000;stroke-width:1;fill:none'\n");
            bufferedWriter.write("d='\n");
            Standstill standstill = solution.getDomicile();
            Location home = standstill.getLocation();
            // Move to starting point
            bufferedWriter.write("M ");
            bufferedWriter.write((home.getLongitude() + offsetX) + ",");
            bufferedWriter.write((height - (home.getLatitude() + offsetY)) + "\n");

            while (standstill != null) {
                bufferedWriter.write("L ");
                Location location = standstill.getLocation();
                bufferedWriter.write((location.getLongitude() + offsetX) + ",");
                bufferedWriter.write((height - (location.getLatitude() + offsetY)) + "\n");
                standstill = findNextVisit(standstill);
            }
            // Now return home
            bufferedWriter.write("L ");
            bufferedWriter.write((home.getLongitude() + offsetX) + ",");
            bufferedWriter.write((height - (home.getLatitude() + offsetY)) + "\n");
            bufferedWriter.write("'/>");
            bufferedWriter.write("</svg>\n");
        }

    }

}
