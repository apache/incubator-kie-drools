package org.drools.compiler.integrationtests.model;

import java.math.BigDecimal;

public class Item {

    private String decomposedPointFlag;
    private BigDecimal segmentPoint;
    private String segment;

    public Item(String decomposedPointFlag, BigDecimal segmentPoint, String segment) {
        this.decomposedPointFlag = decomposedPointFlag;
        this.segmentPoint = segmentPoint;
        this.segment = segment;
    }

    public String getDecomposedPointFlag() {
        return decomposedPointFlag;
    }

    public void setDecomposedPointFlag(String decomposedPointFlag) {
        this.decomposedPointFlag = decomposedPointFlag;
    }

    public BigDecimal getSegmentPoint() {
        return segmentPoint;
    }

    public void setSegmentPoint(BigDecimal segmentPoint) {
        this.segmentPoint = segmentPoint;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    @Override
    public String toString() {
        return "Item{" +
                "pointFlag='" + decomposedPointFlag + '\'' +
                ", segmentPoint=" + segmentPoint +
                ", segment='" + segment + '\'' +
                '}';
    }
}
