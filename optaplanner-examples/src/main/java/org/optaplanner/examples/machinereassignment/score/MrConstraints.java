package org.optaplanner.examples.machinereassignment.score;

public interface MrConstraints {

    String MAXIMUM_CAPACITY = "maximumCapacity";
    String TRANSIENT_USAGE = "transientUsage";
    String SERVICE_CONFLICT = "serviceConflict";
    String SERVICE_LOCATION_SPREAD = "serviceLocationSpread";
    String SERVICE_DEPENDENCY = "serviceDependency";
    String LOAD_COST = "loadCost";
    String BALANCE_COST = "balanceCost";
    String PROCESS_MOVE_COST = "processMoveCost";
    String SERVICE_MOVE_COST = "serviceMoveCost";
    String MACHINE_MOVE_COST = "machineMoveCost";
}
