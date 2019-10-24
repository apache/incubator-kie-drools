/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.api.model;

import java.util.List;

/**
 * Envelop class that wrap the definition of the <b>Background</b> grid and its data
 */
public class Background extends AbstractScesimModel<BackgroundData> {

    @Override
    public BackgroundData addScesimData(int index) {
        if (index < 0 || index > scesimData.size()) {
            throw new IllegalArgumentException(new StringBuilder().append("Index out of range ").append(index).toString());
        }
        BackgroundData backgroundData = new BackgroundData();
        scesimData.add(index, backgroundData);
        return backgroundData;
    }

    //    /**
//     * Describes structure of the simulation
//     */
//    private final SimulationDescriptor simulationDescriptor = new SimulationDescriptor();
//
//
//
//    public SimulationDescriptor getSimulationDescriptor() {
//        return simulationDescriptor;
//    }
//
//
//
//    public void removeFactMappingByIndex(int index) {
//        simulationDescriptor.removeFactMappingByIndex(index);
//    }
//
//    public void removeFactMapping(FactMapping toRemove) {
//        simulationDescriptor.removeFactMapping(toRemove);
//    }
//
//    public void clear() {
//        simulationDescriptor.clear();
//    }

    @Override
    public Background cloneScesimModel() {
        Background toReturn = new Background();
        final List<FactMapping> originalFactMappings = this.simulationDescriptor.getUnmodifiableFactMappings();
        for (int i = 0; i < originalFactMappings.size(); i++) {
            final FactMapping originalFactMapping = originalFactMappings.get(i);
            toReturn.simulationDescriptor.addFactMapping(i, originalFactMapping);
        }
        return toReturn;
    }

}