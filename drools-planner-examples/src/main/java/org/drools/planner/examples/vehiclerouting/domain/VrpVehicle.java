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

package org.drools.planner.examples.vehiclerouting.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("VrpVehicle")
public class VrpVehicle extends AbstractPersistable implements VrpAppearance {

    private int capacity;
    private VrpDepot depot;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public VrpDepot getDepot() {
        return depot;
    }

    public void setDepot(VrpDepot depot) {
        this.depot = depot;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public VrpLocation getLocation() {
        return depot.getLocation();
    }

    @Override
    public String toString() {
        return getLocation() + " vehicle-" + id;
    }

}
