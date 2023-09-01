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