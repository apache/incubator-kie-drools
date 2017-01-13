/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.vehiclerouting.persistence.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.persistence.TspImporter;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.AirLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.DistanceType;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingDao;

public class VehicleRoutingTspBasedGenerator extends LoggingMain {

    public static void main(String[] args) {
        new VehicleRoutingTspBasedGenerator().generate();
    }

    protected final TspImporter tspImporter;
    protected final VehicleRoutingDao vehicleRoutingDao;

    public VehicleRoutingTspBasedGenerator() {
        tspImporter = new TspImporter();
        vehicleRoutingDao = new VehicleRoutingDao();
    }

    public void generate() {
        generateVrp(new File(tspImporter.getInputDir(), "usa115475.tsp"), 100, 10, 250);
        generateVrp(new File(tspImporter.getInputDir(), "usa115475.tsp"), 500, 20, 250);
        generateVrp(new File(tspImporter.getInputDir(), "usa115475.tsp"), 1000, 20, 500);
        generateVrp(new File(tspImporter.getInputDir(), "usa115475.tsp"), 5000, 100, 500);
        generateVrp(new File(tspImporter.getInputDir(), "usa115475.tsp"), 10000, 100, 1000);
        generateVrp(new File(tspImporter.getInputDir(), "usa115475.tsp"), 50000, 500, 1000);
        generateVrp(new File(tspImporter.getInputDir(), "usa115475.tsp"), 100000, 500, 2000);
    }

    public void generateVrp(File tspInputFile, int locationListSize, int vehicleListSize, int capacity) {
        TspSolution tspSolution = (TspSolution) tspImporter.readSolution(tspInputFile);
        String name = tspInputFile.getName().replaceAll("\\d+\\.tsp", "")
                + "-n" + locationListSize + "-k" + vehicleListSize;
        File vrpOutputFile = new File(vehicleRoutingDao.getDataDir(), "import/capacitated/" + name + ".vrp");
        if (!vrpOutputFile.getParentFile().exists()) {
            throw new IllegalArgumentException("The vrpOutputFile parent directory (" + vrpOutputFile.getParentFile()
                    + ") does not exist.");
        }
        try (BufferedWriter vrpWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(vrpOutputFile), "UTF-8"))) {
            vrpWriter.write("NAME: " + name + "\n");
            vrpWriter.write("COMMENT: Generated from " + tspInputFile.getName() + "\n");
            vrpWriter.write("TYPE: CVRP\n");
            vrpWriter.write("DIMENSION: " + locationListSize + "\n");
            vrpWriter.write("EDGE_WEIGHT_TYPE: EUC_2D\n");
            vrpWriter.write("CAPACITY: " + capacity + "\n");
            vrpWriter.write("NODE_COORD_SECTION\n");
            List<org.optaplanner.examples.tsp.domain.location.Location> tspLocationList = tspSolution.getLocationList();
            double selectionDecrement = (double) locationListSize / (double) tspLocationList.size();
            double selection = (double) locationListSize;
            int index = 1;
            for (org.optaplanner.examples.tsp.domain.location.Location tspLocation : tspLocationList) {
                double newSelection = selection - selectionDecrement;
                if ((int) newSelection < (int) selection) {
                    vrpWriter.write(index + " " + tspLocation.getLatitude() + " " + tspLocation.getLongitude() + "\n");
                    index++;
                }
                selection = newSelection;
            }
            vrpWriter.write("DEMAND_SECTION\n");
            // maximumDemand is 2 times the averageDemand. And the averageDemand is 2/3rd of available capacity
            int maximumDemand = (4 * vehicleListSize * capacity) / (locationListSize * 3);
            Random random = new Random(37);
            vrpWriter.write("1 0\n");
            for (int i = 2; i <= locationListSize; i++) {
                vrpWriter.write(i + " " + (random.nextInt(maximumDemand) + 1) + "\n");
            }
            vrpWriter.write("DEPOT_SECTION\n");
            vrpWriter.write("1\n");
            vrpWriter.write("-1\n");
            vrpWriter.write("EOF\n");
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read the tspInputFile (" + tspInputFile.getName()
                    + ") or write the vrpOutputFile (" + vrpOutputFile.getName() + ").", e);
        }
        logger.info("Generated: {}", vrpOutputFile);
    }

    public static VehicleRoutingSolution convert(TspSolution tspSolution) {
        VehicleRoutingSolution vehicleRoutingSolution = new VehicleRoutingSolution();
        vehicleRoutingSolution.setName(tspSolution.getName());
        vehicleRoutingSolution.setDistanceType(convert(tspSolution.getDistanceType()));
        vehicleRoutingSolution.setDistanceUnitOfMeasurement(tspSolution.getDistanceUnitOfMeasurement());
        List<org.optaplanner.examples.vehiclerouting.domain.location.Location> locationList = convert(tspSolution.getLocationList());
        vehicleRoutingSolution.setLocationList(locationList);
        org.optaplanner.examples.vehiclerouting.domain.location.Location firstLocation = locationList.get(0);
        Depot depot = new Depot();
        depot.setId(firstLocation.getId());
        depot.setLocation(firstLocation);
        vehicleRoutingSolution.setDepotList(Collections.singletonList(depot));
        Vehicle vehicle = new Vehicle();
        vehicle.setId(firstLocation.getId());
        vehicle.setDepot(depot);
        vehicle.setCapacity(locationList.size() * 10);
        vehicleRoutingSolution.setVehicleList(Collections.singletonList(vehicle));
        List<Customer> customerList = new ArrayList<>(locationList.size());
        for (org.optaplanner.examples.vehiclerouting.domain.location.Location location : locationList.subList(1, locationList.size())) {
            Customer customer = new Customer();
            customer.setId(location.getId());
            customer.setLocation(location);
            customerList.add(customer);
        }
        for (Visit visit : tspSolution.getVisitList()) {
            Customer customer = customerList.get(tspSolution.getVisitList().indexOf(visit));
            Standstill previousStandstill;
            if (visit.getPreviousStandstill() instanceof Domicile) {
                previousStandstill = vehicle;
            } else {
                if (visit.getPreviousStandstill() == null) {
                    previousStandstill = null;
                } else {
                    previousStandstill = customerList.get(tspSolution.getVisitList().indexOf(visit.getPreviousStandstill()));
                }
            }
            customer.setPreviousStandstill(previousStandstill);
            if (previousStandstill != null) {
                previousStandstill.setNextCustomer(customer);
            }
        }
        vehicleRoutingSolution.setCustomerList(customerList);
        if (tspSolution.getScore() != null) {
            vehicleRoutingSolution.setScore(HardSoftLongScore.valueOfUninitialized(tspSolution.getScore().getInitScore(),
                    0, tspSolution.getScore().getScore()));
        }
        return vehicleRoutingSolution;
    }

    private static List<Location> convert(List<org.optaplanner.examples.tsp.domain.location.Location> tspLocationList) {
        List<Location> locationList = new ArrayList<>(tspLocationList.size());
        for (org.optaplanner.examples.tsp.domain.location.Location tspLocation : tspLocationList) {
            Location location;
            if (tspLocation instanceof org.optaplanner.examples.tsp.domain.location.AirLocation) {
                location = new AirLocation();
            } else if (tspLocation instanceof org.optaplanner.examples.tsp.domain.location.RoadLocation) {
                location = new RoadLocation();
            } else {
                throw new IllegalStateException("The tspLocation class (" + tspLocation.getClass() + ") is not implemented.");
            }
            location.setName(tspLocation.getName());
            location.setLatitude(tspLocation.getLatitude());
            location.setLongitude(tspLocation.getLongitude());
            locationList.add(location);
        }
        for (org.optaplanner.examples.tsp.domain.location.Location tspLocation : tspLocationList) {
            if (tspLocation instanceof org.optaplanner.examples.tsp.domain.location.RoadLocation) {
                RoadLocation location = (RoadLocation) locationList.get(tspLocationList.indexOf(tspLocation));
                Map<RoadLocation, Double> travelDistanceMap = new LinkedHashMap<>(tspLocationList.size());
                Map<org.optaplanner.examples.tsp.domain.location.RoadLocation, Double> tspTravelDistanceMap
                        = ((org.optaplanner.examples.tsp.domain.location.RoadLocation) tspLocation).getTravelDistanceMap();
                for (Map.Entry<org.optaplanner.examples.tsp.domain.location.RoadLocation, Double> entry : tspTravelDistanceMap.entrySet()) {
                    travelDistanceMap.put((RoadLocation) locationList.get(tspLocationList.indexOf(entry.getKey())), entry.getValue());
                }
                location.setTravelDistanceMap(travelDistanceMap);
            }
        }
        return locationList;
    }

    private static DistanceType convert(org.optaplanner.examples.tsp.domain.location.DistanceType distanceType) {
        switch (distanceType) {
            case AIR_DISTANCE:
                return DistanceType.AIR_DISTANCE;
            case ROAD_DISTANCE:
                return DistanceType.ROAD_DISTANCE;
            default:
                throw new IllegalStateException("The distanceType (" + distanceType + ") is not implemented.");
        }
    }

}
