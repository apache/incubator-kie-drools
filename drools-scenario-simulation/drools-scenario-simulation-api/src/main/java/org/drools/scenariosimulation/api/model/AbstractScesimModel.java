/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.scenariosimulation.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Abstract class representing the content model of the grid, to be extended by concrete implementations
 */
public abstract class AbstractScesimModel<T extends AbstractScesimData> {

    /**
     * Describes structure of the simulation
     */
    protected final ScesimModelDescriptor scesimModelDescriptor = new ScesimModelDescriptor();

    /**
     * Contains list of scenarios to test
     */
    protected final List<T> scesimData = new LinkedList<>();

    public abstract AbstractScesimModel<T> cloneModel();

    public abstract T addData(int index);

    /**
     * Returns an <b>unmodifiable</b> list wrapping the backed one
     * @return
     */
    public List<T> getUnmodifiableData() {
        return Collections.unmodifiableList(scesimData);
    }

    public void removeDataByIndex(int index) {
        scesimData.remove(index);
    }

    public void removeData(T toRemove) {
        scesimData.remove(toRemove);
    }

    public T getDataByIndex(int index) {
        return scesimData.get(index);
    }

    public T addData() {
        return addData(scesimData.size());
    }

    public void replaceData(int index, T newScesimData) {
        scesimData.set(index, newScesimData);
    }

    public T cloneData(int sourceIndex, int targetIndex) {
        if (sourceIndex < 0 || sourceIndex >= scesimData.size()) {
            throw new IndexOutOfBoundsException(new StringBuilder().append("SourceIndex out of range ").append(sourceIndex).toString());
        }
        if (targetIndex < 0 || targetIndex > scesimData.size()) {
            throw new IndexOutOfBoundsException(new StringBuilder().append("TargetIndex out of range ").append(targetIndex).toString());
        }
        T scesimDataByIndex = getDataByIndex(sourceIndex);
        T clonedScesimData = (T) scesimDataByIndex.cloneInstance();
        scesimData.add(targetIndex, clonedScesimData);
        return clonedScesimData;
    }

    public void clear() {
        scesimModelDescriptor.clear();
        clearDatas();
    }

    public void clearDatas() {
        scesimData.clear();
    }

    public void resetErrors() {
        scesimData.forEach(AbstractScesimData::resetErrors);
    }

    public ScesimModelDescriptor getScesimModelDescriptor() {
        return scesimModelDescriptor;
    }

    public void removeFactMappingByIndex(int index) {
        clearDatas(scesimModelDescriptor.getFactMappingByIndex(index));
        scesimModelDescriptor.removeFactMappingByIndex(index);
    }

    public void removeFactMapping(FactMapping toRemove) {
        clearDatas(toRemove);
        scesimModelDescriptor.removeFactMapping(toRemove);
    }

    protected void clearDatas(FactMapping toRemove) {
        scesimData.forEach(e -> e.removeFactMappingValueByIdentifiers(toRemove.getFactIdentifier(), toRemove.getExpressionIdentifier()));
    }

    protected <Z extends ScesimDataWithIndex<T>> List<Z> toScesimDataWithIndex(BiFunction<Integer, T, Z> producer) {
        List<Z> toReturn = new ArrayList<>();
        List<T> data = getUnmodifiableData();
        for (int index = 0; index < data.size(); index += 1) {
            toReturn.add(producer.apply(index + 1, data.get(index)));
        }
        return toReturn;
    }
}