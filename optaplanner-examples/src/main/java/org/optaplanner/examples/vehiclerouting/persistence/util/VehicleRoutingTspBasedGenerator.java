/*
 * Copyright 2014 JBoss Inc
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
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.tsp.domain.TravelingSalesmanTour;
import org.optaplanner.examples.tsp.domain.location.Location;
import org.optaplanner.examples.tsp.persistence.TspImporter;
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
        TravelingSalesmanTour tour = (TravelingSalesmanTour) tspImporter.readSolution(tspInputFile);
        String name = tspInputFile.getName().replaceAll("\\d+\\.tsp", "")
                + "-n" + locationListSize + "-k" + vehicleListSize;
        File vrpOutputFile = new File(vehicleRoutingDao.getDataDir(), "import/capacitated/" + name + ".vrp");
        if (!vrpOutputFile.getParentFile().exists()) {
            throw new IllegalArgumentException("The vrpOutputFile parent directory (" + vrpOutputFile.getParentFile()
                    + ") does not exist.");
        }
        BufferedWriter vrpWriter = null;
        try {
            vrpWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(vrpOutputFile), "UTF-8"));
            vrpWriter.write("NAME: " + name + "\n");
            vrpWriter.write("COMMENT: Generated from " + tspInputFile.getName() + "\n");
            vrpWriter.write("TYPE: CVRP\n");
            vrpWriter.write("DIMENSION: " + locationListSize + "\n");
            vrpWriter.write("EDGE_WEIGHT_TYPE: EUC_2D\n");
            vrpWriter.write("CAPACITY: " + capacity + "\n");
            vrpWriter.write("NODE_COORD_SECTION\n");
            List<Location> locationList = tour.getLocationList();
            double selectionDecrement = (double) locationListSize / (double) locationList.size();
            double selection = (double) locationListSize;
            int index = 1;
            for (Location location : locationList) {
                double newSelection = selection - selectionDecrement;
                if ((int) newSelection < (int) selection) {
                    vrpWriter.write(index + " " + location.getLatitude() + " " + location.getLongitude() + "\n");
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
        } finally {
            IOUtils.closeQuietly(vrpWriter);
        }
        logger.info("Generated: {}", vrpOutputFile);
    }

}
