/*
 * Copyright 2015 JBoss Inc
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractPngSolutionImporter;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.TravelingSalesmanTour;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.AirLocation;
import org.optaplanner.examples.tsp.domain.location.DistanceType;
import org.optaplanner.examples.tsp.domain.location.Location;
import org.optaplanner.examples.tsp.domain.location.RoadLocation;

public class TspImageStipplerImporter extends AbstractPngSolutionImporter {

    private static final int DITHERING_THRESHOLD = (256 * 3) / 2;

    public static void main(String[] args) {
        new TspImageStipplerImporter().convertAll();
    }

    public TspImageStipplerImporter() {
        super(new TspDao());
    }

    public TspImageStipplerImporter(boolean withoutDao) {
        super(withoutDao);
    }

    public PngInputBuilder createPngInputBuilder() {
        return new TspImageStipplerInputBuilder();
    }

    public static class TspImageStipplerInputBuilder extends PngInputBuilder {

        private TravelingSalesmanTour travelingSalesmanTour;

        private int locationListSize;

        public Solution readSolution() throws IOException {
            travelingSalesmanTour = new TravelingSalesmanTour();
            travelingSalesmanTour.setId(0L);
            travelingSalesmanTour.setName(FilenameUtils.getBaseName(inputFile.getName()));
            floydSteinbergDithering();
            createVisitList();
            BigInteger possibleSolutionSize = factorial(travelingSalesmanTour.getLocationList().size() - 1);
            logger.info("TravelingSalesmanTour {} has {} locations with a search space of {}.",
                    getInputId(),
                    travelingSalesmanTour.getLocationList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return travelingSalesmanTour;
        }

        /**
         * As described by https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering
         */
        private void floydSteinbergDithering() {
            travelingSalesmanTour.setDistanceType(DistanceType.AIR_DISTANCE);
            travelingSalesmanTour.setDistanceUnitOfMeasurement("distance");
            int width = image.getWidth();
            int height = image.getHeight();
            int[][] errorDiffusion = new int[width][height];
            List<Location> locationList = new ArrayList<Location>(1000);
            long id = 0L;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = (rgb >> 16) & 0xFF;
                    int grayStrength = r + g + b + errorDiffusion[x][y];
                    int error;
                    if (grayStrength >= DITHERING_THRESHOLD) {
                        Location location = new AirLocation();
                        location.setId(id);
                        id++;
                        location.setLatitude(- y);
                        location.setLongitude(x);
                        locationList.add(location);
                    } else {
                        error = grayStrength;
                    }
                    if (x + 1 < width) {
                        errorDiffusion[x + 1][y] += grayStrength * 7 / 16;
                    }
                    if (y + 1 < height) {
                        if (x - 1 >= 0) {
                            errorDiffusion[x - 1][y + 1] += grayStrength * 3 / 16;
                        }
                        errorDiffusion[x][y + 1] += grayStrength * 5 / 16;
                        if (x + 1 < width) {
                            errorDiffusion[x + 1][y + 1] += grayStrength * 1 / 16;
                        }
                    }
                }
            }
            travelingSalesmanTour.setLocationList(locationList);
        }

        private void createVisitList() {
            List<Location> locationList = travelingSalesmanTour.getLocationList();
            List<Visit> visitList = new ArrayList<Visit>(locationList.size() - 1);
            int count = 0;
            for (Location location : locationList) {
                if (count < 1) {
                    Domicile domicile = new Domicile();
                    domicile.setId(location.getId());
                    domicile.setLocation(location);
                    travelingSalesmanTour.setDomicile(domicile);
                } else {
                    Visit visit = new Visit();
                    visit.setId(location.getId());
                    visit.setLocation(location);
                    // Notice that we leave the PlanningVariable properties on null
                    visitList.add(visit);
                }
                count++;
            }
            travelingSalesmanTour.setVisitList(visitList);
        }

    }

}
