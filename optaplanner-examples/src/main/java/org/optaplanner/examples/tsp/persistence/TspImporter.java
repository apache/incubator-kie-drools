/*
 * Copyright 2011 JBoss Inc
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

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.TravelingSalesmanTour;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.AirLocation;
import org.optaplanner.examples.tsp.domain.location.Location;
import org.optaplanner.examples.tsp.domain.location.DistanceType;
import org.optaplanner.examples.tsp.domain.location.RoadLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.segmented.RoadSegmentLocation;

public class TspImporter extends AbstractTxtSolutionImporter {

    public static final String INPUT_FILE_SUFFIX = "tsp";

    public static void main(String[] args) {
        new TspImporter().convertAll();
    }

    public TspImporter() {
        super(new TspDao());
    }

    @Override
    public String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    @Override
    public boolean acceptInputFileDuringBulkConvert(File inputFile) {
        // Blacklist: too slow to write as XML
        return !Arrays.asList("ch71009.tsp").contains(inputFile.getName());
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new TspInputBuilder();
    }

    public static class TspInputBuilder extends TxtInputBuilder {

        private TravelingSalesmanTour travelingSalesmanTour;

        private int locationListSize;

        public Solution readSolution() throws IOException {
            travelingSalesmanTour = new TravelingSalesmanTour();
            travelingSalesmanTour.setId(0L);
            String firstLine = readStringValue();
            if (firstLine.matches("\\s*NAME\\s*:.*")) {
                travelingSalesmanTour.setName(removePrefixSuffixFromLine(firstLine, "\\s*NAME\\s*:", ""));
                readTspLibFormat();
            } else {
                travelingSalesmanTour.setName(FilenameUtils.getBaseName(inputFile.getName()));
                locationListSize = Integer.parseInt(firstLine.trim());
                readCourseraFormat();
            }
            BigInteger possibleSolutionSize = factorial(travelingSalesmanTour.getLocationList().size() - 1);
            logger.info("TravelingSalesmanTour {} has {} locations with a search space of {}.",
                    getInputId(),
                    travelingSalesmanTour.getLocationList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return travelingSalesmanTour;
        }

        // ************************************************************************
        // TSP TSPLIB format. See http://www.math.uwaterloo.ca/tsp/
        // ************************************************************************

        private void readTspLibFormat() throws IOException {
            readTspLibHeaders();
            readTspLibCityList();
            readConstantLine("EOF");
            createVisitList();
        }

        private void readTspLibHeaders() throws IOException {
            readUntilConstantLine("TYPE *: +TSP");
            locationListSize = readIntegerValue("DIMENSION *:");
            String edgeWeightType = readStringValue("EDGE_WEIGHT_TYPE *:");
            if (edgeWeightType.equalsIgnoreCase("EUC_2D")) {
                travelingSalesmanTour.setDistanceType(DistanceType.AIR_DISTANCE);
            } else if (edgeWeightType.equalsIgnoreCase("EXPLICIT")) {
                travelingSalesmanTour.setDistanceType(DistanceType.ROAD_DISTANCE);
                String edgeWeightFormat = readStringValue("EDGE_WEIGHT_FORMAT *:");
                if (!edgeWeightFormat.equalsIgnoreCase("FULL_MATRIX")) {
                    throw new IllegalArgumentException("The edgeWeightFormat (" + edgeWeightFormat + ") is not supported.");
                }
            } else {
                throw new IllegalArgumentException("The edgeWeightType (" + edgeWeightType + ") is not supported.");
            }
            travelingSalesmanTour.setDistanceUnitOfMeasurement(readOptionalStringValue("EDGE_WEIGHT_UNIT_OF_MEASUREMENT *:", "distance"));
        }

        private void readTspLibCityList() throws IOException {
            readConstantLine("NODE_COORD_SECTION");
            DistanceType distanceType = travelingSalesmanTour.getDistanceType();
            List<Location> locationList = new ArrayList<Location>(locationListSize);
            for (int i = 0; i < locationListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line, 3, 4);
                Location location;
                switch (distanceType) {
                    case AIR_DISTANCE:
                        location = new AirLocation();
                        break;
                    case ROAD_DISTANCE:
                        location = new RoadLocation();
                        break;
                    default:
                        throw new IllegalStateException("The distanceType (" + distanceType
                                + ") is not implemented.");

                }
                location.setId(Long.parseLong(lineTokens[0]));
                location.setLatitude(Double.parseDouble(lineTokens[1]));
                location.setLongitude(Double.parseDouble(lineTokens[2]));
                if (lineTokens.length >= 4) {
                    location.setName(lineTokens[3]);
                }
                locationList.add(location);
            }
            travelingSalesmanTour.setLocationList(locationList);
            if (distanceType == DistanceType.ROAD_DISTANCE) {
                readConstantLine("EDGE_WEIGHT_SECTION");
                for (int i = 0; i < locationListSize; i++) {
                    RoadLocation location = (RoadLocation) locationList.get(i);
                    Map<RoadLocation, Double> travelDistanceMap = new LinkedHashMap<RoadLocation, Double>(locationListSize);
                    String line = bufferedReader.readLine();
                    String[] lineTokens = splitBySpacesOrTabs(line.trim(), locationListSize);
                    for (int j = 0; j < locationListSize; j++) {
                        double travelDistance = Double.parseDouble(lineTokens[j]);
                        if (i == j) {
                            if (travelDistance != 0.0) {
                                throw new IllegalStateException("The travelDistance (" + travelDistance
                                        + ") should be zero.");
                            }
                        } else {
                            RoadLocation otherLocation = (RoadLocation) locationList.get(j);
                            travelDistanceMap.put(otherLocation, travelDistance);
                        }
                    }
                    location.setTravelDistanceMap(travelDistanceMap);
                }
            }
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

        // ************************************************************************
        // TSP coursera format. See https://class.coursera.org/optimization-001/
        // ************************************************************************

        private void readCourseraFormat() throws IOException {
            List<Location> locationList = new ArrayList<Location>(locationListSize);
            long id = 0;
            for (int i = 0; i < locationListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line, 2);
                Location location = new AirLocation();
                location.setId(id);
                id++;
                location.setLatitude(Double.parseDouble(lineTokens[0]));
                location.setLongitude(Double.parseDouble(lineTokens[1]));
                locationList.add(location);
            }
            travelingSalesmanTour.setLocationList(locationList);
            createVisitList();
        }

    }

}
