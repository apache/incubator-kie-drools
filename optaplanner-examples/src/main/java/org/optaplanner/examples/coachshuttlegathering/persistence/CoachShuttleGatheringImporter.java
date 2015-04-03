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

package org.optaplanner.examples.coachshuttlegathering.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.BusHub;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStartPoint;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.BusVisit;
import org.optaplanner.examples.coachshuttlegathering.domain.Coach;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.domain.Shuttle;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;

public class CoachShuttleGatheringImporter extends AbstractTxtSolutionImporter {

    public static void main(String[] args) {
        CoachShuttleGatheringImporter importer = new CoachShuttleGatheringImporter();
        importer.convert("example", "example.xml");
        importer.convert("public_preselection_1", "public_preselection_1.xml");
        importer.convert("public_preselection_2", "public_preselection_2.xml");
    }

    public CoachShuttleGatheringImporter() {
        super(new CoachShuttleGatheringDao());
    }

    public CoachShuttleGatheringImporter(boolean withoutDao) {
        super(withoutDao);
    }

    @Override
    public boolean isInputFileDirectory() {
        return true;
    }

    @Override
    public String getInputFileSuffix() {
        throw new IllegalStateException("The inputFile is a directory, so there is no suffix.");
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new CoachShuttleGatheringInputBuilder();
    }

    @Override
    public Solution readSolution(File inputFile) {
        // TODO Bridging hack because InputBuilder is designed for a single File.
        File instanceFile = new File(inputFile, "Busstops.csv");
        return super.readSolution(instanceFile);
    }

    public static class CoachShuttleGatheringInputBuilder extends TxtInputBuilder {

        private CoachShuttleGatheringSolution solution;

        private Map<List<Double>, RoadLocation> latLongToLocationMap;

        public Solution readSolution() throws IOException {
            solution = new CoachShuttleGatheringSolution();
            solution.setId(0L);
            readLocationList();
            readBusList();
            readBusStopList();
            createStartPointListAndVisitList();

            int busListSize = solution.getCoachList().size() + solution.getShuttleList().size();
            int base = solution.getBusStopList().size() + solution.getShuttleList().size();
            BigInteger possibleSolutionSize = factorial(base + busListSize - 1).divide(factorial(busListSize - 1));
            logger.info("CoachShuttleGathering {} has {} road locations, {} coaches, {} shuttles and {} bus stops"
                         + " with a search space of {}.",
                    getInputId(),
                    solution.getLocationList().size(),
                    solution.getCoachList().size(),
                    solution.getShuttleList().size(),
                    solution.getBusStopList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return solution;
        }

        @Override
        public String getInputId() {
            return FilenameUtils.getBaseName(inputFile.getParentFile().getPath());
        }

        private void readLocationList() throws IOException {
            File file = new File(inputFile.getParentFile(), "DistanceTimesCoordinates.csv");
            latLongToLocationMap = new HashMap<List<Double>, RoadLocation>();
            List<RoadLocation> locationList = new ArrayList<RoadLocation>();
            long locationId = 0L;
            BufferedReader subBufferedReader = null;
            try {
                subBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                subBufferedReader.readLine(); // Ignore first line (comment)
                for (String line = subBufferedReader.readLine(); line != null; line = subBufferedReader.readLine()) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] lineTokens = splitBySemicolonSeparatedValue(line, 2);
                    RoadLocation location = new RoadLocation();
                    location.setId(locationId);
                    locationId++;
                    location.setLatitude(Double.parseDouble(lineTokens[0]));
                    location.setLongitude(Double.parseDouble(lineTokens[1]));
                    locationList.add(location);
                    latLongToLocationMap.put(Arrays.asList(location.getLatitude(), location.getLongitude()), location);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not read the file (" + file.getName() + ").", e);
            } finally {
                IOUtils.closeQuietly(subBufferedReader);
            }
            solution.setLocationList(locationList);
        }

        private void readBusList() throws IOException {
            File file = new File(inputFile.getParentFile(), "Fleet.csv");
            List<Coach> coachList = new ArrayList<Coach>();
            List<Shuttle> shuttleList = new ArrayList<Shuttle>();
            long busId = 0L;
            BufferedReader subBufferedReader = null;
            try {
                subBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                subBufferedReader.readLine(); // Ignore first line (comment)
                for (String line = subBufferedReader.readLine(); line != null; line = subBufferedReader.readLine()) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] lineTokens = splitBySemicolonSeparatedValue(line, 8);
                    Bus bus;
                    String busType = lineTokens[0];
                    String name = lineTokens[1];
                    if (busType.equalsIgnoreCase("COACH")) {
                        bus = new Coach();
                        coachList.add((Coach) bus);
                    } else if (busType.equalsIgnoreCase("SHUTTLE")) {
                        bus = new Shuttle();
                        shuttleList.add((Shuttle) bus);
                    } else {
                        throw new IllegalArgumentException("The fleet vehicle with name (" +  name
                                + ") has an unsupported type (" + busType + ").");
                    }
                    bus.setId(busId);
                    busId++;
                    bus.setName(name);
                    bus.setCapacity(Integer.parseInt(lineTokens[2]));
                    int stopLimit = Integer.parseInt(lineTokens[3]);
                    if (bus instanceof Coach) {
                        ((Coach) bus).setStopLimit(stopLimit);
                    } else {
                        if (stopLimit != -1) {
                            throw new IllegalArgumentException("The shuttle with name (" +  name
                                    + ") has an unsupported stopLimit (" + stopLimit + ").");
                        }
                    }
                    bus.setMileageCost(Integer.parseInt(lineTokens[4]));
                    int setupCost = Integer.parseInt(lineTokens[5]);
                    if (bus instanceof Coach) {
                        if (setupCost != 0) {
                            throw new IllegalArgumentException("The coach with name (" +  name
                                    + ") has an unsupported setupCost (" + setupCost + ").");
                        }
                    } else {
                        ((Shuttle) bus).setSetupCost(setupCost);
                    }
                    double latitude = Double.parseDouble(lineTokens[6]);
                    double longitude = Double.parseDouble(lineTokens[7]);
                    RoadLocation location = latLongToLocationMap.get(Arrays.asList(latitude, longitude));
                    if (location == null) {
                        throw new IllegalArgumentException("The fleet vehicle with name (" +  name
                                + ") has a coordinate (" + latitude + ", " + longitude
                                + ") which is not in the coordinates file.");
                    }
                    bus.setDepartureLocation(location);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not read the file (" + file.getName() + ").", e);
            } finally {
                IOUtils.closeQuietly(subBufferedReader);
            }
            solution.setCoachList(coachList);
            solution.setShuttleList(shuttleList);
        }

        private void readBusStopList() throws IOException {
            List<BusStop> busStopList = new ArrayList<BusStop>();
            long busStopId = 0L;
            bufferedReader.readLine(); // Ignore first line (comment)
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] lineTokens = splitBySemicolonSeparatedValue(line, 8);
                String busStopType = lineTokens[0];
                String name = lineTokens[1];
                if (busStopType.equalsIgnoreCase("HUB")) {
                    if (solution.getHub() != null) {
                        throw new IllegalArgumentException("The hub with name (" +  name
                                + ") is not the only hub (" + solution.getHub().getName() + ").");
                    }
                    BusHub hub = new BusHub();
                    hub.setId(busStopId);
                    busStopId++;
                    hub.setName(name);
                    // Ignore lineTokens[2] and lineTokens[3]
                    double latitude = Double.parseDouble(lineTokens[4]);
                    double longitude = Double.parseDouble(lineTokens[5]);
                    RoadLocation location = latLongToLocationMap.get(Arrays.asList(latitude, longitude));
                    if (location == null) {
                        throw new IllegalArgumentException("The bus stop with name (" +  name
                                + ") has a coordinate (" + latitude + ", " + longitude
                                + ") which is not in the coordinates file.");
                    }
                    hub.setLocation(location);
                    int passengerQuantity = Integer.parseInt(lineTokens[6]);
                    if (passengerQuantity != 0) {
                        throw new IllegalArgumentException("The hub with name (" + name
                                + ") has an unsupported passengerQuantity (" + passengerQuantity + ").");
                    }
                    int transportTimeLimit = Integer.parseInt(lineTokens[7]);
                    if (transportTimeLimit != 0) {
                        throw new IllegalArgumentException("The hub with name (" + name
                                + ") has an unsupported transportTimeLimit (" + transportTimeLimit + ").");
                    }
                    solution.setHub(hub);
                } else if (busStopType.equalsIgnoreCase("BUSSTOP")) {
                    BusStop busStop = new BusStop();
                    busStop.setId(busStopId);
                    busStopId++;
                    busStop.setName(name);
                    // Ignore lineTokens[2] and lineTokens[3]
                    double latitude = Double.parseDouble(lineTokens[4]);
                    double longitude = Double.parseDouble(lineTokens[5]);
                    RoadLocation location = latLongToLocationMap.get(Arrays.asList(latitude, longitude));
                    if (location == null) {
                        throw new IllegalArgumentException("The bus stop with name (" +  name
                                + ") has a coordinate (" + latitude + ", " + longitude
                                + ") which is not in the coordinates file.");
                    }
                    busStop.setLocation(location);
                    busStop.setPassengerQuantity(Integer.parseInt(lineTokens[6]));
                    busStop.setTransportTimeLimit(Integer.parseInt(lineTokens[7]));
                    busStopList.add(busStop);
                } else {
                    throw new IllegalArgumentException("The bus stop with name (" +  name
                            + ") has an unsupported type (" + busStopType + ").");
                }
            }
            solution.setBusStopList(busStopList);
        }

        private void createStartPointListAndVisitList() {
            List<Coach> coachList = solution.getCoachList();
            List<Shuttle> shuttleList = solution.getShuttleList();
            List<BusStartPoint> startPointList = new ArrayList<BusStartPoint>(coachList.size() + shuttleList.size());
            long entityId = 0L;
            for (Coach coach : coachList) {
                BusStartPoint startPoint = new BusStartPoint();
                startPoint.setId(entityId);
                entityId++;
                startPoint.setBus(coach);
                startPointList.add(startPoint);
            }
            for (Shuttle shuttle : shuttleList) {
                BusStartPoint startPoint = new BusStartPoint();
                startPoint.setId(entityId);
                entityId++;
                startPoint.setBus(shuttle);
                startPointList.add(startPoint);
            }
            solution.setStartPointList(startPointList);
            List<BusStop> busStopList = solution.getBusStopList();
            List<BusVisit> visitList = new ArrayList<BusVisit>(busStopList.size());
            for (BusStop busStop : busStopList) {
                BusVisit visit = new BusVisit();
                visit.setId(entityId);
                entityId++;
                visit.setBusStop(busStop);
                visitList.add(visit);
            }
            solution.setVisitList(visitList);
        }

    }

}
