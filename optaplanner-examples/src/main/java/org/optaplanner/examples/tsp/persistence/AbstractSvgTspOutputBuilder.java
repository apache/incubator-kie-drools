/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
