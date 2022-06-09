package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MrResource")
public class MrResource extends AbstractPersistable {

    private int index;
    private boolean transientlyConsumed;
    private int loadCostWeight;

    public MrResource() {
    }

    public MrResource(int index, boolean transientlyConsumed, int loadCostWeight) {
        this.index = index;
        this.transientlyConsumed = transientlyConsumed;
        this.loadCostWeight = loadCostWeight;
    }

    public MrResource(long id, int index, boolean transientlyConsumed, int loadCostWeight) {
        super(id);
        this.index = index;
        this.transientlyConsumed = transientlyConsumed;
        this.loadCostWeight = loadCostWeight;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isTransientlyConsumed() {
        return transientlyConsumed;
    }

    public void setTransientlyConsumed(boolean transientlyConsumed) {
        this.transientlyConsumed = transientlyConsumed;
    }

    public int getLoadCostWeight() {
        return loadCostWeight;
    }

    public void setLoadCostWeight(int loadCostWeight) {
        this.loadCostWeight = loadCostWeight;
    }

}
