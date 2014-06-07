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
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.tsp.domain.City;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.TravelingSalesmanTour;
import org.optaplanner.examples.tsp.domain.Visit;

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

        private int cityListSize;

        public Solution readSolution() throws IOException {
            travelingSalesmanTour = new TravelingSalesmanTour();
            travelingSalesmanTour.setId(0L);
            String firstLine = readStringValue();
            if (firstLine.trim().startsWith("NAME :")) {
                travelingSalesmanTour.setName(removePrefixSuffixFromLine(firstLine, "NAME :", ""));
                readTspLibFormat();
            } else {
                travelingSalesmanTour.setName(FilenameUtils.getBaseName(inputFile.getName()));
                cityListSize = Integer.parseInt(firstLine.trim());
                readCourseraFormat();
            }
            BigInteger possibleSolutionSize = factorial(travelingSalesmanTour.getCityList().size() - 1);
            logger.info("TravelingSalesmanTour {} has {} cities with a search space of {}.",
                    getInputId(),
                    travelingSalesmanTour.getCityList().size(),
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
            readUntilConstantLine("TYPE : TSP");
            cityListSize = readIntegerValue("DIMENSION :");
            String edgeWeightType = readStringValue("EDGE_WEIGHT_TYPE :");
            if (!edgeWeightType.equalsIgnoreCase("EUC_2D")) {
                // Only Euclidean distance is implemented in City.getDistance(City)
                throw new IllegalArgumentException("The edgeWeightType (" + edgeWeightType + ") is not supported.");
            }
        }

        private void readTspLibCityList() throws IOException {
            readConstantLine("NODE_COORD_SECTION");
            List<City> cityList = new ArrayList<City>(cityListSize);
            for (int i = 0; i < cityListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line, 3, 4);
                City city = new City();
                city.setId(Long.parseLong(lineTokens[0]));
                city.setLatitude(Double.parseDouble(lineTokens[1]));
                city.setLongitude(Double.parseDouble(lineTokens[2]));
                if (lineTokens.length >= 4) {
                    city.setName(lineTokens[3]);
                }
                cityList.add(city);
            }
            travelingSalesmanTour.setCityList(cityList);
        }

        private void createVisitList() {
            List<City> cityList = travelingSalesmanTour.getCityList();
            List<Visit> visitList = new ArrayList<Visit>(cityList.size() - 1);
            int count = 0;
            for (City city : cityList) {
                if (count < 1) {
                    Domicile domicile = new Domicile();
                    domicile.setId(city.getId());
                    domicile.setCity(city);
                    travelingSalesmanTour.setDomicile(domicile);
                } else {
                    Visit visit = new Visit();
                    visit.setId(city.getId());
                    visit.setCity(city);
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
            List<City> cityList = new ArrayList<City>(cityListSize);
            long id = 0;
            for (int i = 0; i < cityListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line, 2);
                City city = new City();
                city.setId(id);
                id++;
                city.setLatitude(Double.parseDouble(lineTokens[0]));
                city.setLongitude(Double.parseDouble(lineTokens[1]));
                cityList.add(city);
            }
            travelingSalesmanTour.setCityList(cityList);
            createVisitList();
        }

    }

}
