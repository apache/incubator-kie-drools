/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.scrabble.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.scrabble.domain.ScrabbleCell;
import org.optaplanner.examples.scrabble.domain.ScrabbleSolution;
import org.optaplanner.examples.scrabble.domain.ScrabbleWord;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.AirLocation;
import org.optaplanner.examples.tsp.domain.location.DistanceType;
import org.optaplanner.examples.tsp.domain.location.Location;
import org.optaplanner.examples.tsp.domain.location.RoadLocation;

public class ScrabbleImporter extends AbstractTxtSolutionImporter<ScrabbleSolution> {

    public static void main(String[] args) {
        ScrabbleImporter importer = new ScrabbleImporter();
        importer.convert("jbossProjects.txt", "jbossProjects.xml");
    }

    public ScrabbleImporter() {
        super(new ScrabbleDao());
    }

    public ScrabbleImporter(boolean withoutDao) {
        super(withoutDao);
    }

    @Override
    public TxtInputBuilder<ScrabbleSolution> createTxtInputBuilder() {
        return new ScrabbleInputBuilder();
    }

    public static class ScrabbleInputBuilder extends TxtInputBuilder<ScrabbleSolution> {

        private ScrabbleSolution solution;
        private Locale locale;

        @Override
        public ScrabbleSolution readSolution() throws IOException {
            solution = new ScrabbleSolution();
            solution.setId(0L);
            locale = Locale.forLanguageTag(readStringValue("locale:"));
            readCellList();
            readWordList();
            BigInteger possibleSolutionSize = BigInteger.valueOf(solution.getCellList().size()).pow(
                    solution.getWordList().size());
            logger.info("ScrabbleSolution {} has {} words and {} cells with a search space of {}.",
                    getInputId(),
                    solution.getWordList().size(),
                    solution.getCellList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return solution;
        }

        private void readCellList() throws IOException {
            int gridWidth = readIntegerValue("gridWidth:");
            solution.setGridWidth(gridWidth);
            int gridHeight = readIntegerValue("gridHeight:");
            solution.setGridHeight(gridHeight);
            List<ScrabbleCell> cellList = new ArrayList<>(gridWidth * gridHeight);
            for (int x = 0; x < gridWidth; x++) {
                for (int y = 0; y < gridHeight; y++) {
                    ScrabbleCell cell = new ScrabbleCell();
                    cell.setId((long) (y * gridWidth + x));
                    cell.setX(x);
                    cell.setY(y);
                    cell.setWordSet(new LinkedHashSet<>());
                    cellList.add(cell);

                }
            }
            solution.setCellList(cellList);
        }

        private void readWordList() throws IOException {
            readConstantLine("# Words");
            List<ScrabbleWord> wordList = new ArrayList<>();
            String line = bufferedReader.readLine();
            long wordId = 0L;
            while (line != null) {
                String wordString = readStringValue();
                if (!wordString.isEmpty()) {
                    ScrabbleWord word = new ScrabbleWord();
                    word.setId(wordId);
                    wordId++;
                    word.setWord(wordString.toUpperCase(locale));
                    wordList.add(word);
                }
                line = bufferedReader.readLine();
            }
            solution.setWordList(wordList);
        }
//
//        // ************************************************************************
//        // TSP TSPLIB format. See http://www.math.uwaterloo.ca/tsp/
//        // ************************************************************************
//
//        private void readTspLibFormat() throws IOException {
//            readTspLibHeaders();
//            readTspLibCityList();
//            createVisitList();
//            readTspLibSolution();
//            readConstantLine("EOF");
//        }
//
//        private void readTspLibHeaders() throws IOException {
//            readUntilConstantLine("TYPE *: +TSP");
//            locationListSize = readIntegerValue("DIMENSION *:");
//            String edgeWeightType = readStringValue("EDGE_WEIGHT_TYPE *:");
//            if (edgeWeightType.equalsIgnoreCase("EUC_2D")) {
//                solution.setDistanceType(DistanceType.AIR_DISTANCE);
//            } else if (edgeWeightType.equalsIgnoreCase("EXPLICIT")) {
//                solution.setDistanceType(DistanceType.ROAD_DISTANCE);
//                String edgeWeightFormat = readStringValue("EDGE_WEIGHT_FORMAT *:");
//                if (!edgeWeightFormat.equalsIgnoreCase("FULL_MATRIX")) {
//                    throw new IllegalArgumentException("The edgeWeightFormat (" + edgeWeightFormat + ") is not supported.");
//                }
//            } else {
//                throw new IllegalArgumentException("The edgeWeightType (" + edgeWeightType + ") is not supported.");
//            }
//            solution.setDistanceUnitOfMeasurement(readOptionalStringValue("EDGE_WEIGHT_UNIT_OF_MEASUREMENT *:", "distance"));
//        }
//
//        private void readTspLibCityList() throws IOException {
//            readConstantLine("NODE_COORD_SECTION");
//            DistanceType distanceType = solution.getDistanceType();
//            List<Location> locationList = new ArrayList<>(locationListSize);
//            for (int i = 0; i < locationListSize; i++) {
//                String line = bufferedReader.readLine();
//                String[] lineTokens = splitBySpace(line, 3, 4, false, true);
//                Location location;
//                switch (distanceType) {
//                    case AIR_DISTANCE:
//                        location = new AirLocation();
//                        break;
//                    case ROAD_DISTANCE:
//                        location = new RoadLocation();
//                        break;
//                    default:
//                        throw new IllegalStateException("The distanceType (" + distanceType
//                                + ") is not implemented.");
//
//                }
//                location.setId(Long.parseLong(lineTokens[0]));
//                location.setLatitude(Double.parseDouble(lineTokens[1]));
//                location.setLongitude(Double.parseDouble(lineTokens[2]));
//                if (lineTokens.length >= 4) {
//                    location.setName(lineTokens[3]);
//                }
//                locationList.add(location);
//            }
//            solution.setLocationList(locationList);
//            if (distanceType == DistanceType.ROAD_DISTANCE) {
//                readConstantLine("EDGE_WEIGHT_SECTION");
//                for (int i = 0; i < locationListSize; i++) {
//                    RoadLocation location = (RoadLocation) locationList.get(i);
//                    Map<RoadLocation, Double> travelDistanceMap = new LinkedHashMap<>(locationListSize);
//                    String line = bufferedReader.readLine();
//                    String[] lineTokens = splitBySpacesOrTabs(line.trim(), locationListSize);
//                    for (int j = 0; j < locationListSize; j++) {
//                        double travelDistance = Double.parseDouble(lineTokens[j]);
//                        if (i == j) {
//                            if (travelDistance != 0.0) {
//                                throw new IllegalStateException("The travelDistance (" + travelDistance
//                                        + ") should be zero.");
//                            }
//                        } else {
//                            RoadLocation otherLocation = (RoadLocation) locationList.get(j);
//                            travelDistanceMap.put(otherLocation, travelDistance);
//                        }
//                    }
//                    location.setTravelDistanceMap(travelDistanceMap);
//                }
//            }
//        }
//
//        private void createVisitList() {
//            List<Location> locationList = solution.getLocationList();
//            List<Visit> visitList = new ArrayList<>(locationList.size() - 1);
//            int count = 0;
//            for (Location location : locationList) {
//                if (count < 1) {
//                    Domicile domicile = new Domicile();
//                    domicile.setId(location.getId());
//                    domicile.setLocation(location);
//                    solution.setDomicile(domicile);
//                } else {
//                    Visit visit = new Visit();
//                    visit.setId(location.getId());
//                    visit.setLocation(location);
//                    // Notice that we leave the PlanningVariable properties on null
//                    visitList.add(visit);
//                }
//                count++;
//            }
//            solution.setVisitList(visitList);
//        }
//
//        private void readTspLibSolution() throws IOException {
//            boolean enabled = readOptionalConstantLine("TOUR_SECTION");
//            if (!enabled) {
//                return;
//            }
//            long domicileId = readLongValue();
//            Domicile domicile = solution.getDomicile();
//            if (!domicile.getId().equals(domicileId)) {
//                throw new IllegalStateException("The domicileId (" + domicileId
//                        + ") is not the domicile's id (" + domicile.getId() + ").");
//            }
//            int visitListSize = solution.getVisitList().size();
//            Map<Long, Visit> idToVisitMap = new HashMap<>(visitListSize);
//            for (Visit visit : solution.getVisitList()) {
//                idToVisitMap.put(visit.getId(), visit);
//            }
//            Standstill previousStandstill = domicile;
//            for (int i = 0; i < visitListSize; i++) {
//                long visitId = readLongValue();
//                Visit visit = idToVisitMap.get(visitId);
//                if (visit == null) {
//                    throw new IllegalStateException("The visitId (" + visitId
//                            + ") is does not exist.");
//                }
//                visit.setPreviousStandstill(previousStandstill);
//                previousStandstill = visit;
//            }
//        }
//
//        // ************************************************************************
//        // TSP coursera format. See https://class.coursera.org/optimization-001/
//        // ************************************************************************
//
//        private void readCourseraFormat() throws IOException {
//            List<Location> locationList = new ArrayList<>(locationListSize);
//            long id = 0;
//            for (int i = 0; i < locationListSize; i++) {
//                String line = bufferedReader.readLine();
//                String[] lineTokens = splitBySpace(line, 2);
//                Location location = new AirLocation();
//                location.setId(id);
//                id++;
//                location.setLatitude(Double.parseDouble(lineTokens[0]));
//                location.setLongitude(Double.parseDouble(lineTokens[1]));
//                locationList.add(location);
//            }
//            solution.setLocationList(locationList);
//            createVisitList();
//        }

    }

}
