/*
 * Copyright 2012 JBoss Inc
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.vehiclerouting.domain.VrpCustomer;
import org.optaplanner.examples.vehiclerouting.domain.VrpDepot;
import org.optaplanner.examples.vehiclerouting.domain.VrpLocation;
import org.optaplanner.examples.vehiclerouting.domain.VrpSchedule;
import org.optaplanner.examples.vehiclerouting.domain.VrpVehicle;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.VrpTimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.VrpTimeWindowedDepot;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.VrpTimeWindowedSchedule;

public class VehicleRoutingSolutionImporter extends AbstractTxtSolutionImporter {

    private static final String INPUT_FILE_SUFFIX = ".vrp";

    public static void main(String[] args) {
        new VehicleRoutingSolutionImporter().convertAll();
    }

    public VehicleRoutingSolutionImporter() {
        super(new VehicleRoutingDao());
    }

    @Override
    public String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new VrpScheduleInputBuilder();
    }

    public class VrpScheduleInputBuilder extends TxtInputBuilder {

        private VrpSchedule schedule;

        private int locationListSize;
        private int vehicleListSize;
        private int capacity;
        private Map<Long, VrpLocation> locationMap;
        private List<VrpDepot> depotList;

        public Solution readSolution() throws IOException {
            String firstLine = readStringValue();
            if (firstLine.trim().startsWith("NAME :")) {
                schedule = new VrpSchedule();
                schedule.setId(0L);
                schedule.setName(removePrefixSuffixFromLine(firstLine, "NAME :", ""));
                readBasicSolution();
            } if (splitBySpace(firstLine).length == 3) {
                schedule = new VrpSchedule();
                schedule.setId(0L);
                String[] tokens = splitBySpace(firstLine, 3);
                locationListSize = Integer.parseInt(tokens[0]);
                vehicleListSize = Integer.parseInt(tokens[1]);
                capacity = Integer.parseInt(tokens[2]);
                readAlternativeBasicSolution();
            } else {
                schedule = new VrpTimeWindowedSchedule();
                schedule.setId(0L);
                schedule.setName(firstLine);
                readTimeWindowedSolution();
            }
            // TODO search space does not take different vehicles into account
            BigInteger possibleSolutionSize = factorial(schedule.getLocationList().size() - 1);
            String flooredPossibleSolutionSize = "10^" + (possibleSolutionSize.toString().length() - 1);
            logger.info("VrpSchedule {} has {} depots, {} vehicles and {} customers with a search space of {}.",
                    getInputId(),
                    schedule.getDepotList().size(),
                    schedule.getVehicleList().size(),
                    schedule.getCustomerList().size(),
                    flooredPossibleSolutionSize);
            return schedule;
        }

        // ************************************************************************
        // CVRP normal format. See http://neo.lcc.uma.es/vrp/
        // ************************************************************************

        public void readBasicSolution() throws IOException {
            readBasicHeaders();
            readBasicLocationList();
            readBasicCustomerList();
            readBasicDepotList();
            createBasicVehicleList();
            readConstantLine("EOF");
        }

        private void readBasicHeaders() throws IOException {
            readUntilConstantLine("TYPE : CVRP");
            locationListSize = readIntegerValue("DIMENSION :");
            String edgeWeightType = readStringValue("EDGE_WEIGHT_TYPE :");
            if (!edgeWeightType.equalsIgnoreCase("EUC_2D")) {
                // Only Euclidean distance is implemented in VrpLocation.getDistance(VrpLocation)
                throw new IllegalArgumentException("The edgeWeightType (" + edgeWeightType + ") is not supported.");
            }
            capacity = readIntegerValue("CAPACITY :");
        }

        private void readBasicLocationList() throws IOException {
            readConstantLine("NODE_COORD_SECTION");
            List<VrpLocation> locationList = new ArrayList<VrpLocation>(locationListSize);
            locationMap = new HashMap<Long, VrpLocation>(locationListSize);
            for (int i = 0; i < locationListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line.trim().replaceAll(" +", " "), 3);
                VrpLocation location = new VrpLocation();
                location.setId(Long.parseLong(lineTokens[0]));
                location.setLatitude(Double.parseDouble(lineTokens[1]));
                location.setLongitude(Double.parseDouble(lineTokens[2]));
                if (lineTokens.length >= 4) {
                    location.setName(lineTokens[3]);
                }
                locationList.add(location);
                locationMap.put(location.getId(), location);
            }
            schedule.setLocationList(locationList);
        }

        private void readBasicCustomerList() throws IOException {
            readConstantLine("DEMAND_SECTION");
            List<VrpCustomer> customerList = new ArrayList<VrpCustomer>(locationListSize);
            for (int i = 0; i < locationListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line.trim().replaceAll(" +", " "), 2);
                VrpCustomer customer = new VrpCustomer();
                long id = Long.parseLong(lineTokens[0]);
                customer.setId(id);
                VrpLocation location = locationMap.get(id);
                if (location == null) {
                    throw new IllegalArgumentException("The customer with id (" + id
                            + ") has no location (" + location + ").");
                }
                customer.setLocation(location);
                int demand = Integer.parseInt(lineTokens[1]);
                customer.setDemand(demand);
                // Notice that we leave the PlanningVariable properties on null
                // Do not add a customer that has no demand
                if (demand != 0) {
                    customerList.add(customer);
                }
            }
            schedule.setCustomerList(customerList);
        }

        private void readBasicDepotList() throws IOException {
            readConstantLine("DEPOT_SECTION");
            depotList = new ArrayList<VrpDepot>(locationListSize);
            long id = readLongValue();
            while (id != -1) {
                VrpDepot depot = new VrpDepot();
                depot.setId(id);
                VrpLocation location = locationMap.get(id);
                if (location == null) {
                    throw new IllegalArgumentException("The depot with id (" + id
                            + ") has no location (" + location + ").");
                }
                depot.setLocation(location);
                depotList.add(depot);
                id = readLongValue();
            }
            schedule.setDepotList(depotList);
        }

        private void createBasicVehicleList() throws IOException {
            String inputFileName = inputFile.getName();
            if (inputFileName.toLowerCase().startsWith("demo")) {
                vehicleListSize = readIntegerValue("VEHICLES :");
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
            List<VrpVehicle> vehicleList = new ArrayList<VrpVehicle>(vehicleListSize);
            long id = 0;
            for (int i = 0; i < vehicleListSize; i++) {
                VrpVehicle vehicle = new VrpVehicle();
                vehicle.setId(id);
                id++;
                vehicle.setCapacity(capacity);
                vehicle.setDepot(depotList.get(0));
                vehicleList.add(vehicle);
            }
            schedule.setVehicleList(vehicleList);
        }

        // ************************************************************************
        // CVRP alternative format. See https://class.coursera.org/optimization-001/
        // ************************************************************************

        public void readAlternativeBasicSolution() throws IOException {
            List<VrpLocation> locationList = new ArrayList<VrpLocation>(locationListSize);
            depotList = new ArrayList<VrpDepot>(1);
            List<VrpCustomer> customerList = new ArrayList<VrpCustomer>(locationListSize);
            locationMap = new HashMap<Long, VrpLocation>(locationListSize);
            for (int i = 0; i < locationListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line.trim().replaceAll(" +", " "), 3);
                VrpLocation location = new VrpLocation();
                location.setId((long) i);
                location.setLatitude(Double.parseDouble(lineTokens[1]));
                location.setLongitude(Double.parseDouble(lineTokens[2]));
                if (lineTokens.length >= 4) {
                    location.setName(lineTokens[3]);
                }
                locationList.add(location);
                if (i == 0) {
                    VrpDepot depot = new VrpDepot();
                    depot.setId((long) i);
                    depot.setLocation(location);
                    depotList.add(depot);
                } else {
                    VrpCustomer customer = new VrpCustomer();
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
            schedule.setLocationList(locationList);
            schedule.setDepotList(depotList);
            schedule.setCustomerList(customerList);
            createVehicleList();
        }

        // ************************************************************************
        // CVRPTW normal format. See http://neo.lcc.uma.es/vrp/
        // ************************************************************************

        public void readTimeWindowedSolution() throws IOException {
            readTimeWindowedHeaders();
            readTimeWindowedDepotAndCustomers();
            createVehicleList();
        }

        private void readTimeWindowedHeaders() throws IOException {
            readEmptyLine();
            readConstantLine("VEHICLE");
            readConstantLine("NUMBER     CAPACITY");
            String[] lineTokens = splitBySpacesOrTabs(readStringValue(), 2);
            vehicleListSize = Integer.parseInt(lineTokens[0]);
            capacity = Integer.parseInt(lineTokens[1]);
            readEmptyLine();
            readConstantLine("CUSTOMER");
            readRegexConstantLine("CUST\\s+NO\\.\\s+XCOORD\\.\\s+YCOORD\\.\\s+DEMAND\\s+READY\\s+TIME\\s+DUE\\s+DATE\\s+SERVICE\\s+TIME");
            readEmptyLine();
        }

        private void readTimeWindowedDepotAndCustomers() throws IOException {
            String line = bufferedReader.readLine();
            int locationListSizeEstimation = 25;
            List<VrpLocation> locationList = new ArrayList<VrpLocation>(locationListSizeEstimation);
            depotList = new ArrayList<VrpDepot>(1);
            List<VrpCustomer> customerList = new ArrayList<VrpCustomer>(locationListSizeEstimation);
            boolean first = true;
            while (line != null && !line.trim().isEmpty()) {
                String[] lineTokens = splitBySpacesOrTabs(line.trim(), 7);
                long id = Long.parseLong(lineTokens[0]);

                VrpLocation location = new VrpLocation();
                location.setId(id);
                location.setLatitude(Double.parseDouble(lineTokens[1]));
                location.setLongitude(Double.parseDouble(lineTokens[2]));
                locationList.add(location);
                if (first) {
                    VrpTimeWindowedDepot depot = new VrpTimeWindowedDepot();
                    depot.setId(id);
                    depot.setLocation(location);
                    int demand = Integer.parseInt(lineTokens[3]);
                    if (demand != 0) {
                        throw new IllegalArgumentException("The depot with id (" + id
                                + ") has a demand (" + demand + ").");
                    }
                    depot.setReadyTime(Integer.parseInt(lineTokens[4]));
                    depot.setDueTime(Integer.parseInt(lineTokens[5]));
                    int serviceDuration = Integer.parseInt(lineTokens[6]);
                    if (serviceDuration != 0) {
                        throw new IllegalArgumentException("The depot with id (" + id
                                + ") has a serviceDuration (" + serviceDuration + ").");
                    }
                    depotList.add(depot);
                    first = false;
                } else {
                    VrpTimeWindowedCustomer customer = new VrpTimeWindowedCustomer();
                    customer.setId(id);
                    customer.setLocation(location);
                    int demand = Integer.parseInt(lineTokens[3]);
                    customer.setDemand(demand);
                    customer.setReadyTime(Integer.parseInt(lineTokens[4]));
                    customer.setDueTime(Integer.parseInt(lineTokens[5]));
                    customer.setServiceDuration(Integer.parseInt(lineTokens[6]));
                    // Notice that we leave the PlanningVariable properties on null
                    // Do not add a customer that has no demand
                    if (demand != 0) {
                        customerList.add(customer);
                    }
                }
                line = bufferedReader.readLine();
            }
            schedule.setLocationList(locationList);
            schedule.setDepotList(depotList);
            schedule.setCustomerList(customerList);
        }

    }

}
