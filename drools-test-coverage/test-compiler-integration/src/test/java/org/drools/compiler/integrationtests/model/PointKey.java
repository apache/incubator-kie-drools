package org.drools.compiler.integrationtests.model;

public class PointKey {

    private int lastLineNumber;

    public PointKey(int lastLineNumber) {
        this.lastLineNumber = lastLineNumber;
    }

    public int getLastLineNumber() {
        return lastLineNumber;
    }

    public void setLastLineNumber(int lastLineNumber) {
        this.lastLineNumber = lastLineNumber;
    }

    @Override
    public String toString() {
        return "PointKey{" +
                "lastLineNumber='" + lastLineNumber + '\'' +
                '}';
    }
}
