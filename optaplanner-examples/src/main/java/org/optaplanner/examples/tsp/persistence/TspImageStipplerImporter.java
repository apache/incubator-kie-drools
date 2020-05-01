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

package org.optaplanner.examples.tsp.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.optaplanner.examples.common.persistence.AbstractPngSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.tsp.app.TspApp;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.AirLocation;
import org.optaplanner.examples.tsp.domain.location.DistanceType;
import org.optaplanner.examples.tsp.domain.location.Location;

public class TspImageStipplerImporter extends AbstractPngSolutionImporter<TspSolution> {

    private static final double GRAY_MAXIMUM = 256.0 * 3.0;

    public static void main(String[] args) {
        SolutionConverter<TspSolution> converter = SolutionConverter.createImportConverter(
                TspApp.DATA_DIR_NAME, new TspImageStipplerImporter(), TspSolution.class);
        converter.convertAll();
    }

    @Override
    public PngInputBuilder<TspSolution> createPngInputBuilder() {
        return new TspImageStipplerInputBuilder();
    }

    public static class TspImageStipplerInputBuilder extends PngInputBuilder<TspSolution> {

        private TspSolution tspSolution;

        private int locationListSize;

        @Override
        public TspSolution readSolution() throws IOException {
            tspSolution = new TspSolution();
            tspSolution.setId(0L);
            tspSolution.setName(FilenameUtils.getBaseName(inputFile.getName()));
            floydSteinbergDithering();
            createVisitList();
            BigInteger possibleSolutionSize = factorial(tspSolution.getLocationList().size() - 1);
            logger.info("TspSolution {} has {} locations with a search space of {}.",
                    getInputId(),
                    tspSolution.getLocationList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return tspSolution;
        }

        /**
         * As described by <a href="https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering">Floyd-Steinberg
         * dithering</a>.
         */
        private void floydSteinbergDithering() {
            tspSolution.setDistanceType(DistanceType.AIR_DISTANCE);
            tspSolution.setDistanceUnitOfMeasurement("distance");
            int width = image.getWidth();
            int height = image.getHeight();
            double[][] errorDiffusion = new double[width][height];
            List<Location> locationList = new ArrayList<>(1000);
            long id = 0L;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = (rgb >> 16) & 0xFF;
                    double originalGray = (r + g + b) / GRAY_MAXIMUM;
                    double diffusedGray = originalGray + errorDiffusion[x][y];
                    double error;
                    if (diffusedGray <= 0.5) {
                        Location location = new AirLocation();
                        location.setId(id);
                        id++;
                        location.setLatitude(-y);
                        location.setLongitude(x);
                        locationList.add(location);
                        error = diffusedGray;
                    } else {
                        error = diffusedGray - 1.0;
                    }
                    if (x + 1 < width) {
                        errorDiffusion[x + 1][y] += error * 7.0 / 16.0;
                    }
                    if (y + 1 < height) {
                        if (x - 1 >= 0) {
                            errorDiffusion[x - 1][y + 1] += error * 3.0 / 16.0;
                        }
                        errorDiffusion[x][y + 1] += error * 5.0 / 16.0;
                        if (x + 1 < width) {
                            errorDiffusion[x + 1][y + 1] += error * 1.0 / 16.0;
                        }
                    }
                }
            }
            tspSolution.setLocationList(locationList);
        }

        private void createVisitList() {
            List<Location> locationList = tspSolution.getLocationList();
            List<Visit> visitList = new ArrayList<>(locationList.size() - 1);
            int count = 0;
            for (Location location : locationList) {
                if (count < 1) {
                    Domicile domicile = new Domicile();
                    domicile.setId(location.getId());
                    domicile.setLocation(location);
                    tspSolution.setDomicile(domicile);
                } else {
                    Visit visit = new Visit();
                    visit.setId(location.getId());
                    visit.setLocation(location);
                    // Notice that we leave the PlanningVariable properties on null
                    visitList.add(visit);
                }
                count++;
            }
            tspSolution.setVisitList(visitList);
        }

    }

}
