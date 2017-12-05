/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.examples.tsp.domain.Visit;

public class TspExporter extends AbstractTxtSolutionExporter<TspSolution> {

    public static final String OUTPUT_FILE_SUFFIX = "tour";

    public static void main(String[] args) {
        SolutionConverter<TspSolution> converter = SolutionConverter.createExportConverter(
                TspApp.DATA_DIR_NAME, TspSolution.class, new TspExporter());
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
