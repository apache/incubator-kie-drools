package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class MrBalancePenalty extends AbstractPersistable {

    private MrResource originResource;
    private MrResource targetResource;
    private int multiplicand;
    private int weight;

    @SuppressWarnings("unused")
    MrBalancePenalty() {
    }

    public MrBalancePenalty(MrResource originResource, MrResource targetResource, int multiplicand, int weight) {
        this.originResource = originResource;
        this.targetResource = targetResource;
        this.multiplicand = multiplicand;
        this.weight = weight;
    }

    public MrBalancePenalty(long id, MrResource originResource, MrResource targetResource, int multiplicand, int weight) {
        super(id);
        this.originResource = originResource;
        this.targetResource = targetResource;
        this.multiplicand = multiplicand;
        this.weight = weight;
    }

    public MrResource getOriginResource() {
        return originResource;
    }

    public MrResource getTargetResource() {
        return targetResource;
    }

    public int getMultiplicand() {
        return multiplicand;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
