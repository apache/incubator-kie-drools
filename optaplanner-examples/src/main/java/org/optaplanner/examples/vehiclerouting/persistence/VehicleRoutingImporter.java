/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.vehiclerouting.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.vehiclerouting.app.VehicleRoutingApp;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.AirLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.DistanceType;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.segmented.HubSegmentLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.segmented.RoadSegmentLocation;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedDepot;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedVehicleRoutingSolution;

public class VehicleRoutingImporter extends AbstractTxtSolutionImporter<VehicleRoutingSolution> {

    public static void main(String[] args) {
        SolutionConverter<VehicleRoutingSolution> converter = SolutionConverter.createImportConverter(
                VehicleRoutingApp.DATA_DIR_NAME, new VehicleRoutingImporter(), VehicleRoutingSolution.class);
        converter.convert("vrpweb/basic/air/A-n33-k6.vrp", "cvrp-32customers.xml");
        converter.convert("vrpweb/basic/air/A-n55-k9.vrp", "cvrp-54customers.xml");
        converter.convert("vrpweb/basic/air/F-n72-k4.vrp", "cvrp-72customers.xml");
        converter.convert("vrpweb/timewindowed/air/Solomon_025_C101.vrp", "cvrptw-25customers.xml");
        converter.convert("vrpweb/timewindowed/air/Solomon_100_R101.vrp", "cvrptw-100customers-A.xml");
        converter.convert("vrpweb/timewindowed/air/Solomon_100_R201.vrp", "cvrptw-100customers-B.xml");
        converter.convert("vrpweb/timewindowed/air/Homberger_0400_R1_4_1.vrp", "cvrptw-400customers.xml");
        converter.convert("vrpweb/basic/road-unknown/bays-n29-k5.vrp", "road-cvrp-29customers.xml");
    }

    @Override
    public String getInputFileSuffix() {
        return "vrp";
    }

    @Override
    public TxtInputBuilder<VehicleRoutingSolution> createTxtInputBuilder() {
        return new VehicleRoutingInputBuilder();
    }

    public static class VehicleRoutingInputBuilder extends TxtInputBuilder<VehicleRoutingSolution> {

        private VehicleRoutingSolution solution;

        private boolean timewindowed;
        private int customerListSize;
        private int vehicleListSize;
        private int capacity;
        private Map<Long, Location> locationMap;
        private List<Depot> depotList;

        @Override
        public VehicleRoutingSolution readSolution() throws IOException {
            String firstLine = readStringValue();
            if (firstLine.matches("\\s*NAME\\s*:.*")) {
                // Might be replaced by TimeWindowedVehicleRoutingSolution later on
                solution = new VehicleRoutingSolution();
                solution.setId(0L);
                solution.setName(removePrefixSuffixFromLine(firstLine, "\\s*NAME\\s*:", ""));
                readVrpWebFormat();
            } else if (splitBySpacesOrTabs(firstLine).length == 3) {
                timewindowed = false;
                solution = new VehicleRoutingSolution();
                solution.setId(0L);
                solution.setName(FilenameUtils.getBaseName(inputFile.getName()));
                String[] tokens = splitBySpacesOrTabs(firstLine, 3);
                customerListSize = Integer.parseInt(tokens[0]);
                vehicleListSize = Integer.parseInt(tokens[1]);
                capacity = Integer.parseInt(tokens[2]);
                readCourseraFormat();
            } else {
                timewindowed = true;
                solution = new TimeWindowedVehicleRoutingSolution();
                solution.setId(0L);
                solution.setName(firstLine);
                readTimeWindowedFormat();
            }
            BigInteger a = factorial(customerListSize + vehicleListSize - 1);
            BigInteger b = factorial(vehicleListSize - 1);
            BigInteger possibleSolutionSize = (a == null || b == null) ? null : a.divide(b);
            logger.info("VehicleRoutingSolution {} has {} depots, {} vehicles and {} customers with a search space of {}.",
                    getInputId(),
                    solution.getDepotList().size(),
                    solution.getVehicleList().size(),
                    solution.getCustomerList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return solution;
        }

        // ************************************************************************
        // CVRP normal format. See http://neo.lcc.uma.es/vrp/
        // ************************************************************************

        public void readVrpWebFormat() throws IOException {
            readVrpWebHeaders();
            readVrpWebLocationList();
            readVrpWebCustomerList();
            readVrpWebDepotList();
            createVrpWebVehicleList();
            readConstantLine("EOF");
        }

        private void readVrpWebHeaders() throws IOException {
            skipOptionalConstantLines("COMMENT *:.*");
            String vrpType = readStringValue("TYPE *:");
            switch (vrpType) {
                case "CVRP":
                    timewindowed = false;
                    break;
                case "CVRPTW":
                    timewindowed = true;
                    Long solutionId = solution.getId();
                    String solutionName = solution.getName();
                    solution = new TimeWindowedVehicleRoutingSolution();
                    solution.setId(solutionId);
                    solution.setName(solutionName);
                    break;
                default:
                    throw new IllegalArgumentException("The vrpType (" + vrpType + ") is not supported.");
            }
            customerListSize = readIntegerValue("DIMENSION *:");
            String edgeWeightType = readStringValue("EDGE_WEIGHT_TYPE *:");
            if (edgeWeightType.equalsIgnoreCase("EUC_2D")) {
                solution.setDistanceType(DistanceType.AIR_DISTANCE);
            } else if (edgeWeightType.equalsIgnoreCase("EXPLICIT")) {
                solution.setDistanceType(DistanceType.ROAD_DISTANCE);
                String edgeWeightFormat = readStringValue("EDGE_WEIGHT_FORMAT *:");
                if (!edgeWeightFormat.equalsIgnoreCase("FULL_MATRIX")) {
                    throw new IllegalArgumentException("The edgeWeightFormat (" + edgeWeightFormat + ") is not supported.");
                }
            } else if (edgeWeightType.equalsIgnoreCase("SEGMENTED_EXPLICIT")) {
                solution.setDistanceType(DistanceType.SEGMENTED_ROAD_DISTANCE);
                String edgeWeightFormat = readStringValue("EDGE_WEIGHT_FORMAT *:");
                if (!edgeWeightFormat.equalsIgnoreCase("HUB_AND_NEARBY_MATRIX")) {
                    throw new IllegalArgumentException("The edgeWeightFormat (" + edgeWeightFormat + ") is not supported.");
                }
            } else {
                throw new IllegalArgumentException("The edgeWeightType (" + edgeWeightType + ") is not supported.");
            }
            solution.setDistanceUnitOfMeasurement(readOptionalStringValue("EDGE_WEIGHT_UNIT_OF_MEASUREMENT *:", "distance"));
            capacity = readIntegerValue("CAPACITY *:");
        }

        private void readVrpWebLocationList() throws IOException {
            DistanceType distanceType = solution.getDistanceType();
            List<HubSegmentLocation> hubLocationList = null;
            locationMap = new LinkedHashMap<>(customerListSize);
            if (distanceType == DistanceType.SEGMENTED_ROAD_DISTANCE) {
                int hubListSize = readIntegerValue("HUBS *:");
                hubLocationList = new ArrayList<>(hubListSize);
                readConstantLine("HUB_COORD_SECTION");
                for (int i = 0; i < hubListSize; i++) {
                    String line = bufferedReader.readLine();
                    String[] lineTokens = splitBySpacesOrTabs(line.trim(), 3, 4);
                    HubSegmentLocation location = new HubSegmentLocation();
                    location.setId(Long.parseLong(lineTokens[0]));
                    location.setLatitude(Double.parseDouble(lineTokens[1]));
                    location.setLongitude(Double.parseDouble(lineTokens[2]));
                    if (lineTokens.length >= 4) {
                        location.setName(lineTokens[3]);
                    }
                    hubLocationList.add(location);
                    locationMap.put(location.getId(), location);
                }
            }
            List<Location> customerLocationList = new ArrayList<>(customerListSize);
            readConstantLine("NODE_COORD_SECTION");
            for (int i = 0; i < customerListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpacesOrTabs(line.trim(), 3, 4);
                Location location;
                switch (distanceType) {
                    case AIR_DISTANCE:
                        location = new AirLocation();
                        break;
                    case ROAD_DISTANCE:
                        location = new RoadLocation();
                        break;
                    case SEGMENTED_ROAD_DISTANCE:
                        location = new RoadSegmentLocation();
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
                customerLocationList.add(location);
                locationMap.put(location.getId(), location);
            }
            if (distanceType == DistanceType.ROAD_DISTANCE) {
                readConstantLine("EDGE_WEIGHT_SECTION");
                for (int i = 0; i < customerListSize; i++) {
                    RoadLocation location = (RoadLocation) customerLocationList.get(i);
                    Map<RoadLocation, Double> travelDistanceMap = new LinkedHashMap<>(customerListSize);
                    String line = bufferedReader.readLine();
                    String[] lineTokens = splitBySpacesOrTabs(line.trim(), customerListSize);
                    for (int j = 0; j < customerListSize; j++) {
                        double travelDistance = Double.parseDouble(lineTokens[j]);
                        if (i == j) {
                            if (travelDistance != 0.0) {
                                throw new IllegalStateException("The travelDistance (" + travelDistance
                                        + ") should be zero.");
                            }
                        } else {
                            RoadLocation otherLocation = (RoadLocation) customerLocationList.get(j);
                            travelDistanceMap.put(otherLocation, travelDistance);
                        }
                    }
                    location.setTravelDistanceMap(travelDistanceMap);
                }
            }
            if (distanceType == DistanceType.SEGMENTED_ROAD_DISTANCE) {
                readConstantLine("SEGMENTED_EDGE_WEIGHT_SECTION");
                int locationListSize = hubLocationList.size() + customerListSize;
                for (int i = 0; i < locationListSize; i++) {
                    String line = bufferedReader.readLine();
                    String[] lineTokens = splitBySpacesOrTabs(line.trim(), 3, null);
                    if (lineTokens.length % 2 != 1) {
                        throw new IllegalArgumentException("Invalid SEGMENTED_EDGE_WEIGHT_SECTION line (" + line + ").");
                    }
                    long id = Long.parseLong(lineTokens[0]);
                    Location location = locationMap.get(id);
                    if (location == null) {
                        throw new IllegalArgumentException("The location with id (" + id + ") of line (" + line + ") does not exist.");
                    }
                    Map<HubSegmentLocation, Double> hubTravelDistanceMap = new LinkedHashMap<>(lineTokens.length / 2);
                    Map<RoadSegmentLocation, Double> nearbyTravelDistanceMap = new LinkedHashMap<>(lineTokens.length / 2);
                    for (int j = 1; j < lineTokens.length; j += 2) {
                        Location otherLocation = locationMap.get(Long.parseLong(lineTokens[j]));
                        double travelDistance = Double.parseDouble(lineTokens[j + 1]);
                        if (otherLocation instanceof HubSegmentLocation) {
                            hubTravelDistanceMap.put((HubSegmentLocation) otherLocation, travelDistance);
                        } else {
                            nearbyTravelDistanceMap.put((RoadSegmentLocation) otherLocation, travelDistance);
                        }
                    }
                    if (location instanceof HubSegmentLocation) {
                        HubSegmentLocation hubSegmentLocation = (HubSegmentLocation) location;
                        hubSegmentLocation.setHubTravelDistanceMap(hubTravelDistanceMap);
                        hubSegmentLocation.setNearbyTravelDistanceMap(nearbyTravelDistanceMap);
                    } else {
                        RoadSegmentLocation roadSegmentLocation = (RoadSegmentLocation) location;
                        roadSegmentLocation.setHubTravelDistanceMap(hubTravelDistanceMap);
                        roadSegmentLocation.setNearbyTravelDistanceMap(nearbyTravelDistanceMap);
                    }
                }
            }
            List<Location> locationList;
            if (distanceType == DistanceType.SEGMENTED_ROAD_DISTANCE) {
                locationList = new ArrayList<>(hubLocationList.size() + customerListSize);
                locationList.addAll(hubLocationList);
                locationList.addAll(customerLocationList);
            } else {
                locationList = customerLocationList;
            }
            solution.setLocationList(locationList);
        }

        private void readVrpWebCustomerList() throws IOException {
            readConstantLine("DEMAND_SECTION");
            depotList = new ArrayList<>(customerListSize);
            List<Customer> customerList = new ArrayList<>(customerListSize);
            for (int i = 0; i < customerListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpacesOrTabs(line.trim(), timewindowed ? 5 : 2);
                long id = Long.parseLong(lineTokens[0]);
                int demand = Integer.parseInt(lineTokens[1]);
                // Depots have no demand
                if (demand == 0) {
                    Depot depot = timewindowed ? new TimeWindowedDepot() : new Depot();
                    depot.setId(id);
                    Location location = locationMap.get(id);
                    if (location == null) {
                        throw new IllegalArgumentException("The depot with id (" + id
                                + ") has no location (" + location + ").");
                    }
                    depot.setLocation(location);
                    if (timewindowed) {
                        TimeWindowedDepot timeWindowedDepot = (TimeWindowedDepot) depot;
                        timeWindowedDepot.setReadyTime(Long.parseLong(lineTokens[2]));
                        timeWindowedDepot.setDueTime(Long.parseLong(lineTokens[3]));
                        long serviceDuration = Long.parseLong(lineTokens[4]);
                        if (serviceDuration != 0L) {
                            throw new IllegalArgumentException("The depot with id (" + id
                                    + ") has a serviceDuration (" + serviceDuration + ") that is not 0.");
                        }
                    }
                    depotList.add(depot);
                } else {
                    Customer customer = timewindowed ? new TimeWindowedCustomer() : new Customer();
                    customer.setId(id);
                    Location location = locationMap.get(id);
                    if (location == null) {
                        throw new IllegalArgumentException("The customer with id (" + id
                                + ") has no location (" + location + ").");
                    }
                    customer.setLocation(location);
                    customer.setDemand(demand);
                    if (timewindowed) {
                        TimeWindowedCustomer timeWindowedCustomer = (TimeWindowedCustomer) customer;
                        timeWindowedCustomer.setReadyTime(Long.parseLong(lineTokens[2]));
                        timeWindowedCustomer.setDueTime(Long.parseLong(lineTokens[3]));
                        timeWindowedCustomer.setServiceDuration(Long.parseLong(lineTokens[4]));
                    }
                    // Notice that we leave the PlanningVariable properties on null
                    customerList.add(customer);
                }
            }
            solution.setCustomerList(customerList);
            solution.setDepotList(depotList);
        }

        private void readVrpWebDepotList() throws IOException {
            readConstantLine("DEPOT_SECTION");
            int depotCount = 0;
            long id = readLongValue();
            while (id != -1) {
                depotCount++;
                id = readLongValue();
            }
            if (depotCount != depotList.size()) {
                throw new IllegalStateException("The number of demands with 0 demand (" + depotList.size()
                        + ") differs from the number of depots (" + depotCount + ").");
            }
        }

        private void createVrpWebVehicleList() throws IOException {
            String inputFileName = inputFile.getName();
            if (inputFileName.toLowerCase().startsWith("tutorial")) {
                vehicleListSize = readIntegerValue("VEHICLES *:");
            } else {
                String inputFileNameRegex = "^.+\\-k(\\d+)\\.vrp$";
                if (!inputFileName.matches(inputFileNameRegex)) {
                    throw new IllegalArgumentException("The inputFileName (" + inputFileName
                            + ") does not match the inputFileNameRegex (" + inputFileNameRegex + ").");
                }
                String vehicleListSizeString = inputFileName.replaceAll(inputFileNameRegex, "$1");
                try {
                    vehicleListSize = Integer.parseInt(vehicleListSizeString);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("The inputFileName (" + inputFileName
                            + ") has a vehicleListSizeString (" + vehicleListSizeString + ") that is not a number.", e);
                }
            }
            createVehicleList();
        }

        private void createVehicleList() {
            List<Vehicle> vehicleList = new ArrayList<>(vehicleListSize);
            long id = 0;
            for (int i = 0; i < vehicleListSize; i++) {
                Vehicle vehicle = new Vehicle();
                vehicle.setId(id);
                id++;
                vehicle.setCapacity(capacity);
                // Round robin the vehicles to a depot if there are multiple depots
                vehicle.setDepot(depotList.get(i % depotList.size()));
                vehicleList.add(vehicle);
            }
            solution.setVehicleList(vehicleList);
        }

        // ************************************************************************
        // CVRP coursera format. See https://class.coursera.org/optimization-001/
        // ************************************************************************

        public void readCourseraFormat() throws IOException {
            solution.setDistanceType(DistanceType.AIR_DISTANCE);
            solution.setDistanceUnitOfMeasurement("distance");
            List<Location> locationList = new ArrayList<>(customerListSize);
            depotList = new ArrayList<>(1);
            List<Customer> customerList = new ArrayList<>(customerListSize);
            locationMap = new LinkedHashMap<>(customerListSize);
            for (int i = 0; i < customerListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpacesOrTabs(line.trim(), 3, 4);
                AirLocation location = new AirLocation();
                location.setId((long) i);
                location.setLatitude(Double.parseDouble(lineTokens[1]));
                location.setLongitude(Double.parseDouble(lineTokens[2]));
                if (lineTokens.length >= 4) {
                    location.setName(lineTokens[3]);
                }
                locationList.add(location);
                if (i == 0) {
                    Depot depot = new Depot();
                    depot.setId((long) i);
                    depot.setLocation(location);
                    depotList.add(depot);
                } else {
                    Customer customer = new Customer();
                    customer.setId((long) i);
                    customer.setLocation(location);
                    int demand = Integer.parseInt(lineTokens[0]);
                    customer.setDemand(demand);
                    // Notice that we leave the PlanningVariable properties on null
                    // Do not add a customer that has no demand
                    if (demand != 0) {
                        customerList.add(customer);
                    }
                }
            }
            solution.setLocationList(locationList);
            solution.setDepotList(depotList);
            solution.setCustomerList(customerList);
            createVehicleList();
        }

        // ************************************************************************
        // CVRPTW normal format. See http://neo.lcc.uma.es/vrp/
        // ************************************************************************

        public void readTimeWindowedFormat() throws IOException {
            readTimeWindowedHeaders();
            readTimeWindowedDepotAndCustomers();
            createVehicleList();
        }

        private void readTimeWindowedHeaders() throws IOException {
            solution.setDistanceType(DistanceType.AIR_DISTANCE);
            solution.setDistanceUnitOfMeasurement("distance");
            readEmptyLine();
            readConstantLine("VEHICLE");
            readConstantLine("NUMBER +CAPACITY");
            String[] lineTokens = splitBySpacesOrTabs(readStringValue(), 2);
            vehicleListSize = Integer.parseInt(lineTokens[0]);
            capacity = Integer.parseInt(lineTokens[1]);
            readEmptyLine();
            readConstantLine("CUSTOMER");
            readConstantLine("CUST\\s+NO\\.\\s+XCOORD\\.\\s+YCOORD\\.\\s+DEMAND\\s+READY\\s+TIME\\s+DUE\\s+DATE\\s+SERVICE\\s+TIME");
            readEmptyLine();
        }

        private void readTimeWindowedDepotAndCustomers() throws IOException {
            String line = bufferedReader.readLine();
            int locationListSizeEstimation = 25;
            List<Location> locationList = new ArrayList<>(locationListSizeEstimation);
            depotList = new ArrayList<>(1);
            TimeWindowedDepot depot = null;
            List<Customer> customerList = new ArrayList<>(locationListSizeEstimation);
            boolean first = true;
            while (line != null && !line.trim().isEmpty()) {
                String[] lineTokens = splitBySpacesOrTabs(line.trim(), 7);
                long id = Long.parseLong(lineTokens[0]);
                AirLocation location = new AirLocation();
                location.setId(id);
                location.setLatitude(Double.parseDouble(lineTokens[1]));
                location.setLongitude(Double.parseDouble(lineTokens[2]));
                locationList.add(location);
                int demand = Integer.parseInt(lineTokens[3]);
                long readyTime = Long.parseLong(lineTokens[4]) * 1000L;
                long dueTime = Long.parseLong(lineTokens[5]) * 1000L;
                long serviceDuration = Long.parseLong(lineTokens[6]) * 1000L;
                if (first) {
                    depot = new TimeWindowedDepot();
                    depot.setId(id);
                    depot.setLocation(location);
                    if (demand != 0) {
                        throw new IllegalArgumentException("The depot with id (" + id
                                + ") has a demand (" + demand + ").");
                    }
                    depot.setReadyTime(readyTime);
                    depot.setDueTime(dueTime);
                    if (serviceDuration != 0) {
                        throw new IllegalArgumentException("The depot with id (" + id
                                + ") has a serviceDuration (" + serviceDuration + ").");
                    }
                    depotList.add(depot);
                    first = false;
                } else {
                    TimeWindowedCustomer customer = new TimeWindowedCustomer();
                    customer.setId(id);
                    customer.setLocation(location);
                    customer.setDemand(demand);
                    customer.setReadyTime(readyTime);
                    // Score constraint arrivalAfterDueTimeAtDepot is a built-in hard constraint in VehicleRoutingImporter
                    long maximumDueTime = depot.getDueTime()
                            - serviceDuration - location.getDistanceTo(depot.getLocation());
                    if (dueTime > maximumDueTime) {
                        logger.warn("The customer ({})'s dueTime ({}) was automatically reduced" +
                                " to maximumDueTime ({}) because of the depot's dueTime ({}).",
                                customer, dueTime, maximumDueTime, depot.getDueTime());
                        dueTime = maximumDueTime;
                    }
                    customer.setDueTime(dueTime);
                    customer.setServiceDuration(serviceDuration);
                    // Notice that we leave the PlanningVariable properties on null
                    // Do not add a customer that has no demand
                    if (demand != 0) {
                        customerList.add(customer);
                    }
                }
                line = bufferedReader.readLine();
            }
            solution.setLocationList(locationList);
            solution.setDepotList(depotList);
            solution.setCustomerList(customerList);
            customerListSize = locationList.size();
        }

    }

}
