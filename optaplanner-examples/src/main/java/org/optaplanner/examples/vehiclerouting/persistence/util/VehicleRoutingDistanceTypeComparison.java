package org.optaplanner.examples.vehiclerouting.persistence.util;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.vehiclerouting.app.VehicleRoutingApp;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingSolutionFileIO;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class VehicleRoutingDistanceTypeComparison extends LoggingMain {

    private final SolutionManager<VehicleRoutingSolution, HardSoftLongScore> solutionManager;

    public static void main(String[] args) {
        new VehicleRoutingDistanceTypeComparison().compare(
                "solved/tmp-p-belgium-n50-k10.json",
                "solved/tmp-p-belgium-road-km-n50-k10.json",
                "solved/tmp-p-belgium-road-time-n50-k10.json");
    }

    protected final File dataDir;
    protected final SolutionFileIO<VehicleRoutingSolution> solutionFileIO;

    public VehicleRoutingDistanceTypeComparison() {
        dataDir = CommonApp.determineDataDir(VehicleRoutingApp.DATA_DIR_NAME);
        solutionFileIO = new VehicleRoutingSolutionFileIO();
        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory
                .createFromXmlResource(VehicleRoutingApp.SOLVER_CONFIG);
        solutionManager = SolutionManager.create(solverFactory);
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
            VehicleRoutingSolution variablesSolution = solutionFileIO.read(varFile);
            for (File inputFile : files) {
                HardSoftLongScore score;
                if (inputFile == varFile) {
                    score = variablesSolution.getScore();
                } else {
                    VehicleRoutingSolution inputSolution = solutionFileIO.read(inputFile);
                    applyVariables(inputSolution, variablesSolution);
                    score = inputSolution.getScore();
                }
                logger.info("    {} (according to {})", score.softScore(), inputFile.getName());
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
        for (Customer varCustomer : varSolution.getCustomerList()) {
            Customer inputCustomer = inputCustomerMap.get(varCustomer.getId());
            inputCustomer.setPreviousCustomer(findInputObjectById(inputCustomerMap, varCustomer.getPreviousCustomer()));
            inputCustomer.setNextCustomer(findInputObjectById(inputCustomerMap, varCustomer.getNextCustomer()));
            inputCustomer.setVehicle(findInputObjectById(inputVehicleMap, varCustomer.getVehicle()));
        }
        solutionManager.update(inputSolution);
    }

    private <T extends AbstractPersistable> T findInputObjectById(Map<Long, T> inputMap, T varObject) {
        return varObject == null ? null : inputMap.get(varObject.getId());
    }

}
