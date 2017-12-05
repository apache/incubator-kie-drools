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

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.vehiclerouting.app.VehicleRoutingApp;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class VehicleRoutingDistanceTypeComparison extends LoggingMain {

    private final ScoreDirectorFactory<VehicleRoutingSolution> scoreDirectorFactory;

    public static void main(String[] args) {
        new VehicleRoutingDistanceTypeComparison().compare(
                "solved/tmp-p-belgium-n50-k10.xml",
                "solved/tmp-p-belgium-road-km-n50-k10.xml",
                "solved/tmp-p-belgium-road-time-n50-k10.xml");
    }

    protected final File dataDir;
    protected final SolutionFileIO<VehicleRoutingSolution> solutionFileIO;

    public VehicleRoutingDistanceTypeComparison() {
        dataDir = CommonApp.determineDataDir(VehicleRoutingApp.DATA_DIR_NAME);
        solutionFileIO = new XStreamSolutionFileIO<>(VehicleRoutingSolution.class);
        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.createFromXmlResource(VehicleRoutingApp.SOLVER_CONFIG);
        scoreDirectorFactory = solverFactory.buildSolver().getScoreDirectorFactory();
    }

    public void compare(String... filePaths) {
        File[] files = new File[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            File file = new File(dataDir, filePaths[i]);
            if (!file.exists()) {
                throw new IllegalArgumentException("The file (" + file + ") does not exist.");
            }
            files[i] = file;
        }
        for (File varFile : files) {
            logger.info("  Results for {}:", varFile.getName());
            // Intentionally create a new instance instead of reusing the older one.
            VehicleRoutingSolution variablesSolution = (VehicleRoutingSolution) solutionFileIO.read(varFile);
            for (File inputFile : files) {
                HardSoftLongScore score;
                if (inputFile == varFile) {
                    score = variablesSolution.getScore();
                } else {
                    VehicleRoutingSolution inputSolution = (VehicleRoutingSolution) solutionFileIO.read(inputFile);
                    applyVariables(inputSolution, variablesSolution);
                    score = inputSolution.getScore();
                }
                logger.info("    {} (according to {})", score.getSoftScore(), inputFile.getName());
            }
        }
    }

    private void applyVariables(VehicleRoutingSolution inputSolution, VehicleRoutingSolution varSolution) {
        List<Vehicle> inputVehicleList = inputSolution.getVehicleList();
        Map<Long, Vehicle> inputVehicleMap = new LinkedHashMap<>(inputVehicleList.size());
        for (Vehicle vehicle : inputVehicleList) {
            inputVehicleMap.put(vehicle.getId(), vehicle);
        }
        List<Customer> inputCustomerList = inputSolution.getCustomerList();
        Map<Long, Customer> inputCustomerMap = new LinkedHashMap<>(inputCustomerList.size());
        for (Customer customer : inputCustomerList) {
            inputCustomerMap.put(customer.getId(), customer);
        }

        for (Vehicle varVehicle : varSolution.getVehicleList()) {
            Vehicle inputVehicle = inputVehicleMap.get(varVehicle.getId());
            Customer varNext = varVehicle.getNextCustomer();
            inputVehicle.setNextCustomer(varNext == null ? null : inputCustomerMap.get(varNext.getId()));
        }
        for (Customer varCustomer : varSolution.getCustomerList()) {
            Customer inputCustomer = inputCustomerMap.get(varCustomer.getId());
            Standstill varPrevious = varCustomer.getPreviousStandstill();
            inputCustomer.setPreviousStandstill(varPrevious == null ? null :
                    varPrevious instanceof Vehicle ? inputVehicleMap.get(((Vehicle) varPrevious).getId())
                    : inputCustomerMap.get(((Customer) varPrevious).getId()));
            Customer varNext = varCustomer.getNextCustomer();
            inputCustomer.setNextCustomer(varNext == null ? null : inputCustomerMap.get(varNext.getId()));
        }
        try (ScoreDirector<VehicleRoutingSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector()) {
            scoreDirector.setWorkingSolution(inputSolution);
            scoreDirector.calculateScore();
        }
    }

}
