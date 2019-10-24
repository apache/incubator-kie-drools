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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class representing the content model of the grid, to be extended by concrete implementations
 */
public abstract class AbstractScesimModel<T extends AbstractScesimData> {

    /**
     * Describes structure of the simulation
     */
    protected final SimulationDescriptor simulationDescriptor = new SimulationDescriptor();

    /**
     * Contains list of scenarios to test
     */
    protected final List<T> scesimData = new LinkedList<>();

    public abstract AbstractScesimModel cloneScesimModel();

    public abstract T addScesimData(int index);

    /**
     * Returns an <b>unmodifiable</b> list wrapping the backed one
     * @return
     */
    public List<T> getUnmodifiableScesimData() {
        return Collections.unmodifiableList(scesimData);
    }

    public void removeScesimDataByIndex(int index) {
        scesimData.remove(index);
    }

    public void removeScesimData(T toRemove) {
        scesimData.remove(toRemove);
    }

    public T getScesimDataByIndex(int index) {
        return scesimData.get(index);
    }

    public T addScesimData() {
        return addScesimData(scesimData.size());
    }

    public void replaceScesimData(int index, T newScesimData) {
        scesimData.set(index, newScesimData);
    }

    public T cloneScesimData(int sourceIndex, int targetIndex) {
        if (sourceIndex < 0 || sourceIndex >= scesimData.size()) {
            throw new IllegalArgumentException(new StringBuilder().append("SourceIndex out of range ").append(sourceIndex).toString());
        }
        if (targetIndex < 0 || targetIndex > scesimData.size()) {
            throw new IllegalArgumentException(new StringBuilder().append("TargetIndex out of range ").append(targetIndex).toString());
        }
        T scesimDataByIndex = getScesimDataByIndex(sourceIndex);
        T clonedScesimData = (T) scesimDataByIndex.cloneScesimData();
        scesimData.add(targetIndex, clonedScesimData);
        return clonedScesimData;
    }

    public void clear() {
        simulationDescriptor.clear();
        clearScesimDatas();
    }

    public void clearScesimDatas() {
        scesimData.clear();
    }

    public void resetErrors() {
        scesimData.forEach(AbstractScesimData::resetErrors);
    }

    public SimulationDescriptor getSimulationDescriptor() {
        return simulationDescriptor;
    }

    public void removeFactMappingByIndex(int index) {
        clearScesimDatas(simulationDescriptor.getFactMappingByIndex(index));
        simulationDescriptor.removeFactMappingByIndex(index);
    }

    public void removeFactMapping(FactMapping toRemove) {
        clearScesimDatas(toRemove);
        simulationDescriptor.removeFactMapping(toRemove);
    }

    private void clearScesimDatas(FactMapping toRemove) {
        scesimData.forEach(e -> e.removeFactMappingValueByIdentifiers(toRemove.getFactIdentifier(), toRemove.getExpressionIdentifier()));
    }
}