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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.optaplanner.examples.coachshuttlegathering.app.CoachShuttleGatheringApp;
import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.BusHub;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.Coach;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.domain.Shuttle;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocationArc;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;

public class CoachShuttleGatheringImporter extends AbstractTxtSolutionImporter<CoachShuttleGatheringSolution> {

    public static void main(String[] args) {
        SolutionConverter<CoachShuttleGatheringSolution> converter = SolutionConverter.createImportConverter(
                CoachShuttleGatheringApp.DATA_DIR_NAME, new CoachShuttleGatheringImporter(),
                CoachShuttleGatheringSolution.class);
        converter.convert("example", "demo01.xml");
    }

    @Override
    public boolean isInputFileDirectory() {
        return true;
    }

    @Override
    public String getInputFileSuffix() {
        throw new IllegalStateException("The inputFile is a directory, so there is no suffix.");
    }

    @Override
    public TxtInputBuilder<CoachShuttleGatheringSolution> createTxtInputBuilder() {
        return new CoachShuttleGatheringInputBuilder();
    }

    @Override
    public CoachShuttleGatheringSolution readSolution(File inputFile) {
        // TODO Bridging hack because InputBuilder is designed for a single File.
        File instanceFile = new File(inputFile, "Busstops.csv");
        return super.readSolution(instanceFile);
    }

    public static class CoachShuttleGatheringInputBuilder extends TxtInputBuilder<CoachShuttleGatheringSolution> {

        private CoachShuttleGatheringSolution solution;

        private Map<List<Double>, RoadLocation> latLongToLocationMap;
        private long busOrStopOrHubId;

        @Override
        public CoachShuttleGatheringSolution readSolution() throws IOException {
            solution = new CoachShuttleGatheringSolution();
            solution.setId(0L);
            readLocationList();
            busOrStopOrHubId = 0L;
            readBusList();
            readBusStopList();

            int busListSize = solution.getCoachList().size() + solution.getShuttleList().size();
            int base = solution.getStopList().size() + solution.getShuttleList().size();
            BigInteger a = factorial(base + busListSize - 1);
            BigInteger b = factorial(busListSize - 1);
            BigInteger possibleSolutionSize = (a == null || b == null) ? null : a.divide(b);
            logger.info("CoachShuttleGathering {} has {} road locations, {} coaches, {} shuttles and {} bus stops"
                    + " with a search space of {}.",
                    getInputId(),
                    solution.getLocationList().size(),
                    solution.getCoachList().size(),
                    solution.getShuttleList().size(),
                    solution.getStopList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return solution;
        }

        @Override
        public String getInputId() {
            return FilenameUtils.getBaseName(inputFile.getParentFile().getPath());
        }

        private void readLocationList() throws IOException {
            File file = new File(inputFile.getParentFile(), "DistanceTimesCoordinates.csv");
            latLongToLocationMap = new HashMap<>();
            List<RoadLocation> locationList = new ArrayList<>();
            long locationId = 0L;
            try (BufferedReader subBufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                readConstantLine(subBufferedReader, "X_COORD;Y_COORD");
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
            }
            solution.setLocationList(locationList);
            for (RoadLocation sourceLocation : locationList) {
                LinkedHashMap<RoadLocation, RoadLocationArc> travelDistanceMap = new LinkedHashMap<>(locationList.size());
                for (RoadLocation targetLocation : locationList) {
                    travelDistanceMap.put(targetLocation, new RoadLocationArc());
                }
                sourceLocation.setTravelDistanceMap(travelDistanceMap);
            }
            readLocationDistancesCoaches();
            readLocationDistancesShuttles();
        }

        private void readLocationDistancesCoaches() throws IOException {
            List<RoadLocation> locationList = solution.getLocationList();
            int locationListSize = locationList.size();
            File file = new File(inputFile.getParentFile(), "DistanceTimesData_COACHES.csv");
            int locationListIndex = 0;
            try (BufferedReader subBufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                readConstantLine(subBufferedReader, "Quadratic matrix with as many rows as DistanceTimesCoordinates");
                for (String line = subBufferedReader.readLine(); line != null; line = subBufferedReader.readLine()) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    RoadLocation sourceLocation = locationList.get(locationListIndex);
                    locationListIndex++;
                    String[] lineTokens = splitBySemicolonSeparatedValue(line, locationListSize * 2);
                    for (int i = 0; i < locationListSize; i++) {
                        RoadLocation targetLocation = locationList.get(i);
                        RoadLocationArc locationArc = sourceLocation.getTravelDistanceMap().get(targetLocation);
                        locationArc.setCoachDistance(Integer.parseInt(lineTokens[i * 2]));
                        locationArc.setCoachDuration(Integer.parseInt(lineTokens[i * 2 + 1]));
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not read the file (" + file.getName() + ").", e);
            }
        }

        private void readLocationDistancesShuttles() throws IOException {
            List<RoadLocation> locationList = solution.getLocationList();
            int locationListSize = locationList.size();
            File file = new File(inputFile.getParentFile(), "DistanceTimesData_SHUTTLES.csv");
            int locationListIndex = 0;
            try (BufferedReader subBufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                readConstantLine(subBufferedReader, "Quadratic matrix with as many rows as DistanceTimesCoordinates");
                for (String line = subBufferedReader.readLine(); line != null; line = subBufferedReader.readLine()) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    RoadLocation sourceLocation = locationList.get(locationListIndex);
                    locationListIndex++;
                    String[] lineTokens = splitBySemicolonSeparatedValue(line, locationListSize * 2);
                    for (int i = 0; i < locationListSize; i++) {
                        RoadLocation targetLocation = locationList.get(i);
                        RoadLocationArc locationArc = sourceLocation.getTravelDistanceMap().get(targetLocation);
                        locationArc.setShuttleDistance(Integer.parseInt(lineTokens[i * 2]));
                        locationArc.setShuttleDuration(Integer.parseInt(lineTokens[i * 2 + 1]));
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not read the file (" + file.getName() + ").", e);
            }
        }

        private void readBusList() throws IOException {
            File file = new File(inputFile.getParentFile(), "Fleet.csv");
            List<Coach> coachList = new ArrayList<>();
            List<Shuttle> shuttleList = new ArrayList<>();
            try (BufferedReader subBufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                readConstantLine(subBufferedReader,
                        "TYPE;VEHICLE_ID;CAPACITY;MAX_NUM_STOPS;COSTS \\[MU/km\\];COSTS \\[MU/USAGE\\];X_COORD;Y_COORD");
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
                        throw new IllegalArgumentException("The fleet vehicle with name (" + name
                                + ") has an unsupported type (" + busType + ").");
                    }
                    bus.setId(busOrStopOrHubId);
                    busOrStopOrHubId++;
                    bus.setName(name);
                    bus.setCapacity(Integer.parseInt(lineTokens[2]));
                    int stopLimit = Integer.parseInt(lineTokens[3]);
                    if (bus instanceof Coach) {
                        ((Coach) bus).setStopLimit(stopLimit);
                    } else {
                        if (stopLimit != -1) {
                            throw new IllegalArgumentException("The shuttle with name (" + name
                                    + ") has an unsupported stopLimit (" + stopLimit + ").");
                        }
                    }
                    bus.setMileageCost(Integer.parseInt(lineTokens[4]));
                    int setupCost = Integer.parseInt(lineTokens[5]);
                    if (bus instanceof Coach) {
                        if (setupCost != 0) {
                            throw new IllegalArgumentException("The coach with name (" + name
                                    + ") has an unsupported setupCost (" + setupCost + ").");
                        }
                    } else {
                        ((Shuttle) bus).setSetupCost(setupCost);
                    }
                    double latitude = Double.parseDouble(lineTokens[6]);
                    double longitude = Double.parseDouble(lineTokens[7]);
                    RoadLocation location = latLongToLocationMap.get(Arrays.asList(latitude, longitude));
                    if (location == null) {
                        throw new IllegalArgumentException("The fleet vehicle with name (" + name
                                + ") has a coordinate (" + latitude + ", " + longitude
                                + ") which is not in the coordinates file.");
                    }
                    bus.setDepartureLocation(location);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not read the file (" + file.getName() + ").", e);
            }
            solution.setCoachList(coachList);
            solution.setShuttleList(shuttleList);
        }

        private void readBusStopList() throws IOException {
            List<BusStop> busStopList = new ArrayList<>();
            readConstantLine("LOCATION_TYPE;LOCATION_ID;POST_CODE;CITY;X_COORD;Y_COORD;QUANTITY;MAX_TRANSPORT_TIME");
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] lineTokens = splitBySemicolonSeparatedValue(line, 8);
                String busStopType = lineTokens[0];
                String name = lineTokens[1];
                if (busStopType.equalsIgnoreCase("HUB")) {
                    if (solution.getHub() != null) {
                        throw new IllegalArgumentException("The hub with name (" + name
                                + ") is not the only hub (" + solution.getHub().getName() + ").");
                    }
                    BusHub hub = new BusHub();
                    hub.setId(busOrStopOrHubId);
                    busOrStopOrHubId++;
                    hub.setName(name);
                    // Ignore lineTokens[2] and lineTokens[3]
                    double latitude = Double.parseDouble(lineTokens[4]);
                    double longitude = Double.parseDouble(lineTokens[5]);
                    RoadLocation location = latLongToLocationMap.get(Arrays.asList(latitude, longitude));
                    if (location == null) {
                        throw new IllegalArgumentException("The bus stop with name (" + name
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
                    for (Coach coach : solution.getCoachList()) {
                        coach.setDestination(hub);
                    }
                    ArrayList<Shuttle> transferShuttleList = new ArrayList<>(solution.getShuttleList().size());
                    for (Shuttle shuttle : solution.getShuttleList()) {
                        // TODO Use a fixed value Construction Heuristic to initialize the destination variable
                        shuttle.setDestination(hub);
                        transferShuttleList.add(shuttle);
                    }
                    hub.setTransferShuttleList(transferShuttleList);
                    solution.setHub(hub);
                } else if (busStopType.equalsIgnoreCase("BUSSTOP")) {
                    BusStop busStop = new BusStop();
                    busStop.setId(busOrStopOrHubId);
                    busOrStopOrHubId++;
                    busStop.setName(name);
                    // Ignore lineTokens[2] and lineTokens[3]
                    double latitude = Double.parseDouble(lineTokens[4]);
                    double longitude = Double.parseDouble(lineTokens[5]);
                    RoadLocation location = latLongToLocationMap.get(Arrays.asList(latitude, longitude));
                    if (location == null) {
                        throw new IllegalArgumentException("The bus stop with name (" + name
                                + ") has a coordinate (" + latitude + ", " + longitude
                                + ") which is not in the coordinates file.");
                    }
                    busStop.setLocation(location);
                    busStop.setPassengerQuantity(Integer.parseInt(lineTokens[6]));
                    busStop.setTransportTimeLimit(Integer.parseInt(lineTokens[7]));
                    // Trade memory for performance (might not be desired if it is too memory hungry)
                    busStop.setTransferShuttleList(new ArrayList<>(solution.getShuttleList().size()));
                    busStopList.add(busStop);
                } else {
                    throw new IllegalArgumentException("The bus stop with name (" + name
                            + ") has an unsupported type (" + busStopType + ").");
                }
            }
            solution.setStopList(busStopList);
        }

    }

}
