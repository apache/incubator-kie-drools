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

enum MrConstraintName {

    MAXIMUM_CAPACITY("maximumCapacity"),
    TRANSIENT_USAGE("transientUsage"),
    SERVICE_CONFLICT("serviceConflict"),
    SERVICE_LOCATION_SPREAD("serviceLocationSpread"),
    SERVICE_DEPENDENCY("serviceDependency"),
    LOAD_COST("loadCost"),
    BALANCE_COST("balanceCost"),
    PROCESS_MOVE_COST("processMoveCost"),
    SERVICE_MOVE_COST("serviceMoveCost"),
    MACHINE_MOVE_COST("machineMoveCost");

    private final String name;

    MrConstraintName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
