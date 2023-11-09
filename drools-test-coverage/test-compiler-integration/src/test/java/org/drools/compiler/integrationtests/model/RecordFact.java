package org.drools.compiler.integrationtests.model;

import java.math.BigDecimal;

public class RecordFact {

    private RecordKey recordKey;
    private String segment;
    private BigDecimal recordPoint;
    private BigDecimal decomposedPoint;
    private PointKey pointKey;

    public RecordFact(RecordKey recordKey, String segment, BigDecimal recordPoint, BigDecimal decomposedPoint, PointKey pointKey) {
        this.recordKey = recordKey;
        this.segment = segment;
        this.recordPoint = recordPoint;
        this.decomposedPoint = decomposedPoint;
        this.pointKey = pointKey;
    }

    public String getReceiptKey() {
        return recordKey.getReceiptKey();
    }


    public RecordKey getRecordKey() {
        return recordKey;
    }

    public void setRecordKey(RecordKey recordKey) {
        this.recordKey = recordKey;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public BigDecimal getRecordPoint() {
        return recordPoint;
    }

    public void setRecordPoint(BigDecimal recordPoint) {
        this.recordPoint = recordPoint;
    }

    public BigDecimal getDecomposedPoint() {
        return decomposedPoint;
    }

    public void setDecomposedPoint(BigDecimal decomposedPoint) {
        this.decomposedPoint = decomposedPoint;
    }

    public PointKey getPointKey() {
        return pointKey;
    }

    public void setPointKey(PointKey pointKey) {
        this.pointKey = pointKey;
    }

    @Override
    public String toString() {
        return "RecordFact{" +
                ", recordKey=" + recordKey +
                ", segment='" + segment + '\'' +
                ", recordPoint=" + recordPoint +
                ", decomposedPoint=" + decomposedPoint +
                ", pointKey=" + pointKey +
                '}';
    }
}
