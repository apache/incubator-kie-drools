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

import java.util.Objects;

/**
 * Tuple with <code>AbstractScesimData</code> and its index
 */
public abstract class ScesimDataWithIndex<T extends AbstractScesimData> {

    protected T scesimData;
    protected int index;

    public ScesimDataWithIndex() {
        // CDI
    }

    public ScesimDataWithIndex(int index, T scesimData) {
        this.scesimData = scesimData;
        this.index = index;
    }

    public T getScesimData() {
        return scesimData;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScesimDataWithIndex that = (ScesimDataWithIndex) o;
        return index == that.index &&
                Objects.equals(scesimData, that.scesimData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scesimData, index);
    }
}