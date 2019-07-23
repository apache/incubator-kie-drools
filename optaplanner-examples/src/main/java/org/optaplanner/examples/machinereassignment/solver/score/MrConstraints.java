/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.machinereassignment.solver.score;

interface MrConstraints {

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
