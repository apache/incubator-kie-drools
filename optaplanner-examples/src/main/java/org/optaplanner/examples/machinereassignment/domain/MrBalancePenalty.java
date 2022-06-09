package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MrBalancePenalty")
public class MrBalancePenalty extends AbstractPersistable {

    private MrResource originResource;
    private MrResource targetResource;
    private int multiplicand;
    private int weight;

    public MrBalancePenalty() {
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

    public void setOriginResource(MrResource originResource) {
        this.originResource = originResource;
    }

    public MrResource getTargetResource() {
        return targetResource;
    }

    public void setTargetResource(MrResource targetResource) {
        this.targetResource = targetResource;
    }

    public int getMultiplicand() {
        return multiplicand;
    }

    public void setMultiplicand(int multiplicand) {
        this.multiplicand = multiplicand;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
