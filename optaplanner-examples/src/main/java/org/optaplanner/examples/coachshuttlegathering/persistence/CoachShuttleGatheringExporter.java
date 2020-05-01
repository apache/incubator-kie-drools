/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.coachshuttlegathering.persistence;

import java.io.IOException;

import org.optaplanner.examples.coachshuttlegathering.app.CoachShuttleGatheringApp;
import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.BusHub;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.Coach;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.domain.StopOrHub;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;

public class CoachShuttleGatheringExporter extends AbstractTxtSolutionExporter<CoachShuttleGatheringSolution> {

    public static final String OUTPUT_FILE_SUFFIX = "csv";

    public static void main(String[] args) {
        SolutionConverter<CoachShuttleGatheringSolution> converter = SolutionConverter.createExportConverter(
                CoachShuttleGatheringApp.DATA_DIR_NAME, CoachShuttleGatheringSolution.class,
                new CoachShuttleGatheringExporter());
        converter.convertAll();
    }

    @Override
    public String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    @Override
    public TxtOutputBuilder<CoachShuttleGatheringSolution> createTxtOutputBuilder() {
        return new CoachShuttleGatheringOutputBuilder();
    }

    public static class CoachShuttleGatheringOutputBuilder extends TxtOutputBuilder<CoachShuttleGatheringSolution> {

        @Override
        public void writeSolution() throws IOException {
            bufferedWriter.append("VEHICLE_ID;TOUR_POSITION;LOCATION_ID;LOCATION_TYPE\n");
            for (Bus bus : solution.getBusList()) {
                int i = 1;
                for (BusStop stop = bus.getNextStop(); stop != null; stop = stop.getNextStop()) {
                    bufferedWriter.append(bus.getName()).append(";")
                            .append(Integer.toString(i)).append(";")
                            .append(stop.getName()).append(";")
                            .append("BUSSTOP").append("\n");
                    i++;
                }
                if (i > 1 || bus instanceof Coach) {
                    StopOrHub destination = bus.getDestination();
                    bufferedWriter.append(bus.getName()).append(";")
                            .append(Integer.toString(i)).append(";")
                            .append(destination.getName()).append(";")
                            .append(destination instanceof BusHub ? "HUB" : "BUSSTOP").append("\n").append("\n");
                }
            }
        }

    }

}
