package org.drools.compiler.integrationtests.model;

import java.math.BigDecimal;

public class RecordFact {

    private BigDecimal decomposedPoint;
    private int lineNumber;

    public RecordFact(BigDecimal decomposedPoint, int lineNumber) {
        this.decomposedPoint = decomposedPoint;
        this.lineNumber = lineNumber;
    }

    public BigDecimal getDecomposedPoint() {
        return decomposedPoint;
    }

    public void setDecomposedPoint(BigDecimal decomposedPoint) {
        this.decomposedPoint = decomposedPoint;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "RecordFact{" +
                ", decomposedPoint=" + decomposedPoint +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
