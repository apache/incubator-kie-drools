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

package org.drools.planner.examples.vehiclerouting.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.drools.planner.examples.vehiclerouting.domain.VrpLocation;
import org.drools.planner.examples.vehiclerouting.domain.VrpVehicle;
import org.drools.planner.examples.vehiclerouting.domain.VrpSchedule;
import org.drools.planner.examples.vehiclerouting.domain.VrpCustomer;

public class VehicleRoutingSolutionImporter extends AbstractTxtSolutionImporter {

    private static final String INPUT_FILE_SUFFIX = ".vrp";

    public static void main(String[] args) {
        new VehicleRoutingSolutionImporter().convertAll();
    }

    public VehicleRoutingSolutionImporter() {
        super(new VehicleRoutingDaoImpl());
    }

    @Override
    protected String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new VrpScheduleInputBuilder();
    }

    public class VrpScheduleInputBuilder extends TxtInputBuilder {

        private VrpSchedule vrpSchedule;

        private int locationListSize;
        private int capacity;
        private Map<Long, VrpLocation> locationMap;

        public Solution readSolution() throws IOException {
            vrpSchedule = new VrpSchedule();
            vrpSchedule.setId(0L);
            readHeaders();
            readLocationList();
            readCustomerList();
            readVehicleList();
            readConstantLine("EOF");
            logger.info("VrpSchedule with {} vehicles and {} customers.",
                    vrpSchedule.getVehicleList().size(), vrpSchedule.getCustomerList().size());
            BigInteger possibleSolutionSize = factorial(vrpSchedule.getLocationList().size() - 1);
            String flooredPossibleSolutionSize = "10^" + (possibleSolutionSize.toString().length() - 1);
            logger.info("VrpSchedule with flooredPossibleSolutionSize ({}) and possibleSolutionSize ({}).",
                    flooredPossibleSolutionSize, possibleSolutionSize);
            return vrpSchedule;
        }

        private void readHeaders() throws IOException {
            vrpSchedule.setName(readStringValue("NAME :"));
            readUntilConstantLine("TYPE : CVRP");
            locationListSize = readIntegerValue("DIMENSION :");
            String edgeWeightType = readStringValue("EDGE_WEIGHT_TYPE :");
            if (!edgeWeightType.equalsIgnoreCase("EUC_2D")) {
                // Only Euclidean distance is implemented in VrpLocation.getDistance(VrpLocation)
                throw new IllegalArgumentException("The edgeWeightType (" + edgeWeightType + ") is not supported.");
            }
            capacity = readIntegerValue("CAPACITY :");
        }

        private void readLocationList() throws IOException {
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
            vrpSchedule.setLocationList(locationList);
        }

        private void readCustomerList() throws IOException {
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
                customer.setDemand(Integer.parseInt(lineTokens[1]));
                // Notice that we leave the PlanningVariable properties on null
                customerList.add(customer);
            }
            vrpSchedule.setCustomerList(customerList);

            
        }

        private void readVehicleList() throws IOException {
            readConstantLine("DEPOT_SECTION");
            List<VrpVehicle> vehicleList = new ArrayList<VrpVehicle>(locationListSize);
            long id = readLongValue();
            while (id != -1) {
                VrpVehicle vehicle = new VrpVehicle();
                vehicle.setId(id);
                VrpLocation location = locationMap.get(id);
                if (location == null) {
                    throw new IllegalArgumentException("The customer with id (" + id
                            + ") has no location (" + location + ").");
                }
                vehicle.setDepotLocation(location);
                vehicleList.add(vehicle);
                id = readLongValue();
            }
            vrpSchedule.setVehicleList(vehicleList);
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
