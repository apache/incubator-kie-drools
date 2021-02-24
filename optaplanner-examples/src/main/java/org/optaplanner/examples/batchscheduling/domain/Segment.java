package org.optaplanner.examples.batchscheduling.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PipeSegment")
public class Segment extends AbstractPersistable {

    private Batch batch;
    private RoutePath routePath;
    private String name;

    //flowRate is in m3/minute (i.e. meter cube per minute)
    private float flowRate;

    private float length;
    private float crossSectionArea;

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoutePath getRoutePath() {
        return routePath;
    }

    public void setRoutePath(RoutePath routePath) {
        this.routePath = routePath;
    }

    public float getFlowRate() {
        return flowRate;
    }

    public void setFlowRate(float flowRate) {
        this.flowRate = flowRate;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getCrossSectionArea() {
        return crossSectionArea;
    }

    public void setCrossSectionArea(float crossSectionArea) {
        this.crossSectionArea = crossSectionArea;
    }
}
