package org.drools.rule;

import java.io.Serializable;

public class LineMappings implements Serializable {
    private String className;
    private int    startLine;
    private int    offset;

    public LineMappings(final String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public void setStartLine(final int startLine) {
        this.startLine = startLine;
    }

    public int getStartLine() {
        return this.startLine;
    }

    public void setOffset(final int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return this.offset;
    }

}
