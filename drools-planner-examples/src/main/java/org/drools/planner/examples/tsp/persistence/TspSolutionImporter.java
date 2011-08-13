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

package org.drools.planner.examples.tsp.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.drools.planner.examples.pas.domain.AdmissionPart;
import org.drools.planner.examples.pas.domain.BedDesignation;
import org.drools.planner.examples.tsp.domain.City;
import org.drools.planner.examples.tsp.domain.CityAssignment;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanTour;

public class TspSolutionImporter extends AbstractTxtSolutionImporter {

    private static final String INPUT_FILE_SUFFIX = ".tsp";

    public static void main(String[] args) {
        new TspSolutionImporter().convertAll();
    }

    public TspSolutionImporter() {
        super(new TspDaoImpl());
    }

    @Override
    protected String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new TravelingSalesmanTourInputBuilder();
    }

    public class TravelingSalesmanTourInputBuilder extends TxtInputBuilder {

        private TravelingSalesmanTour travelingSalesmanTour;

        private int cityListSize;

        public Solution readSolution() throws IOException {
            travelingSalesmanTour = new TravelingSalesmanTour();
            travelingSalesmanTour.setId(0L);
            readHeaders();
            readCityList();
            readConstantLine("EOF");
            createCityAssignmentList();
            logger.info("TravelingSalesmanTour with {} cities.",
                    travelingSalesmanTour.getCityList().size());
            BigInteger possibleSolutionSize = factorial(travelingSalesmanTour.getCityList().size() - 1);
            String flooredPossibleSolutionSize = "10^" + (possibleSolutionSize.toString().length() - 1);
            logger.info("TravelingSalesmanTour with flooredPossibleSolutionSize ({}) and possibleSolutionSize({}).",
                    flooredPossibleSolutionSize, possibleSolutionSize);
            return travelingSalesmanTour;
        }

        private void readHeaders() throws IOException {
            travelingSalesmanTour.setName(readStringValue("NAME :"));
            readUntilConstantLine("TYPE : TSP");
            cityListSize = readIntegerValue("DIMENSION :");
            String edgeWeightType = readStringValue("EDGE_WEIGHT_TYPE :");
            if (!edgeWeightType.equalsIgnoreCase("EUC_2D")) {
                // Only Euclidean distance is implemented in City.getDistance(City)
                throw new IllegalArgumentException("The edgeWeightType (" + edgeWeightType + ") is not supported.");
            }
        }

        private void readCityList() throws IOException {
            readConstantLine("NODE_COORD_SECTION");
            List<City> cityList = new ArrayList<City>(cityListSize);
            for (int i = 0; i < cityListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line, 3);
                City city = new City();
                city.setId(Long.parseLong(lineTokens[0]));
                city.setLatitude(Double.parseDouble(lineTokens[1]));
                city.setLongitude(Double.parseDouble(lineTokens[2]));
                cityList.add(city);
            }
            travelingSalesmanTour.setCityList(cityList);
            travelingSalesmanTour.setStartCity(cityList.get(0));
        }

        private void createCityAssignmentList() {
            List<City> cityList = travelingSalesmanTour.getCityList();
            List<CityAssignment> cityAssignmentList = new ArrayList<CityAssignment>(cityList.size());
            long id = 0L;
            for (City city : cityList) {
                CityAssignment cityAssignment = new CityAssignment();
                cityAssignment.setId(id);
                id++;
                cityAssignment.setCity(city);
                // Notice that we leave the PlanningVariable properties on null
                cityAssignmentList.add(cityAssignment);
                if (city.equals(travelingSalesmanTour.getStartCity())) {
                    travelingSalesmanTour.setStartCityAssignment(cityAssignment);
                }
            }
            travelingSalesmanTour.setCityAssignmentList(cityAssignmentList);
        }

        private BigInteger factorial(int base) {
            BigInteger value = BigInteger.ONE;
            for (int i = 1; i <= base; i++) {
                value = value.multiply(BigInteger.valueOf(base));
            }
            return value;
        }

    }

}
